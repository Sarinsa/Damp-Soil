package com.sarinsa.dampsoil.common.block;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.tile.SprinklerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SprinklerBlock extends Block implements EntityBlock {

    public static final BooleanProperty SPRINKLING = BooleanProperty.create("sprinkling");
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);

    private final Supplier<Integer> radius;

    public SprinklerBlock(Supplier<Integer> radiusSupplier) {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops());

        registerDefaultState(stateDefinition.any()
                .setValue(SPRINKLING, false)
                .setValue(ACTIVATED, false)
                .setValue(FACING, Direction.UP));
        this.radius = radiusSupplier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SprinklerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, blockState, pos, blockEntity) -> SprinklerBlockEntity.tick(lvl, blockState, pos, (SprinklerBlockEntity) blockEntity);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingVerticalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!level.isClientSide) {
            // obstruction check
            boolean obstructed = false;

            if (state.getValue(FACING) == Direction.UP) {
                if (!level.getBlockState(pos.above()).isAir())
                    obstructed = true;
            }
            else {
                if (!level.getBlockState(pos.below()).isAir())
                    obstructed = true;
            }
            int activationTime = DSCommonConfig.COMMON.sprinklerActivationTime.get();

            // activation
            if (level.hasNeighborSignal(pos) && !obstructed) {
                if (!state.getValue(ACTIVATED)) {
                    level.setBlock(pos, state.setValue(SPRINKLING, true).setValue(ACTIVATED, true), 2);

                    if (activationTime > 0) {
                        level.scheduleTick(pos, this, activationTime * 20);
                    }
                    else {
                        level.setBlock(pos, state.setValue(SPRINKLING, true).setValue(ACTIVATED, true), 2);
                    }
                }
            }
            else {
                if (activationTime > 0) {
                    level.setBlock(pos, state.setValue(SPRINKLING, state.getValue(SPRINKLING)).setValue(ACTIVATED, false), 2);
                }
                else {
                    level.setBlock(pos, state.setValue(SPRINKLING, false).setValue(ACTIVATED, false), 2);
                }
            }
            // but not when obstructed
            if (obstructed) {
                level.setBlock(pos, state.setValue(SPRINKLING, false).setValue(ACTIVATED, false), 2);
            }
        }
    }

    public final Supplier<Integer> getRadius() {
        return radius;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        world.setBlock(pos, state.setValue(SPRINKLING, false), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(SPRINKLING, ACTIVATED, FACING);
    }
}
