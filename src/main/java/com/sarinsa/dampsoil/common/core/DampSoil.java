package com.sarinsa.dampsoil.common.core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DampSoil.MODID)
public class DampSoil {

    public static final String MODID = "dampsoil";

    private static final Logger LOGGER = LogManager.getLogger(MODID);


    public DampSoil() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        DSBlocks.BLOCKS.register(modBus);
        DSItems.ITEMS.register(modBus);
        DSTileEntities.TILE_ENTITIES.register(modBus);
    }

    public static ResourceLocation resLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
