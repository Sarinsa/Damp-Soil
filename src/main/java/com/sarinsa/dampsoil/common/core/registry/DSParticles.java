package com.sarinsa.dampsoil.common.core.registry;

import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DampSoil.MODID);


    public static final RegistryObject<SimpleParticleType> SPRINKLER_SPLASH = PARTICLES.register("sprinkler_splash", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> WATER_VAPOR = PARTICLES.register("water_vapor", () -> new SimpleParticleType(false));
}
