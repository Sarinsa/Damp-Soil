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
     * Checks if farmland should cancel its random tick to prevent it from losing
     * moisture. How likely this is to happen depends on the farmlandDryingRate config option.
     * <br><br>
     * Also checks if we are in a cold biome and 'freezeFarmland' is enabled in the config, in which case
     * we freeze the farmland and let it preserve the moisture it had.
     * <br><br>
     * ALSO also, if we are in a biome with a temperature greater than 1.0, and the block is in direct sunlight,
     * evaporate moisture at normal tick speed.
     */
    public static void onFarmlandRandomTick( BlockState state, RandomSource random, BlockPos pos, ServerLevel level, CallbackInfo ci ) {
        if( level.isClientSide ) return;
        
        int moisture = state.getValue( FarmBlock.MOISTURE );
        
        if( Config.IRRIGATION.FARMLAND.canFreeze.get() ) {
            if( BlockHelper.shouldFreezeFarmlandAt( level, pos ) && moisture > 0 ) {
                level.setBlock( pos, DSBlocks.FROZEN_FARMLAND.get().defaultBlockState().setValue( FrozenFarmBlock.MOISTURE, moisture ), Block.UPDATE_CLIENTS );
                ci.cancel();
                return;
            }
        }
        checkAndVaporize( state, random, pos, level, moisture );
        
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
        checkAndVaporize( state, random, pos, level, state.getValue( FarmBlock.MOISTURE ) );
    }
    
    public static void onCanMate( Animal animal, CallbackInfoReturnable<Boolean> cir ) {
        // noinspection resource
        if( animal.level().isClientSide ) return;
        
        if( !Config.COMPAT.SERENE_SEASONS.seasonalBreeding.get() || !ModList.get().isLoaded( SereneSeasonsHelper.MODID ) )
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
    
    private static void checkAndVaporize( BlockState state, RandomSource random, BlockPos pos, ServerLevel level, int moisture ) {
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
            // Speed things up a bit
            level.scheduleTick( pos, state.getBlock(), Config.IRRIGATION.FARMLAND.vaporizeDelay.get() );
        }
    }
}
