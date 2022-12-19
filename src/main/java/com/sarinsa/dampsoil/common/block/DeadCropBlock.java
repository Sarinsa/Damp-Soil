package com.sarinsa.dampsoil.common.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.Arrays;
import java.util.List;

public class DeadCropBlock extends DeadBushBlock {

    private static final List<Block> validGround = Arrays.asList(
            Blocks.DIRT,
            Blocks.COARSE_DIRT,
            Blocks.FARMLAND,
            Blocks.PODZOL
    );

    public DeadCropBlock() {
        super(AbstractBlock.Properties.copy(Blocks.DEAD_BUSH));
    }

    protected boolean mayPlaceOn(BlockState state, IBlockReader world, BlockPos pos) {
        return validGround.contains(state.getBlock());
    }
}
