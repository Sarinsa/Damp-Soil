package com.sarinsa.dampsoil.common.util;

import com.sarinsa.dampsoil.common.core.config.Config;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;

import java.util.Optional;

public class BlockHelper {
    
    /**
     * @return True if the given block position is at a place
     * where wet farmland should dry out quickly when exposed to direct sunlight.
     */
    public static boolean shouldEvaporateAt( Level level, BlockPos pos ) {
        if( level.getBlockState( pos ).is( Blocks.FARMLAND ) && !FarmBlock.isNearWater( level, pos ) ) {
            int moisture = level.getBlockState( pos ).getValue( FarmBlock.MOISTURE );
            
            if( moisture > 0 ) {
                return Config.IRRIGATION.FARMLAND.vaporizeConditions.getOrElse( new EnvironmentContext( level, pos ), false );
            }
        }
        return false;
    }
    
    /**
     * @return True if the given block position is at a place
     * where wet farmland should freeze.
     */
    public static boolean shouldFreezeFarmlandAt( Level level, BlockPos pos, int moisture ) {
        return moisture > 0 && Config.IRRIGATION.FARMLAND.freezeConditions.getOrElse( new EnvironmentContext( level, pos ), false );
    }
    
    /**
     * @return The moisture level of the block state at the given position,
     * or null if the target block state does not have a valid moisture property.
     */
    public static Optional<Integer> getMoistureAt( LevelAccessor level, BlockPos pos ) {
        if( level.getBlockState( pos ).getBlock() instanceof FarmBlock ) {
            return Optional.of( level.getBlockState( pos ).getValue( FarmBlock.MOISTURE ) );
        }
        else {
            return Optional.empty();
        }
    }
    
    /**
     * @return The moisture level of the block state at the given position,
     * or the given default value if the target block state does not have a valid moisture property.
     */
    public static int getMoistureAtOrElse( LevelAccessor level, BlockPos pos, int defaultValue ) {
        return getMoistureAt( level, pos ).orElse( defaultValue );
    }
}
