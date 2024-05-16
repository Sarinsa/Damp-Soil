package com.sarinsa.dampsoil.common.event;

import com.sarinsa.dampsoil.api.CooldownQueue;
import com.sarinsa.dampsoil.api.impl.DampSoilApi;
import com.sarinsa.dampsoil.common.core.DampSoil;
import com.sarinsa.dampsoil.common.core.config.DSComGeneralConfig;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.sarinsa.dampsoil.common.core.config.DSComGeneralConfig.CONFIG;

public class DSEventListener {

    /**
     * Reduce or completely negate the effects of bone meal on crops.
     */
    @SubscribeEvent
    public void onCropBonemealed(BonemealEvent event) {
        Block block = event.getBlock().getBlock();
        RandomSource random = event.getLevel().random;

        if (block instanceof CropBlock) {
            final int chance = CONFIG.boneMealEfficiency.get();

            if (chance <= 0 || random.nextDouble() > 1.0 / ((float) chance)) {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        LevelAccessor level = event.getLevel();
        BlockPos pos = event.getPos();

        if (!level.getBlockState(pos).is(BlockTags.CROPS))
            return;

        if (level.getBlockState(pos.below()).getBlock() instanceof FarmBlock) {
            int moisture = level.getBlockState(pos.below()).getValue(FarmBlock.MOISTURE);

            // Kill off crops on dry soil
            if (DSComGeneralConfig.CONFIG.cropsDie.get()) {
                if (moisture < 1) {
                    level.setBlock(pos, DSBlocks.DEAD_CROP.get().defaultBlockState(), 2);
                    level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 0.65F, 0.5F);
                }
            }
            // Maybe cancel crop growth
            double growthRate = 1.0D / DSComGeneralConfig.CONFIG.growthRate.get();
            double moistureGrowthMul = (1.0D / 7.0D) * moisture;

            if (level.getRandom().nextDouble() > (moistureGrowthMul * growthRate)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    /**
     * Cancel out farmland trampling if
     * the farmland has moisture.
     */
    @SubscribeEvent
    public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (CONFIG.disableTrampling.get()) {
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack usedItem = event.getItemStack();

        if (event.getTarget() instanceof Animal animal && animal.isFood(event.getItemStack())) {
            if (animal.isBaby() && !CONFIG.feedAnimalBabies.get()) {
                cancelInteract(event, animal);
            }
        }

        if (event.getTarget() instanceof Cow cow && usedItem.getItem() == Items.BUCKET) {
            if (!DampSoilApi.INSTANCE.getProduceCooldownManager().canProduce(cow, CooldownQueue.FIRST)) {
                cancelInteract(event, cow);
            }
            else {
                DampSoilApi.INSTANCE.getProduceCooldownManager().setRecentlyProduced(cow, CooldownQueue.FIRST, CONFIG.cowMilkCooldown.get());
            }
        }

        else if (event.getTarget() instanceof MushroomCow mushroomCow && usedItem.getItem() == Items.BOWL) {
            if (!DampSoilApi.INSTANCE.getProduceCooldownManager().canProduce(mushroomCow, CooldownQueue.SECOND)) {
                cancelInteract(event, mushroomCow);
            }
            else {
                DampSoilApi.INSTANCE.getProduceCooldownManager().setRecentlyProduced(mushroomCow, CooldownQueue.SECOND, CONFIG.mooshroomStewCooldown.get());
            }
        }
    }

    private void cancelInteract(PlayerInteractEvent event, Mob entity) {
        entity.playAmbientSound();
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.PASS);
    }
}
