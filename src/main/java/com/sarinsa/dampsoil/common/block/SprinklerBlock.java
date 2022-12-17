package com.sarinsa.dampsoil.common.block;

import com.sarinsa.dampsoil.common.tile.SprinklerTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class SprinklerBlock extends Block {

    public static final BooleanProperty SPRINKLING = BooleanProperty.create("sprinkling");
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public SprinklerBlock() {
        super(AbstractBlock.Properties.of(Material.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
                .harvestTool(ToolType.PICKAXE));

        registerDefaultState(stateDefinition.any().setValue(SPRINKLING, false).setValue(ACTIVATED, false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SprinklerTileEntity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClientSide) {
            if (world.hasNeighborSignal(pos) && world.getBlockState(pos.above()).isAir()) {
                if (!state.getValue(ACTIVATED)) {
                    world.setBlock(pos, state.setValue(SPRINKLING, true).setValue(ACTIVATED, true), 2);
                    world.getBlockTicks().scheduleTick(pos, this, 300);
                }
            }
            else {
                boolean canSprinkle = world.getBlockState(pos.above()).isAir(world, pos.above()) && state.getValue(SPRINKLING);
                world.setBlock(pos, state.setValue(SPRINKLING, canSprinkle).setValue(ACTIVATED, false), 2);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlock(pos, state.setValue(SPRINKLING, false), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(SPRINKLING, ACTIVATED);
    }
}
