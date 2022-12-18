package com.sarinsa.dampsoil.common.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.sarinsa.dampsoil.common.core.config.DSCommonConfig.COMMON;

public class DSEventListener {

    @SubscribeEvent
    public void onCropGrow(BonemealEvent event) {
        Block block = event.getBlock().getBlock();

        if (block instanceof CropsBlock) {
            if (COMMON.boneMealReductor.get() <= 0 || event.getWorld().getRandom().nextDouble() > 1.0 / ((float) COMMON.boneMealReductor.get())) {
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
        BlockState state = event.getWorld().getBlockState(event.getPos());

        // Ensure we are not encountering some modded farmland with
        // different block state properties.
        if (state.getBlock() instanceof FarmlandBlock && state.hasProperty(FarmlandBlock.MOISTURE)) {
            if (state.getValue(FarmlandBlock.MOISTURE) > 0)
                event.setCanceled(true);
        }
    }
}
