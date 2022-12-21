package com.sarinsa.dampsoil.common.event;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

import static com.sarinsa.dampsoil.common.core.config.DSCommonConfig.COMMON;

public class DSEventListener {

    /**
     * Reduce or completely negate the effects of bone meal on crops.
     */
    @SubscribeEvent
    public void onCropGrow(BonemealEvent event) {
        Block block = event.getBlock().getBlock();
        RandomSource random = event.getLevel().random;

        if (block instanceof CropBlock) {
            final int chance = COMMON.boneMealEfficiency.get();

            if (chance <= 0 || random.nextDouble() > 1.0 / ((float) chance)) {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    /**
     * Cancel out farmland trampling if
     * the farmland has moisture.
     */
    @SubscribeEvent
    public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (COMMON.disableTrampling.get()) {
            BlockState state = event.getLevel().getBlockState(event.getPos());

            // Ensure we are not encountering some modded farmland with
            // different block state properties.
            if (state.getBlock() instanceof FarmBlock && state.hasProperty(FarmBlock.MOISTURE)) {
                if (state.getValue(FarmBlock.MOISTURE) > 0)
                    event.setCanceled(true);
            }
        }
    }

    /**
     * Make tilled farmland start out at max moisture level.
     */
    @SubscribeEvent
    public void onBlockToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (!event.isSimulated()) {
            if (event.getToolAction() == ToolActions.HOE_TILL) {
                BlockState finalState = event.getFinalState();

                if (finalState.is(Blocks.DIRT) || finalState.is(Blocks.GRASS_BLOCK)) {
                    event.setFinalState(Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE));
                }
            }
        }
    }
}
