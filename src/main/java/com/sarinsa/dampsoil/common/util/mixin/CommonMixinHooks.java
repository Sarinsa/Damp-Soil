package com.sarinsa.dampsoil.common.util.mixin;

import com.sarinsa.dampsoil.common.block.FrozenFarmBlock;
import com.sarinsa.dampsoil.common.core.config.Config;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.mixin.FarmBlockMixin;
import com.sarinsa.dampsoil.common.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings( "JavadocReference" )
public class CommonMixinHooks {
    
    
    /**
     * Called from {@link FarmBlockMixin#redirectIsWaterNearby(BlockPos, BlockPos, LevelReader, BlockPos)}<br>
     * <br>
     *
     * @return an Iterable containing the BlockPos bounds to check for water around farmland.
     */
    public static Iterable<BlockPos> getFarmlandCheckBounds( BlockPos origin ) {
        final int waterRange = Config.IRRIGATION.FARMLAND.waterRange.get();
        
        if( waterRange < 1 ) return List.of( origin );
        
        return BlockPos.betweenClosed( origin.offset( -waterRange, 0, -waterRange ), origin.offset( waterRange, 1, waterRange ) );
    }
    
    /**
     * Called from {@link FarmBlockMixin#onRandomTick(BlockState, ServerLevel, BlockPos, RandomSource, CallbackInfo)}<br>
     * <br>
     * Checks if farmland should cancel its random tick to prevent it from losing
     * moisture. How likely this is to happen depends on the farmlandDryingRate config option.<br>
     * <br>
     * Also checks if we are in a cold biome and 'freezeFarmland' is enabled in the config, in which case
     * we freeze the farmland and let it preserve the moisture it had.<br>
     * <br>
     * ALSO also, if we are in a biome with a temperature greater than 1.0, and the block is in direct sunlight,
     * evaporate moisture at normal tick speed.
     */
    public static void onFarmlandRandomTick( BlockState state, RandomSource random, BlockPos pos, ServerLevel level, CallbackInfo ci ) {
        int moisture = state.getValue( FarmBlock.MOISTURE );
        
        if( Config.IRRIGATION.FARMLAND.canFreeze.get() ) {
            if( BlockHelper.shouldFreezeFarmlandAt( level, pos ) ) {
                level.setBlock( pos, DSBlocks.FROZEN_FARMLAND.get().defaultBlockState().setValue( FrozenFarmBlock.MOISTURE, moisture ), 2 );
                ci.cancel();
                return;
            }
        }
        checkAndVaporize( state, random, pos, level, moisture );
        
        if( moisture > 0 && !Config.IRRIGATION.FARMLAND.dryingChance.rollChance( random ) )
            ci.cancel();
    }
    
    @SuppressWarnings( "unused" )
    public static void onFarmlandTick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci ) {
        checkAndVaporize( state, random, pos, level, state.getValue( FarmBlock.MOISTURE ) );
    }
    
    private static void checkAndVaporize( BlockState state, RandomSource random, BlockPos pos, ServerLevel level, int moisture ) {
        if( Config.IRRIGATION.FARMLAND.vaporizeChance.rollChance( random ) ) {
            if( BlockHelper.shouldEvaporateAt( level, pos ) ) {
                level.setBlock( pos, Blocks.FARMLAND.defaultBlockState().setValue( FarmBlock.MOISTURE, --moisture ), 2 );
                
                for( int i = 0; i < 5; i++ ) {
                    level.sendParticles(
                            DSParticles.WATER_VAPOR.get(),
                            pos.getX() + random.nextDouble(),
                            pos.getY() + 1.1D,
                            pos.getZ() + random.nextDouble(),
                            1,
                            0.0D,
                            0.01D,
                            0.0D,
                            0.02D
                    );
                }
                // Speed things up a bit
                level.scheduleTick( pos, state.getBlock(), 30 );
            }
        }
    }
}
