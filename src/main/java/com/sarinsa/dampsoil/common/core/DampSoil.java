package com.sarinsa.dampsoil.common.core;

import com.sarinsa.dampsoil.api.SprinkleResults;
import com.sarinsa.dampsoil.api.impl.DampSoilApi;
import com.sarinsa.dampsoil.common.compat.glitchfiend.AnimalBreedListener;
import com.sarinsa.dampsoil.common.compat.glitchfiend.SereneSeasonsHelper;
import com.sarinsa.dampsoil.common.compat.glitchfiend.TempModifiers;
import com.sarinsa.dampsoil.common.compat.glitchfiend.ToughAsNailsHelper;
import com.sarinsa.dampsoil.common.core.config.Config;
import com.sarinsa.dampsoil.common.core.registry.DSBlockEntities;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import com.sarinsa.dampsoil.common.core.registry.DSItems;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.event.GameEventListener;
import com.sarinsa.dampsoil.common.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod( DampSoil.MODID )
public class DampSoil {
    
    /** The mod's ID. */
    public static final String MODID = "dampsoil";
    /** A logger instance using this mod's ID as identifier. */
    public static final Logger LOGGER = LogManager.getLogger( MODID );
    
    
    @SuppressWarnings( "FieldCanBeLocal" )
    private final PacketHandler packetHandler = new PacketHandler();
    
    private final DampSoilApi api = DampSoilApi.INSTANCE;
    
    
    public DampSoil( FMLJavaModLoadingContext context ) {
        IEventBus modBus = context.getModEventBus();
        
        modBus.addListener( DSItems::onCreativeTabPopulate );
        modBus.addListener( this::onCommonSetup );
        modBus.addListener( this::onInterModEnqueue );
        
        addCompatListener( MinecraftForge.EVENT_BUS, AnimalBreedListener::new, SereneSeasonsHelper.MODID );
        
        MinecraftForge.EVENT_BUS.register( new GameEventListener() );
        MinecraftForge.EVENT_BUS.register( api.getProduceCooldownManager() );
        
        packetHandler.registerMessages();
        
        DSBlocks.BLOCKS.register( modBus );
        DSItems.ITEMS.register( modBus );
        DSParticles.PARTICLES.register( modBus );
        DSBlockEntities.TILE_ENTITIES.register( modBus );
        
        // Enqueue config initialization
        ModLoadingStage.CONSTRUCT.getDeferredWorkQueue().enqueueWork( context.getContainer(), Config::initialize );
    }
    
    public void onCommonSetup( FMLCommonSetupEvent event ) {
        event.enqueueWork( SprinkleResults::registerDefault );
    }
    
    public void onInterModEnqueue( InterModEnqueueEvent event ) {
        event.enqueueWork( () -> {
            if( ModList.get().isLoaded( ToughAsNailsHelper.TAN_MODID ) ) {
                TempModifiers.register();
            }
        } );
    }
    
    @SuppressWarnings( "SameParameterValue" )
    private void addCompatListener( IEventBus bus, Supplier<Object> listener, String modId ) {
        if( ModList.get().isLoaded( modId ) )
            bus.register( listener.get() );
    }
    
    /** @return A {@link ResourceLocation} with this mod's namespace and the specified path. */
    public static ResourceLocation rl( String path ) {
        return ResourceLocation.fromNamespaceAndPath( MODID, path );
    }
}
