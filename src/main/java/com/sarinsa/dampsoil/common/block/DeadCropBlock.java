package com.sarinsa.dampsoil.common.block;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

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
        super(BlockBehaviour.Properties.copy(Blocks.DEAD_BUSH));
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return validGround.contains(state.getBlock());
    }
}
