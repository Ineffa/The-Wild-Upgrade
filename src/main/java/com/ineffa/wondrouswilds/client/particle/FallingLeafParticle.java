package com.ineffa.wondrouswilds.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class FallingLeafParticle extends SpriteBillboardParticle {

    private final float randomFloat;
    private final float rotationAcceleration;
    private float rotationSpeed;

    private FallingLeafParticle(ClientWorld clientWorld, double x, double y, double z, float red, float green, float blue, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);

        this.setColor(red, green, blue);
        this.setSprite(spriteProvider.getSprite(this.random.nextInt(12), 12));

        this.randomFloat = this.random.nextFloat();
        this.rotationAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -5.0F : 5.0F);
        this.rotationSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -30.0F : 30.0F);

        this.maxAge = 300;

        this.gravityStrength = 7.5E-4F;
        this.velocityMultiplier = 1.0F;

        this.scale = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.setBoundingBoxSpacing(this.scale, this.scale);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.maxAge-- <= 0) this.markDead();
        if (this.dead) return;

        float f = 300 - this.maxAge;
        float g = Math.min(f / 300.0F, 1.0F);
        double d = Math.cos(Math.toRadians(this.randomFloat * 60.0F)) * 2.0D * Math.pow(g, 1.25D);
        double e = Math.sin(Math.toRadians(this.randomFloat * 60.0F)) * 2.0D * Math.pow(g, 1.25D);
        this.velocityX += d * 0.0025D;
        this.velocityZ += e * 0.0025D;
        this.velocityY -= this.gravityStrength;
        this.rotationSpeed += this.rotationAcceleration / 20.0F;
        this.prevAngle = this.angle;
        this.angle += this.rotationSpeed / 20.0F;
        this.move(this.velocityX, this.velocityY, this.velocityZ);

        if (this.onGround || this.maxAge < 299 && (this.velocityX == 0.0D || this.velocityZ == 0.0D)) this.markDead();
        if (this.dead) return;

        this.velocityX *= this.velocityMultiplier;
        this.velocityY *= this.velocityMultiplier;
        this.velocityZ *= this.velocityMultiplier;
    }

    @Environment(value = EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType particleType, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new FallingLeafParticle(world, x, y, z, (float) velocityX, (float) velocityY, (float) velocityZ, this.spriteProvider);
        }
    }
}
