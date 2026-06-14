package com.sarinsa.dampsoil.common.event;

import com.sarinsa.dampsoil.api.CooldownQueue;
import com.sarinsa.dampsoil.api.impl.DampSoilApi;
import com.sarinsa.dampsoil.common.core.config.Config;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings( "UnstableApiUsage" )
public class GameEventListener {
    
    /**
     * Reduce or completely negate the effects of bone meal on crops.
     */
    
    @SubscribeEvent
    public void onBoneMeal( BonemealEvent event ) {
        if( event.getLevel().isClientSide ) return;
        
        final BlockState state = event.getBlock();
        final RandomSource random = event.getLevel().random;
        
        if( Config.CROPS.GENERAL.boneMealChances.contains( state ) ) {
            if( !Config.CROPS.GENERAL.boneMealChances.rollChance( state, random ) ) {
                event.setResult( Event.Result.ALLOW );
            }
        }
    }
    
    @SubscribeEvent( priority = EventPriority.HIGHEST )
    public void onCropGrow( BlockEvent.CropGrowEvent.Pre event ) {
        final LevelAccessor level = event.getLevel();
        final BlockPos pos = event.getPos();
        
        if( level.getBlockState( pos.below() ).getBlock() instanceof FarmBlock ) {
            int moisture = level.getBlockState( pos.below() ).getValue( FarmBlock.MOISTURE );
            
            // Kill off crops on dry soil
            if( moisture < 1 && Config.CROPS.GENERAL.killDryCrops.get() ) {
                level.setBlock( pos, DSBlocks.DEAD_CROP.get().defaultBlockState(), Block.UPDATE_CLIENTS );
                level.playSound( null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 0.65F, 0.5F );
            }
            // Maybe cancel crop growth
            double growthChance = Config.CROPS.GENERAL.growthChance.get();
            boolean useMoistureMult = Config.CROPS.GENERAL.useMoistureMult.get();
            
            // Maybe reduce chance based on moisture level
            if( useMoistureMult ) {
                growthChance = ((1.0 / FarmBlock.MAX_MOISTURE) * moisture) * growthChance;
            }
            
            if( level.getRandom().nextDouble() > growthChance ) {
                event.setResult( Event.Result.DENY );
            }
        }
    }
    
    /**
     * Cancel out farmland trampling if
     * the farmland has moisture.
     */
    @SubscribeEvent
    public void onFarmlandTrample( BlockEvent.FarmlandTrampleEvent event ) {
        if( Config.IRRIGATION.FARMLAND.denyTrampling.get() ) {
            final BlockState state = event.getLevel().getBlockState( event.getPos() );
            
            // Ensure we are not encountering some modded farmland with
            // different block state properties.
            if( state.getBlock() instanceof FarmBlock && state.hasProperty( FarmBlock.MOISTURE ) ) {
                if( state.getValue( FarmBlock.MOISTURE ) > 0 )
                    event.setCanceled( true );
            }
        }
    }
    
    /**
     * Called when a block is changed by a tool (like tilling or log stripping).
     */
    @SubscribeEvent
    public void onBlockToolModification( BlockEvent.BlockToolModificationEvent event ) {
        // Check if max-moisture tilling is enabled
        if( Config.IRRIGATION.FARMLAND.maxMoistureOnTill.get() && !event.isSimulated() ) {
            if( event.getToolAction() == ToolActions.HOE_TILL ) {
                final BlockState finalState = event.getFinalState();
                
                if( finalState.is( Blocks.DIRT ) || finalState.is( Blocks.GRASS_BLOCK ) ) {
                    event.setFinalState( Blocks.FARMLAND.defaultBlockState().setValue( FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE ) );
                }
            }
        }
    }
    
    /** Called when a player right-clicks an entity. */
    @SubscribeEvent( priority = EventPriority.HIGH )
    public void onPlayerEntityInteract( PlayerInteractEvent.EntityInteract event ) {
        if( !(event.getTarget() instanceof Mob target) ) return;
        
        final ItemStack usedItem = event.getItemStack();
        
        // Check if baby feeding is disabled
        if( target instanceof Animal animal && animal.isFood( event.getItemStack() ) ) {
            if( animal.isBaby() && Config.ANIMALS.GENERAL.denyBabyFeeding.get() ) {
                cancelInteract( event, animal );
            }
        }
        
        if( (target.getType() == EntityType.COW || target.getType() == EntityType.MOOSHROOM) && usedItem.getItem() == Items.BUCKET ) {
            if( !DampSoilApi.INSTANCE.getProduceCooldownManager().canProduce( target, CooldownQueue.FIRST ) ) {
                cancelInteract( event, target );
            }
            else {
                DampSoilApi.INSTANCE.getProduceCooldownManager().setRecentlyProduced( target, CooldownQueue.FIRST, Config.ANIMALS.PRODUCE.cowMilkCooldown );
            }
        }
        
        else if( target.getType() == EntityType.MOOSHROOM && usedItem.getItem() == Items.BOWL ) {
            if( !DampSoilApi.INSTANCE.getProduceCooldownManager().canProduce( target, CooldownQueue.SECOND ) ) {
                cancelInteract( event, target );
            }
            else {
                DampSoilApi.INSTANCE.getProduceCooldownManager().setRecentlyProduced( target, CooldownQueue.SECOND, Config.ANIMALS.PRODUCE.mooshroomStewCooldown );
            }
        }
    }
    
    /** Helper method for canceling player interact events. */
    private void cancelInteract( PlayerInteractEvent event, Mob entity ) {
        entity.playAmbientSound();
        event.setCanceled( true );
        event.setCancellationResult( InteractionResult.PASS );
    }
}
