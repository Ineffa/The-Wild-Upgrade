package com.ineffa.wondrouswilds.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SheddingLeavesBlock extends LeavesBlock {

    private final ParticleEffect fallingLeafParticle;
    private final int averageParticleDelayInTicks;
    private final float particleRed, particleGreen, particleBlue;
    //private boolean useWorldColor = false;

    /*public SheddingLeavesBlock(Settings settings, ParticleEffect fallingLeafParticle, int averageParticleDelayInTicks) {
        this(settings, fallingLeafParticle, averageParticleDelayInTicks, 1.0F, 1.0F, 1.0F);
        this.useWorldColor = true;
    }*/

    public SheddingLeavesBlock(Settings settings, ParticleEffect fallingLeafParticle, int averageParticleDelayInTicks, float particleRed, float particleGreen, float particleBlue) {
        super(settings);
        this.fallingLeafParticle = fallingLeafParticle;
        this.averageParticleDelayInTicks = averageParticleDelayInTicks;
        this.particleRed = particleRed;
        this.particleGreen = particleGreen;
        this.particleBlue = particleBlue;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (random.nextInt(this.averageParticleDelayInTicks) != 0) return;

        BlockPos belowPos = pos.down();
        if (isFaceFullSquare(world.getBlockState(belowPos).getCollisionShape(world, belowPos), Direction.UP)) return;

        world.addParticle(this.fallingLeafParticle, (double) pos.getX() + random.nextDouble(), (double) pos.getY() - 0.05D, (double) pos.getZ() + random.nextDouble(), this.particleRed, this.particleGreen, this.particleBlue);
    }
}
