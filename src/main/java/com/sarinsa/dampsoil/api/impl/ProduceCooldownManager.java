package com.sarinsa.dampsoil.api.impl;

import com.sarinsa.dampsoil.api.CooldownQueue;
import com.sarinsa.dampsoil.api.IProduceCooldownManager;
import com.sarinsa.dampsoil.common.tag.DSEntityTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.checkerframework.checker.units.qual.C;

public class ProduceCooldownManager implements IProduceCooldownManager {

    private static final String MOD_DATA_KEY = "DampSoilData";
    private static final String COOLDOWNS_KEY = "ProduceCooldowns";



    @SubscribeEvent
    public void tickEntity(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity().getType().is(DSEntityTags.COOLDOWNABLE_MOBS)) {
            tickAllCooldowns(event.getEntity());
        }
    }

    @Override
    public boolean canProduce(LivingEntity livingEntity, CooldownQueue queue) {
        if (livingEntity == null || queue == null)
            return true;
        if (!livingEntity.getType().is(DSEntityTags.COOLDOWNABLE_MOBS))
            return true;
        if (livingEntity.level().isClientSide) return false;

        return getCooldownForQueue(queue, livingEntity) <= 0;
    }

    @Override
    public void setRecentlyProduced(LivingEntity livingEntity, CooldownQueue queue, int cooldown) {
        if (livingEntity == null || queue == null)
            return;
        if (!livingEntity.getType().is(DSEntityTags.COOLDOWNABLE_MOBS))
            return;
        if (livingEntity.level().isClientSide) return;

        setCooldownForQueue(queue, livingEntity, cooldown);
    }

    private void tickCooldown(CooldownQueue queue, LivingEntity livingEntity) {
        CompoundTag modData = livingEntity.getPersistentData().getCompound(MOD_DATA_KEY);
        CompoundTag cooldownsTag = modData.getCompound(COOLDOWNS_KEY);
        CompoundTag cooldown = cooldownsTag.getCompound(queue.getTagName());

        int currentValue = cooldown.getInt("Cooldown");

        if (currentValue > 0) {
            cooldown.putInt("Cooldown", --currentValue);
        }
    }

    private void tickAllCooldowns(LivingEntity livingEntity) {
        // If this mob has never been put on cooldown before, don't bother ticking
        if (!livingEntity.getPersistentData().contains(MOD_DATA_KEY)) return;

        CompoundTag modData = livingEntity.getPersistentData().getCompound(MOD_DATA_KEY);
        CompoundTag cooldownsTag = modData.getCompound(COOLDOWNS_KEY);

        for (CooldownQueue queue : CooldownQueue.values()) {
            CompoundTag cooldown = cooldownsTag.getCompound(queue.getTagName());
            int currentValue = cooldown.getInt("Cooldown");

            if (currentValue > 0) {
                cooldown.putInt("Cooldown", --currentValue);
            }
        }
    }

    private void setCooldownForQueue(CooldownQueue queue, LivingEntity livingEntity, int timeTicks) {
        if (!livingEntity.getPersistentData().contains(MOD_DATA_KEY)) {
            CompoundTag modData = new CompoundTag();
            CompoundTag cooldownsTag = new CompoundTag();
            CompoundTag cooldown = new CompoundTag();
            cooldown.putInt("Cooldown", timeTicks);

            cooldownsTag.put(queue.getTagName(), cooldown);
            modData.put(COOLDOWNS_KEY, cooldownsTag);
            livingEntity.getPersistentData().put(MOD_DATA_KEY, modData);
        }
        else {
            CompoundTag modData = livingEntity.getPersistentData().getCompound(MOD_DATA_KEY);
            CompoundTag cooldownsTag = modData.getCompound(COOLDOWNS_KEY);
            CompoundTag cooldown = cooldownsTag.getCompound(queue.getTagName());

            cooldown.putInt("Cooldown", timeTicks);
        }
    }

    private int getCooldownForQueue(CooldownQueue queue, LivingEntity livingEntity) {
        CompoundTag persistentData = livingEntity.getPersistentData();

        if (persistentData.contains(MOD_DATA_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag modData = persistentData.getCompound(MOD_DATA_KEY);

            if (modData.contains(COOLDOWNS_KEY, Tag.TAG_COMPOUND)) {
                CompoundTag cooldownsTag = modData.getCompound(COOLDOWNS_KEY);
                CompoundTag cooldown = cooldownsTag.getCompound(queue.getTagName());

                return cooldown.getInt("Cooldown");
            }
        }
        return 0;
    }
}
