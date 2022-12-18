package com.sarinsa.dampsoil.common.core;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import com.sarinsa.dampsoil.common.core.registry.DSItems;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.core.registry.DSTileEntities;
import com.sarinsa.dampsoil.common.event.DSEventListener;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.HoeItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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
        DSParticles.PARTICLES.register(modBus);
        DSTileEntities.TILE_ENTITIES.register(modBus);

        modBus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(new DSEventListener());

        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, DSCommonConfig.COMMON_SPEC);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Loop through the hoe tillables map and replace all
            // entries that result in farmland with fully moisturized farmland.
            HoeItem.TILLABLES.replaceAll((block, state) -> {
                if (state.is(Blocks.FARMLAND)) {
                    return Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7);
                }
                return state;
            });
        });
    }

    public static ResourceLocation resLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
