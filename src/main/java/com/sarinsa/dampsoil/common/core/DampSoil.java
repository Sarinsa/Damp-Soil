package com.sarinsa.dampsoil.common.core;

import com.sarinsa.dampsoil.common.compat.glitchfiend.TempModifiers;
import com.sarinsa.dampsoil.common.compat.glitchfiend.ToughAsNailsHelper;
import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import com.sarinsa.dampsoil.common.core.registry.DSItems;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.core.registry.DSBlockEntities;
import com.sarinsa.dampsoil.common.event.DSEventListener;
import com.sarinsa.dampsoil.common.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DampSoil.MODID)
public class DampSoil {

    public static final String MODID = "dampsoil";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @SuppressWarnings("FieldCanBeLocal")
    private final PacketHandler packetHandler = new PacketHandler();


    public DampSoil() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        packetHandler.registerMessages();

        DSBlocks.BLOCKS.register(modBus);
        DSItems.ITEMS.register(modBus);
        DSParticles.PARTICLES.register(modBus);
        DSBlockEntities.TILE_ENTITIES.register(modBus);

        modBus.addListener(DSItems::onCreativeTabPopulate);
        modBus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(new DSEventListener());

        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, DSCommonConfig.COMMON_SPEC);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (ModList.get().isLoaded(ToughAsNailsHelper.MODID)) {
                TempModifiers.register();
            }
        });
    }

    public static ResourceLocation resLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
