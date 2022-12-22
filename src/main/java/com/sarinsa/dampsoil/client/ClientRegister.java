package com.sarinsa.dampsoil.client;

import com.sarinsa.dampsoil.client.particle.SprinklerSplashParticle;
import com.sarinsa.dampsoil.client.particle.WaterVaporParticle;
import com.sarinsa.dampsoil.common.core.DampSoil;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DampSoil.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegister {

    @SubscribeEvent
    public static void onClientSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.register(DSParticles.SPRINKLER_SPLASH.get(), SprinklerSplashParticle.Factory::new);
        event.register(DSParticles.WATER_VAPOR.get(), WaterVaporParticle.Factory::new);
    }
}
