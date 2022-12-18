package com.sarinsa.dampsoil.common.block;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.tile.SprinklerTileEntity;
import com.sarinsa.dampsoil.common.util.DirectionHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class SprinklerBlock extends Block {

    public static final BooleanProperty SPRINKLING = BooleanProperty.create("sprinkling");
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);

    private final Supplier<Integer> radius;

    public SprinklerBlock(Supplier<Integer> radiusSupplier) {
        super(AbstractBlock.Properties.of(Material.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops()
                .harvestTool(ToolType.PICKAXE));

        registerDefaultState(stateDefinition.any()
                .setValue(SPRINKLING, false)
                .setValue(ACTIVATED, false)
                .setValue(FACING, Direction.UP));
        this.radius = radiusSupplier;
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction dir = context.getPlayer() == null ? Direction.UP : DirectionHelper.getVerticalLookingDir(context.getPlayer());
        return defaultBlockState().setValue(FACING, dir.getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClientSide) {
            // obstruction check
            boolean obstructed = false;

            if (state.getValue(FACING) == Direction.UP) {
                if (!world.getBlockState(pos.above()).isAir())
                    obstructed = true;
            }
            else {
                if (!world.getBlockState(pos.below()).isAir())
                    obstructed = true;
            }
            int activationTime = DSCommonConfig.COMMON.sprinklerActivationTime.get();

            // activation
            if (world.hasNeighborSignal(pos) && !obstructed) {
                if (!state.getValue(ACTIVATED)) {
                    world.setBlock(pos, state.setValue(SPRINKLING, true).setValue(ACTIVATED, true), 2);

                    if (activationTime > 0) {
                        world.getBlockTicks().scheduleTick(pos, this, activationTime * 20);
                    }
                    else {
                        world.setBlock(pos, state.setValue(SPRINKLING, true).setValue(ACTIVATED, true), 2);
                    }
                }
            }
            else {
                if (activationTime > 0) {
                    world.setBlock(pos, state.setValue(SPRINKLING, state.getValue(SPRINKLING)).setValue(ACTIVATED, false), 2);
                }
                else {
                    world.setBlock(pos, state.setValue(SPRINKLING, false).setValue(ACTIVATED, false), 2);
                }
            }
            // but not when obstructed
            if (obstructed) {
                world.setBlock(pos, state.setValue(SPRINKLING, false).setValue(ACTIVATED, false), 2);
            }
        }
    }

    public final Supplier<Integer> getRadius() {
        return radius;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlock(pos, state.setValue(SPRINKLING, false), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(SPRINKLING, ACTIVATED, FACING);
    }
}
