package com.sarinsa.dampsoil.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class WaterVaporParticle extends TextureSheetParticle {

    private SpriteSet spriteSet;

    public WaterVaporParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        spriteSet = sprites;
        friction = 0.96F;
        xd = 0.0F;
        yd = Math.max(yd * (double)0.01F + ySpeed, 0.01F);
        zd = 0.0F;
        quadSize *= 0.5F;
        int i = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
        lifetime = (int) Math.max((float) i * 2.5F, 1.0F);
        hasPhysics = true;
        pickSprite(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        setAlpha(1.0F - ((float) age / (float) lifetime));
        super.tick();
    }

    public record Factory(
            SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        public Particle createParticle(SimpleParticleType particleType, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WaterVaporParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        }
    }
}
