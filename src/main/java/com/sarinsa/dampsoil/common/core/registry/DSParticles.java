package com.sarinsa.dampsoil.common.core.registry;

import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DampSoil.MODID);


    public static final RegistryObject<BasicParticleType> SPRINKLER_SPLASH = PARTICLES.register("sprinkler_splash", () -> new BasicParticleType(false));
}
