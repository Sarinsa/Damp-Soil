package com.sarinsa.dampsoil.api;

import com.sarinsa.dampsoil.common.util.ObjHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Helper class for registering "sprinkle results".<br>
 * <br>
 * With this you can register blocks that can be affected by the sprinkler
 * when it is active.
 */
public final class SprinkleResults {
    
    private static final Map<Block, SprinkleResult> sprinkleResults = new HashMap<>();
    
    
    // Default logic for vanilla farmland. This can be overridden if desired.
    static {
        registerResult( Blocks.FARMLAND, ( level, pos, original ) -> Blocks.FARMLAND.defaultBlockState().setValue( FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE ) );
    }
    
    
    /**
     * Registers a SprinkleResult that will be associated with the given block.
     * <br><br>
     * If a SprinkleResult already exists for the block, it will be replaced.
     */
    public static void registerResult( Supplier<? extends Block> blockSupplier, SprinkleResult result ) {
        registerResult( blockSupplier.get(), result );
    }
    
    /**
     * Registers a SprinkleResult that will be associated with the given block.
     * <br><br>
     * If a SprinkleResult already exists for the block, it will be replaced.
     */
    public static void registerResult( Block block, SprinkleResult result ) {
        ObjHelper.nonnull( block, result );
        sprinkleResults.put( block, result );
    }
    
    /**
     * @return The {@link SprinkleResult} associated with the given block,
     * if it exists in the result map.
     */
    @Nullable
    public static SprinkleResult get( Block block ) {
        if( block == null )
            return null;
        
        return sprinkleResults.getOrDefault( block, null );
    }
    
    
    @FunctionalInterface
    public interface SprinkleResult {
        /**
         * The sprinkler will look for nearby blocks that can be sprinkled when it is active.
         * It will first check if the block it is trying to sprinkle has a SprinkleResult associated with
         * it, and if it does, this method is called to fetch the new BlockState for the block being sprinkled.<br>
         * <br>
         *
         * @param pos      The world position of the block being sprinkled.
         * @param original The original/current state of the block being sprinkled.
         * @return The new BlockState for the block that has been sprinkled.
         */
        BlockState getState( Level level, BlockPos pos, BlockState original );
    }
    
    // Utility class
    private SprinkleResults() { }
}
