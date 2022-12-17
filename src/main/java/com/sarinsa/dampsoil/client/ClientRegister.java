package com.sarinsa.dampsoil.client;

import com.sarinsa.dampsoil.client.particle.SprinklerSplashParticle;
import com.sarinsa.dampsoil.common.core.DSParticles;
import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DampSoil.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegister {

    @SubscribeEvent
    public static void onClientSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        ParticleManager manager = Minecraft.getInstance().particleEngine;

        manager.register(DSParticles.SPRINKLER_SPLASH.get(), SprinklerSplashParticle.Factory::new);
    }
}
