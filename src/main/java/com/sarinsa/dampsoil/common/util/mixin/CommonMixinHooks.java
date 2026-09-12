package com.sarinsa.dampsoil.common.util.mixin;

import com.sarinsa.dampsoil.common.block.FrozenFarmBlock;
import com.sarinsa.dampsoil.common.compat.glitchfiend.SeasonRepresentable;
import com.sarinsa.dampsoil.common.compat.glitchfiend.SereneSeasonsHelper;
import com.sarinsa.dampsoil.common.core.config.Config;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.mixin.FarmBlockMixin;
import com.sarinsa.dampsoil.common.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.List;

@SuppressWarnings( "JavadocReference" )
public class CommonMixinHooks {
    
    
    /**
     * Called from {@link FarmBlockMixin#redirect_isWaterNearby(BlockPos, BlockPos, LevelReader, BlockPos)}
     * <br><br>
     *
     * @return an Iterable containing the BlockPos bounds to check for water around farmland.
     */
    public static Iterable<BlockPos> getFarmlandCheckBounds( BlockPos origin ) {
        final int waterRange = Config.IRRIGATION.FARMLAND.waterRange.get();
        
        if( waterRange < 1 ) return List.of( origin );
        
        return BlockPos.betweenClosed( origin.offset( -waterRange, 0, -waterRange ), origin.offset( waterRange, 1, waterRange ) );
    }
    
    /**
     * Called from {@link FarmBlockMixin#inject_randomTick(BlockState, ServerLevel, BlockPos, RandomSource, CallbackInfo)}
     * <br><br>
     * First checks if {@link com.sarinsa.dampsoil.common.core.config.IrrigationConfig.Farmland#freezeConditions freeze conditions} evaluates to true,
     * and freezes the farmland block if so, letting it retain its current moisture value.
     * <br><br>
     * If freezing conditions did not pass, {@link com.sarinsa.dampsoil.common.core.config.IrrigationConfig.Farmland#vaporizeConditions vaporize conditions}
     * are checked next, resulting in the farmland losing moisture rapidly if passed.
     * <br><br>
     * Lastly, checks if the farmland block should cancel its random tick to prevent it from losing
     * moisture naturally. How likely this is to happen depends on the value of the
     * {@link com.sarinsa.dampsoil.common.core.config.IrrigationConfig.Farmland#dryingChance drying chance} config option.
     */
    public static void onFarmlandRandomTick( BlockState state, RandomSource random, BlockPos pos, ServerLevel level, CallbackInfo ci ) {
        if( level.isClientSide ) return;
        
        final int moisture = state.getValue( FarmBlock.MOISTURE );
        
        if( maybeFreeze( pos, level, moisture, ci ) ) return;
        maybeVaporize( state, random, pos, level, moisture );
        
        if( moisture > 0 && !Config.IRRIGATION.FARMLAND.dryingChance.rollChance( random ) )
            ci.cancel();
    }
    
    /**
     * Called from {@link FarmBlockMixin#inject_tick(BlockState, ServerLevel, BlockPos, RandomSource, CallbackInfo)}
     * when a farmland block is running a scheduled tick.
     */
    @SuppressWarnings( "unused" )
    public static void onFarmlandTick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci ) {
        if( level.isClientSide ) return;
        maybeVaporize( state, random, pos, level, state.getValue( FarmBlock.MOISTURE ) );
    }
    
    /**
     * Called from {@link com.sarinsa.dampsoil.common.mixin.AnimalMixin#inject_canMate(Animal, CallbackInfoReturnable)}
     * when an animal entity checks if it can mate.
     */
    public static void onCanMate( Animal animal, CallbackInfoReturnable<Boolean> cir ) {
        // noinspection resource
        if( animal.level().isClientSide ) return;
        
        if( !Config.COMPAT.SERENE_SEASONS.seasonalBreeding.get() || !ModList.get().isLoaded( SereneSeasonsHelper.MOD_ID ) )
            return;
        
        final SeasonRepresentable[] seasons = Config.COMPAT.SERENE_SEASONS.breedingSeasonsList.get( animal );
        
        if( seasons != null ) {
            final Level level = animal.level();
            final Season.SubSeason subSeason = SeasonHelper.getSeasonState( level ).getSubSeason();
            boolean isBreedingSeason = false;
            
            for( SeasonRepresentable season : seasons ) {
                if( season.getName().equals( subSeason.name() ) ) {
                    isBreedingSeason = true;
                    break;
                }
            }
            
            if( !isBreedingSeason ) {
                cir.setReturnValue( false );
            }
        }
    }
    
    private static boolean maybeFreeze( BlockPos pos, ServerLevel level, int moisture, CallbackInfo ci ) {
        if( BlockHelper.shouldFreezeFarmlandAt( level, pos, moisture ) ) {
            level.setBlock( pos, DSBlocks.FROZEN_FARMLAND.get().defaultBlockState().setValue( FrozenFarmBlock.MOISTURE, moisture ), Block.UPDATE_CLIENTS );
            ci.cancel();
            return true;
        }
        return false;
    }
    
    private static void maybeVaporize( BlockState state, RandomSource random, BlockPos pos, ServerLevel level, int moisture ) {
        if( BlockHelper.shouldEvaporateAt( level, pos ) ) {
            level.setBlock( pos, Blocks.FARMLAND.defaultBlockState().setValue( FarmBlock.MOISTURE, --moisture ), Block.UPDATE_CLIENTS );
            
            for( int i = 0; i < 5; i++ ) {
                level.sendParticles(
                        DSParticles.WATER_VAPOR.get(),
                        pos.getX() + random.nextDouble(),
                        pos.getY() + 1.1,
                        pos.getZ() + random.nextDouble(),
                        1,
                        0.0,
                        0.01,
                        0.0,
                        0.02
                );
            }
            // Schedule next tick to evaporate
            level.scheduleTick( pos, state.getBlock(), Config.IRRIGATION.FARMLAND.vaporizeDelay.get() );
        }
    }
}
