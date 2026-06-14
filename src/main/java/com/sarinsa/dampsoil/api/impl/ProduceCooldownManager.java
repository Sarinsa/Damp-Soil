package com.sarinsa.dampsoil.api.impl;

import com.sarinsa.dampsoil.api.CooldownQueue;
import com.sarinsa.dampsoil.api.IProduceCooldownManager;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ProduceCooldownManager implements IProduceCooldownManager {
    
    // NBT keys
    private static final String KEY_MOD_DATA = "DampSoilData";
    private static final String KEY_COOLDOWNS = "ProduceCooldowns";
    
    
    @SubscribeEvent
    public void onLivingTick( LivingEvent.LivingTickEvent event ) {
        if( event.getEntity() instanceof Mob mob ) {
            tickAllCooldowns( mob );
        }
    }
    
    /**
     * @param cooldownQueue A {@link CooldownQueue} representing the cooldown timer you wish to check.
     * @param mob           The animal to check.
     * @return True if the given animal is on cooldown in the specified timer.
     */
    @Override
    public boolean canProduce( Mob mob, CooldownQueue cooldownQueue ) {
        return getCooldownForQueue( cooldownQueue, mob ) <= 0;
    }
    
    /**
     * Puts the given entity on cooldown in the specified cooldown queue. Does nothing on client.
     *
     * @param mob           The animal to put on cooldown.
     * @param cooldownQueue A {@link CooldownQueue} representing a cooldown timer to add a cooldown to.
     *                      Which type to use is entirely up to you. You can choose between 3 different cooldown timers.
     *                      See {@link CooldownQueue} for more information.
     * @param cooldown      The amount of cooldown ticks.
     */
    @Override
    public void setRecentlyProduced( Mob mob, CooldownQueue cooldownQueue, int cooldown ) {
        setCooldownForQueue( cooldownQueue, mob, cooldown );
    }
    
    /**
     * Puts the given entity on cooldown in the specified cooldown queue. Does nothing on client.
     *
     * @param mob           The animal to put on cooldown.
     * @param cooldownQueue A {@link CooldownQueue} representing a cooldown timer to add a cooldown to.
     *                      Which type to use is completely optional. You can choose between 3 different cooldown timers.
     *                      See {@link CooldownQueue} for more information.
     * @param cooldownField An integer random-range config field to sample a cooldown from.
     */
    @Override
    public void setRecentlyProduced( Mob mob, CooldownQueue cooldownQueue, IntField.RandomRange cooldownField ) {
        setRecentlyProduced( mob, cooldownQueue, cooldownField.next( mob.getRandom() ) );
    }
    
    /** Ticks all current produce cooldowns on the given mob. */
    private void tickAllCooldowns( Mob mob ) {
        // If this mob has never been put on cooldown before, don't bother ticking
        if( !mob.getPersistentData().contains( KEY_MOD_DATA ) ) return;
        
        final CompoundTag modData = mob.getPersistentData().getCompound( KEY_MOD_DATA );
        final CompoundTag cooldownsTag = modData.getCompound( KEY_COOLDOWNS );
        
        for( CooldownQueue queue : CooldownQueue.values() ) {
            CompoundTag cooldown = cooldownsTag.getCompound( queue.getTagName() );
            int currentValue = cooldown.getInt( "Cooldown" );
            
            if( currentValue > 0 ) {
                cooldown.putInt( "Cooldown", --currentValue );
            }
        }
    }
    
    /**
     * Puts the given mob on a cooldown in the specified cooldown queue.
     *
     * @param duration The duration of the cooldown, in ticks.
     */
    private void setCooldownForQueue( CooldownQueue queue, Mob mob, int duration ) {
        final CompoundTag modData = NBTHelper.getOrCreateCompound( mob.getPersistentData(), KEY_MOD_DATA );
        final CompoundTag cooldownsTag = NBTHelper.getOrCreateCompound( modData, KEY_COOLDOWNS );
        final CompoundTag cooldown = NBTHelper.getOrCreateCompound( cooldownsTag, queue.getTagName() );
        
        cooldown.putInt( "Cooldown", duration );
    }
    
    /** @return The current cooldown in the specified queue for the given entity. */
    private int getCooldownForQueue( CooldownQueue queue, Mob mob ) {
        final CompoundTag persistentData = mob.getPersistentData();
        
        if( NBTHelper.containsCompound( persistentData, KEY_MOD_DATA ) ) {
            final CompoundTag modData = persistentData.getCompound( KEY_MOD_DATA );
            
            if( NBTHelper.containsCompound( modData, KEY_COOLDOWNS ) ) {
                final CompoundTag cooldownsTag = modData.getCompound( KEY_COOLDOWNS );
                final CompoundTag cooldown = cooldownsTag.getCompound( queue.getTagName() );
                
                return cooldown.getInt( "Cooldown" );
            }
        }
        return 0;
    }
}
