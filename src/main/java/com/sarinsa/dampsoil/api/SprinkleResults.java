package com.sarinsa.dampsoil.api;

import com.sarinsa.dampsoil.common.util.ObjHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Helper class for registering sprinkle results.
 * A sprinkle result is essentially a block state provider
 * that is associated with a specific block, used by sprinklers
 * to determine what block state to replace sprinkled blocks with.
 * For example, any farmland block state will be replaced with the farmland block's
 * max moisture state by default, as seen below.
 * <br><br>
 * Use this class' registration methods to register sprinkle results.
 */
public final class SprinkleResults {
    
    private static final Map<Block, Result> SPRINKLE_RESULTS = new HashMap<>();
    
    
    // Default logic for vanilla farmland. This can be overridden if desired.
    @ApiStatus.Internal
    public static void registerDefault() {
        registerResult( Blocks.FARMLAND, ( level, pos, original ) -> Blocks.FARMLAND.defaultBlockState().setValue( FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE ) );
    }
    
    
    /**
     * Registers a Result that will be associated with the given block.
     * <br><br>
     * If a Result already exists for the block, it will be replaced.
     */
    public static void registerResult( Supplier<? extends Block> blockSupplier, Result result ) {
        registerResult( blockSupplier.get(), result );
    }
    
    /**
     * Registers a Result that will be associated with the given block.
     * <br><br>
     * If a Result already exists for the block, it will be replaced.
     */
    public static void registerResult( Block block, Result result ) {
        ObjHelper.nonnull( block, result );
        SPRINKLE_RESULTS.put( block, result );
    }
    
    /**
     * @return The {@link Result} associated with the given block,
     * if it exists in the result map.
     */
    @Nullable
    public static Result get( Block block ) {
        return SPRINKLE_RESULTS.getOrDefault( block, null );
    }
    
    
    @FunctionalInterface
    public interface Result {
        
        /**
         * The sprinkler will look for nearby blocks that can be sprinkled when it is active.
         * It will first check if the block it is trying to sprinkle has a {@code Result} associated with
         * it, and if it does, this method is called to get the block state to replace the sprinkled block with.
         *
         * @param pos      The world position of the block being sprinkled.
         * @param original The original/current state of the block being sprinkled.
         * @return A replacement block state for the block that has been sprinkled.
         */
        BlockState getState( Level level, BlockPos pos, BlockState original );
    }
    
    // Utility class
    private SprinkleResults() { }
}
