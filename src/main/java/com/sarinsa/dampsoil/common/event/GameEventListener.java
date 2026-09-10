package com.sarinsa.dampsoil.common.event;

import com.sarinsa.dampsoil.api.CooldownQueue;
import com.sarinsa.dampsoil.api.IProduceCooldownManager;
import com.sarinsa.dampsoil.api.impl.DampSoilApi;
import com.sarinsa.dampsoil.common.core.config.Config;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import com.sarinsa.dampsoil.common.util.BlockHelper;
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
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

public class GameEventListener {
    
    /** Called when a player attempts to use bone meal on an applicable block. */
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
    
    /** Called when a sapling, fungus, mushroom or azalea is about to grow into a tree. */
    @SubscribeEvent( priority = EventPriority.HIGHEST )
    public void onSaplingGrowTree( SaplingGrowTreeEvent event ) {
        event.setResult( getGrowthResult( event.getLevel(), event.getPos() ) );
    }
    
    /** Called right before a crop grows. */
    @SubscribeEvent( priority = EventPriority.HIGHEST )
    public void onCropGrow( BlockEvent.CropGrowEvent.Pre event ) {
        event.setResult( getGrowthResult( event.getLevel(), event.getPos() ) );
    }
    
    /** Called when farmland gets trampled by an entity. */
    @SubscribeEvent
    public void onFarmlandTrample( BlockEvent.FarmlandTrampleEvent event ) {
        // Prevent trampling if enabled in config
        if( Config.IRRIGATION.FARMLAND.denyTrampling.get() ) {
            if( event.getLevel().getBlockState( event.getPos() ).getBlock() instanceof FarmBlock ) {
                event.setCanceled( true );
            }
        }
    }
    
    /** Called when a block is changed by a tool (like tilling or log stripping). */
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
        final IProduceCooldownManager cooldownManager = DampSoilApi.INSTANCE.getProduceCooldownManager();
        final EntityType<?> type = target.getType();
        
        // Check cooldowns for vanilla entities
        if( (type == EntityType.COW || type == EntityType.MOOSHROOM) && usedItem.getItem() == Items.BUCKET ) {
            if( !cooldownManager.canProduce( target, CooldownQueue.FIRST ) ) {
                cancelInteract( event, target );
            }
            else {
                cooldownManager.setRecentlyProduced( target, CooldownQueue.FIRST, Config.ANIMALS.PRODUCE.cowMilkCooldown );
            }
        }
        else if( type == EntityType.MOOSHROOM && usedItem.getItem() == Items.BOWL ) {
            if( !cooldownManager.canProduce( target, CooldownQueue.SECOND ) ) {
                cancelInteract( event, target );
            }
            else {
                cooldownManager.setRecentlyProduced( target, CooldownQueue.SECOND, Config.ANIMALS.PRODUCE.mooshroomStewCooldown );
            }
        }
    }
    
    /**
     * Helper method for handling growth events, such as crops growing or saplings growing into trees.
     *
     * @return {@link Event.Result#ALLOW} if growth was allowed, and {@link Event.Result#DENY} otherwise.
     */
    private Event.Result getGrowthResult( LevelAccessor level, BlockPos pos ) {
        final BlockState growth = level.getBlockState( pos );
        final Optional<Integer> moisture = BlockHelper.getMoistureAt( level, pos.below() );
        final int moistureLevel = moisture.orElse( 0 );
        double growthChance = Config.CROPS.GENERAL.growthChances.getOrElse( growth, 1.0 );
        
        // If we are at a block with no moisture and the growth requires it,
        // kill the growth if enabled.
        if( Config.CROPS.GENERAL.diesWithoutWater.contains( growth ) && moistureLevel <= 0 ) {
            level.setBlock( pos, DSBlocks.DEAD_CROP.get().defaultBlockState(), Block.UPDATE_CLIENTS );
            // TODO make "crop dies" sound event
            level.playSound( null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 0.65F, 0.5F );
            return Event.Result.DENY;
        }
        // Maybe reduce growth chance based on moisture level
        if( moisture.isPresent() && Config.CROPS.GENERAL.useMoistureMult.get() ) {
            growthChance = ((1.0 / FarmBlock.MAX_MOISTURE) * moistureLevel) * growthChance;
        }
        return !(level.getRandom().nextDouble() <= growthChance) ? Event.Result.DENY : Event.Result.ALLOW;
    }
    
    /** Helper method for canceling player interact events. */
    private void cancelInteract( PlayerInteractEvent event, Mob entity ) {
        entity.playAmbientSound();
        event.setCanceled( true );
        event.setCancellationResult( InteractionResult.PASS );
    }
}
