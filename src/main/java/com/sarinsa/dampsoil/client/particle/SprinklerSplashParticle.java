package com.sarinsa.dampsoil.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SprinklerSplashParticle extends TextureSheetParticle {

    public SprinklerSplashParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        hasPhysics = true;
        gravity = 0.7F;

        xd *= 5.5F;
        yd *= 1.2F;
        zd *= 5.5F;
        pickSprite(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Factory(
            SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        public Particle createParticle(SimpleParticleType particleType, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SprinklerSplashParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        }
    }
}
