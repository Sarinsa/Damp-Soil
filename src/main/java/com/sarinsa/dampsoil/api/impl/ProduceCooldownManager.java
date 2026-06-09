package com.sarinsa.dampsoil.api.impl;

import com.sarinsa.dampsoil.api.CooldownQueue;
import com.sarinsa.dampsoil.api.IProduceCooldownManager;
import com.sarinsa.dampsoil.common.tag.DSEntityTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ProduceCooldownManager implements IProduceCooldownManager {
    
    private static final String MOD_DATA_KEY = "DampSoilData";
    private static final String COOLDOWNS_KEY = "ProduceCooldowns";
    
    
    @SubscribeEvent
    public void tickEntity( LivingEvent.LivingTickEvent event ) {
        if( !event.getEntity().level().isClientSide && event.getEntity().getType().is( DSEntityTags.COOLDOWNABLE_MOBS ) ) {
            tickAllCooldowns( event.getEntity() );
        }
    }
    
    /**
     * @param cooldownQueue A {@link CooldownQueue} representing the cooldown timer you wish to check.
     * @param entity        The animal to check.
     * @return True if the given animal is on cooldown in the specified timer. Always returns false on client.
     */
    @Override
    @SuppressWarnings( "resource" )
    public boolean canProduce( LivingEntity entity, CooldownQueue cooldownQueue ) {
        if( entity == null || cooldownQueue == null )
            return true;
        if( !entity.getType().is( DSEntityTags.COOLDOWNABLE_MOBS ) )
            return true;
        if( entity.level().isClientSide ) return false;
        
        return getCooldownForQueue( cooldownQueue, entity ) <= 0;
    }
    
    /**
     * Puts the given entity on cooldown in the specified cooldown queue. Does nothing on client.
     *
     * @param entity        The animal to put on cooldown.
     * @param cooldownQueue A {@link CooldownQueue} representing a cooldown timer to add a cooldown to.
     *                      Which type to use is entirely up to you. You can choose between 3 different cooldown timers.
     *                      See {@link CooldownQueue} for more information.
     * @param cooldown      The amount of cooldown ticks.
     */
    @Override
    @SuppressWarnings( "resource" )
    public void setRecentlyProduced( LivingEntity entity, CooldownQueue cooldownQueue, int cooldown ) {
        if( entity == null || cooldownQueue == null )
            return;
        if( !entity.getType().is( DSEntityTags.COOLDOWNABLE_MOBS ) )
            return;
        if( entity.level().isClientSide ) return;
        
        setCooldownForQueue( cooldownQueue, entity, cooldown );
    }
    
    private void tickCooldown( CooldownQueue queue, LivingEntity livingEntity ) {
        final CompoundTag modData = livingEntity.getPersistentData().getCompound( MOD_DATA_KEY );
        final CompoundTag cooldownsTag = modData.getCompound( COOLDOWNS_KEY );
        final CompoundTag cooldown = cooldownsTag.getCompound( queue.getTagName() );
        
        int currentValue = cooldown.getInt( "Cooldown" );
        
        if( currentValue > 0 ) {
            cooldown.putInt( "Cooldown", --currentValue );
        }
    }
    
    private void tickAllCooldowns( LivingEntity livingEntity ) {
        // If this mob has never been put on cooldown before, don't bother ticking
        if( !livingEntity.getPersistentData().contains( MOD_DATA_KEY ) ) return;
        
        final CompoundTag modData = livingEntity.getPersistentData().getCompound( MOD_DATA_KEY );
        final CompoundTag cooldownsTag = modData.getCompound( COOLDOWNS_KEY );
        
        for( CooldownQueue queue : CooldownQueue.values() ) {
            CompoundTag cooldown = cooldownsTag.getCompound( queue.getTagName() );
            int currentValue = cooldown.getInt( "Cooldown" );
            
            if( currentValue > 0 ) {
                cooldown.putInt( "Cooldown", --currentValue );
            }
        }
    }
    
    private void setCooldownForQueue( CooldownQueue queue, LivingEntity livingEntity, int timeTicks ) {
        if( !livingEntity.getPersistentData().contains( MOD_DATA_KEY ) ) {
            final CompoundTag modData = new CompoundTag();
            final CompoundTag cooldownsTag = new CompoundTag();
            final CompoundTag cooldown = new CompoundTag();
            cooldown.putInt( "Cooldown", timeTicks );
            
            cooldownsTag.put( queue.getTagName(), cooldown );
            modData.put( COOLDOWNS_KEY, cooldownsTag );
            livingEntity.getPersistentData().put( MOD_DATA_KEY, modData );
        }
        else {
            final CompoundTag modData = livingEntity.getPersistentData().getCompound( MOD_DATA_KEY );
            final CompoundTag cooldownsTag = modData.getCompound( COOLDOWNS_KEY );
            final CompoundTag cooldown = cooldownsTag.getCompound( queue.getTagName() );
            
            cooldown.putInt( "Cooldown", timeTicks );
        }
    }
    
    private int getCooldownForQueue( CooldownQueue queue, LivingEntity livingEntity ) {
        CompoundTag persistentData = livingEntity.getPersistentData();
        
        if( persistentData.contains( MOD_DATA_KEY, Tag.TAG_COMPOUND ) ) {
            final CompoundTag modData = persistentData.getCompound( MOD_DATA_KEY );
            
            if( modData.contains( COOLDOWNS_KEY, Tag.TAG_COMPOUND ) ) {
                final CompoundTag cooldownsTag = modData.getCompound( COOLDOWNS_KEY );
                final CompoundTag cooldown = cooldownsTag.getCompound( queue.getTagName() );
                
                return cooldown.getInt( "Cooldown" );
            }
        }
        return 0;
    }
}
