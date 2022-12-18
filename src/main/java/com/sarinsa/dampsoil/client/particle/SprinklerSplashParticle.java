package com.sarinsa.dampsoil.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SprinklerSplashParticle extends SpriteTexturedParticle {


    public SprinklerSplashParticle(ClientWorld clientWorld, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite sprites) {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);
        hasPhysics = true;
        gravity = 0.7F;

        xd *= 5.5F;
        yd *= 1.2F;
        zd *= 5.5F;
        pickSprite(sprites);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {

        private final IAnimatedSprite sprites;

        public Factory(IAnimatedSprite animatedSprite) {
            sprites = animatedSprite;
        }

        public Particle createParticle(BasicParticleType particleType, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SprinklerSplashParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
