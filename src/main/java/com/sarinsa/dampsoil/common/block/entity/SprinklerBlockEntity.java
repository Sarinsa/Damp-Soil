package com.sarinsa.dampsoil.common.block.entity;

import com.sarinsa.dampsoil.api.SprinkleResults;
import com.sarinsa.dampsoil.common.block.SprinklerBlock;
import com.sarinsa.dampsoil.common.compat.glitchfiend.ToughAsNailsHelper;
import com.sarinsa.dampsoil.common.core.config.Config;
import com.sarinsa.dampsoil.common.core.registry.DSBlockEntities;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SprinklerBlockEntity extends BlockEntity {
    
    // NBT Keys
    public static final String KEY_STATE = "State";
    
    /** The current state/activity of the sprinkler. This SHOULD never be null. */
    protected State state = State.NONE;
    /** An int supplier providing the AoE radius of this sprinkler. */
    private Supplier<Integer> radiusSupplier;
    
    /** A flag for determining if the sprinkler's fluid tank needs to be synced to the client. */
    protected boolean needSync = false;
    /** How many ticks until next sync check. */
    protected int timeNextSync = 10;
    
    /** Water tank */
    @SuppressWarnings( "deprecation" )
    private final FluidTank waterTank = new FluidTank( 2000, ( fluidStack ) -> fluidStack.getFluid().is( FluidTags.WATER ) ) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            needSync = true;
        }
    };
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of( () -> waterTank );
    
    
    public SprinklerBlockEntity( BlockPos pos, BlockState state ) {
        super( DSBlockEntities.SPRINKLER.get(), pos, state );
    }
    
    /**
     * @return This sprinkler's internal water reservoir.
     * This is only used by the sprinkler if {@link com.sarinsa.dampsoil.common.core.config.CompatConfig.General#sprinklerRequiresPiping}
     * is enabled.
     */
    public FluidTank getWaterTank() {
        return waterTank;
    }
    
    /** @return This sprinkler's AoE radius. */
    public int getRadius() {
        return radiusSupplier.get();
    }
    
    /** Called when the block entity is loaded. */
    @Override
    public void onLoad() {
        if( level != null && getBlockState().getBlock() instanceof SprinklerBlock ) {
            radiusSupplier = ((SprinklerBlock) getBlockState().getBlock()).getRadius();
        }
    }
    
    /** The sprinkler block entity's client-side ticker. */
    public static void clientTick( Level level, BlockPos pos, BlockState state, SprinklerBlockEntity sprinkler ) {
        switch( sprinkler.state ) {
            case SPRINKLE -> splashParticles( sprinkler.getRadius(), level, pos );
            case SPOUT_VAPOR -> vaporParticles( level, pos );
            // Do nothing
            default -> { }
        }
    }
    
    /** The sprinkler block entity's server-side ticker. */
    public static void serverTick( ServerLevel level, BlockPos pos, BlockState state, SprinklerBlockEntity sprinkler ) {
        // Are we sprinklin'? :^)
        if( !state.getValue( SprinklerBlock.SPRINKLING ) ) {
            sprinkler.maybeUpdateState( State.NONE );
        }
        else {
            final int radius = sprinkler.getRadius();
            final boolean requiresPiping = Config.COMPAT.GENERAL.sprinklerRequiresPiping.get();
            
            // Check if we are configured to need piped water
            if( requiresPiping ) {
                FluidTank waterTank = sprinkler.getWaterTank();
                if( waterTank.getFluid().getFluid().getFluidType() == ForgeMod.WATER_TYPE.get() && waterTank.getFluid().getAmount() >= radius ) {
                    waterTank.getFluid().setAmount( waterTank.getFluid().getAmount() - radius );
                    
                    if( --sprinkler.timeNextSync <= 0 ) {
                        if( sprinkler.needSync ) {
                            sprinkler.sendBlockUpdate();
                            sprinkler.needSync = false;
                        }
                        sprinkler.timeNextSync = 10;
                    }
                }
                else {
                    sprinkler.maybeUpdateState( State.NONE );
                    return;
                }
            }
            final RandomSource random = level.getRandom();
            // Make some sad vapor particles if it's too
            // hot in this dimension to sprinkle.
            if( !Config.IRRIGATION.SPRINKLERS.worksInUltrawarm.get() && level.dimensionType().ultraWarm() ) {
                sprinkler.maybeUpdateState( State.SPOUT_VAPOR );
            }
            else {
                sprinkler.maybeUpdateState( State.SPRINKLE );
                
                // Play the sprinkly noise
                if( random.nextDouble() < 0.15D ) {
                    level.playSound(
                            null,
                            pos,
                            SoundEvents.WEATHER_RAIN,
                            SoundSource.BLOCKS,
                            0.5F,
                            1.5F
                    );
                }
                final int loopCount = (int) ((radius + 1) / 1.5D);
                
                for( int i = 0; i < loopCount; ++i ) {
                    int yOffset = 1;
                    if( state.getValue( SprinklerBlock.FACING ) == Direction.DOWN ) yOffset = 4;
                    
                    BlockPos randomOffsetPos = pos.offset( random.nextInt( 1 + 2 * radius ) - radius, random.nextInt( 3 ) - yOffset, random.nextInt( 1 + 2 * radius ) - radius );
                    // Make sure we are not in an unloaded chunk
                    if( !level.isLoaded( randomOffsetPos ) ) continue;
                    
                    BlockState currentState = level.getBlockState( randomOffsetPos );
                    SprinkleResults.Result result = SprinkleResults.get( currentState.getBlock() );
                    
                    // Process sprinkled block (moisten farmland and other stuff)
                    if( result != null ) {
                        BlockState newState = result.getState( level, randomOffsetPos, currentState );
                        
                        if( newState != currentState ) {
                            level.setBlock( randomOffsetPos, result.getState( level, randomOffsetPos, currentState ), SprinklerBlock.UPDATE_CLIENTS );
                        }
                    }
                    // TODO - register this as a sprinkle result
                    // extinguish fires
                    if( level.getBlockState( randomOffsetPos ).is( BlockTags.FIRE ) ) {
                        level.removeBlock( randomOffsetPos, false );
                    }
                }
                AABB range = new AABB( pos.offset( -radius, -1, -radius ), pos.offset( radius, 2, radius ) );
                
                // Tough As Nails compat: cool down players
                if( Config.COMPAT.TOUGH_AS_NAILS.sprinklerCoolsPlayers.get() ) {
                    for( Player player : level.getEntitiesOfClass( Player.class, range, player -> !player.isCreative() && !player.isSpectator() ) ) {
                        ToughAsNailsHelper.sprinklePlayer( player );
                    }
                }
                // entity interactions
                if( state.getValue( SprinklerBlock.FACING ) == Direction.DOWN )
                    range = range.move( 0, -3, 0 );
                
                for( Entity entity : level.getEntitiesOfClass( Entity.class, range, entity -> true ) ) {
                    // Hurt water sensitive mobs if enabled
                    if( Config.IRRIGATION.SPRINKLERS.hurtsWaterSensitive.get() ) {
                        if( entity instanceof LivingEntity livingEntity && livingEntity.isSensitiveToWater() ) {
                            entity.hurt( entity.damageSources().drown(), 1.0F );
                        }
                    }
                    // Extinguish entities if enabled
                    if( Config.IRRIGATION.SPRINKLERS.extinguishEntities.get() ) {
                        if( entity.getRemainingFireTicks() > 0 ) entity.clearFire();
                    }
                }
            }
        }
    }
    
    /**
     * Updated the sprinkler's current state,
     * if the given state differs from the current.
     */
    protected void maybeUpdateState( State newState ) {
        if( newState != state ) {
            state = newState;
            
            if( hasLevel() ) {
                sendBlockUpdate();
            }
        }
    }
    
    /** Sets the current state of the sprinkler. */
    public void setState( State state ) {
        this.state = state;
    }
    
    /** @return The current state of the sprinkler. */
    public State getState() {
        return state;
    }
    
    @Override
    public void saveAdditional( CompoundTag compoundTag ) {
        super.saveAdditional( compoundTag );
        writeSyncData( compoundTag );
    }
    
    @Override
    public void load( CompoundTag compoundTag ) {
        super.load( compoundTag );
        readSyncData( compoundTag );
    }
    
    /**
     * Writes data that should be synced to the client
     * to the given compound tag.
     *
     * @return The compound tag with update data to send to the client.
     */
    private CompoundTag writeSyncData( CompoundTag compoundTag ) {
        waterTank.writeToNBT( compoundTag );
        compoundTag.putString( KEY_STATE, state.getSerializedName() );
        
        return compoundTag;
    }
    
    /** Called on the client when receiving an update packet from the server. */
    private void readSyncData( CompoundTag syncTag ) {
        waterTank.readFromNBT( syncTag );
        
        if( NBTHelper.containsString( syncTag, KEY_STATE ) ) {
            state = State.getFromName( syncTag.getString( KEY_STATE ) );
        }
    }
    
    /**
     * @return A compound tag containing data that should be sent to
     * the client when an update is requested.
     */
    @Override
    public CompoundTag getUpdateTag() {
        return writeSyncData( new CompoundTag() );
    }
    
    /** @return An update packet for syncing data to clients. */
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    /** Called on the client when receiving update data from the server. */
    @Override
    public void handleUpdateTag( CompoundTag tag ) {
        readSyncData( tag );
    }
    
    /** Send a block update which should send the sprinkler update packet to clients. */
    protected void sendBlockUpdate() {
        // noinspection ConstantConditions
        level.sendBlockUpdated( getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS );
    }
    
    /** @return This block entity's capability data of a certain capability type, and optionally from a specific facing. */
    @Override
    public <T> LazyOptional<T> getCapability( Capability<T> capability, @Nullable Direction facing ) {
        LazyOptional<T> result = ForgeCapabilities.FLUID_HANDLER.orEmpty( capability, fluidHandler );
        
        if( result.isPresent() ) {
            Direction sprinklerDir = getBlockState().getValue( SprinklerBlock.FACING );
            
            if( facing == null )
                return result;
            
            return facing != sprinklerDir ? result : LazyOptional.empty();
        }
        else {
            return super.getCapability( capability, facing );
        }
    }
    
    // TODO - This looks a bit insane when the radius is big, maychancely tweak?
    
    /** Helper method for spawning splash particles. */
    protected static void splashParticles( int radius, Level level, BlockPos pos ) {
        // Make sure we are in a loaded area.
        // Weird things can happen if the player is suddenly moved far away for any reason
        // and things are unloaded before we are done spawning splash particles
        if( !level.isLoaded( pos ) ) return;
        
        final RandomSource random = level.random;
        final double speedMul = 15.0 * radius;
        final int count = Math.max( 3 * radius, 100 );
        
        for( int i = 0; i < count; ++i ) {
            final double dx = (double) random.nextFloat() - 0.5;
            final double dy = level.getBlockState( pos ).getValue( SprinklerBlock.FACING ) == Direction.UP ? 1.0 : -1.0;
            final double dz = (double) random.nextFloat() - 0.5;
            
            double yOffset = dy < 0.0 ? -0.001 : 1.0;
            
            level.addParticle( DSParticles.SPRINKLER_SPLASH.get(),
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + yOffset,
                    (double) pos.getZ() + 0.5,
                    dx * speedMul,
                    dy * 60.0,
                    dz * speedMul );
        }
    }
    
    /** Helper method for spawning misc particles for a "vaporizing" effect. */
    protected static void vaporParticles( Level level, BlockPos pos ) {
        // Make sure we are in a loaded area.
        // Weird things can happen if the player is suddenly moved far away for any reason
        // and things are unloaded before we are done spawning splash particles
        if( !level.isLoaded( pos ) ) return;
        
        final RandomSource random = level.random;
        final double dy = level.getBlockState( pos ).getValue( SprinklerBlock.FACING ) == Direction.UP
                ? 0.15 : -0.02;
        final double yOffset = dy < 0.0
                ? -0.001 : 1.0;
        
        if( random.nextInt( 10 ) == 0 ) {
            final double dx = (double) random.nextFloat() - 0.5;
            final double dz = (double) random.nextFloat() - 0.5;
            
            level.addParticle( DSParticles.SPRINKLER_SPLASH.get(),
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + yOffset,
                    (double) pos.getZ() + 0.5,
                    dx * 5.0,
                    dy * 60.0,
                    dz * 5.0 );
        }
        else {
            level.addParticle( DSParticles.WATER_VAPOR.get(),
                    (double) pos.getX() + 0.5 + (random.nextGaussian() / 10),
                    (double) pos.getY() + yOffset,
                    (double) pos.getZ() + 0.5 + (random.nextGaussian() / 10),
                    0.0,
                    dy,
                    0.0 );
        }
    }
    
    /** Represents the current activity/state of a sprinkler. */
    public enum State implements StringRepresentable {
        NONE( "none" ),
        SPRINKLE( "sprinkle" ),
        SPOUT_VAPOR( "spout_vapor" );
        
        final String name;
        
        State( String name ) {
            this.name = name;
        }
        
        @Override
        public String getSerializedName() {
            return name;
        }
        
        /**
         * @return The {@link State} with the specified name.
         * Defaults to {@link State#NONE} if no match is found.
         */
        public static State getFromName( String name ) {
            for( State state : State.values() ) {
                if( state.name.equalsIgnoreCase( name ) )
                    return state;
            }
            return NONE;
        }
    }
}
