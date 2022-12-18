package com.sarinsa.dampsoil.common.event;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
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
        Random random = event.getWorld().random;

        if (block instanceof CropsBlock) {
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
            BlockState state = event.getWorld().getBlockState(event.getPos());

            // Ensure we are not encountering some modded farmland with
            // different block state properties.
            if (state.getBlock() instanceof FarmlandBlock && state.hasProperty(FarmlandBlock.MOISTURE)) {
                if (state.getValue(FarmlandBlock.MOISTURE) > 0)
                    event.setCanceled(true);
            }
        }
    }
}
