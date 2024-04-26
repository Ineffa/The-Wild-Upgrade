package com.ineffa.wondrouswilds.world.features.trees.decorators;

import com.ineffa.wondrouswilds.blocks.IvyBlock;
import com.ineffa.wondrouswilds.registry.WondrousWildsBlocks;
import com.ineffa.wondrouswilds.registry.WondrousWildsFeatures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.EnumSet;

public class IvyTreeDecorator extends TreeDecorator {

    public static final Codec<IvyTreeDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_to_generate").forGetter(decorator -> decorator.chanceToGenerate)
    ).apply(instance, IvyTreeDecorator::new));

    private static final IvyBlock IVY_BLOCK = (IvyBlock) WondrousWildsBlocks.IVY;

    private static final float CHANCE_PER_SURFACE = 0.75F;
    private final float chanceToGenerate;

    public IvyTreeDecorator(float chanceToGenerate) {
        this.chanceToGenerate = chanceToGenerate;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return WondrousWildsFeatures.Trees.Decorators.IVY_TYPE;
    }

    @Override
    public void generate(Generator generator) {
        Random random = generator.getRandom();
        if (random.nextFloat() >= this.chanceToGenerate) return;

        generator.getLogPositions().forEach(logPos -> {
            for (Direction directionFromLog : Direction.values()) {
                BlockPos ivyPos = logPos.offset(directionFromLog);
                if (random.nextFloat() < CHANCE_PER_SURFACE && generator.isAir(ivyPos)) {
                    StructureWorldAccess world = (StructureWorldAccess) generator.getWorld();
                    Direction initialIvyDirection = directionFromLog.getOpposite();
                    BlockState ivyState = IVY_BLOCK.withDirection(world.getBlockState(ivyPos), world, ivyPos, initialIvyDirection);
                    if (ivyState == null) continue;
                    boolean canPlaceOnOppositeSurface = false;
                    for (Direction currentIvyDirection : EnumSet.complementOf(EnumSet.of(initialIvyDirection, directionFromLog, Direction.DOWN))) {
                        if (random.nextFloat() < CHANCE_PER_SURFACE && IVY_BLOCK.canGrowWithDirection(world, ivyState, ivyPos, currentIvyDirection)) {
                            ivyState = ivyState.with(IvyBlock.getProperty(currentIvyDirection), true);
                            canPlaceOnOppositeSurface = true;
                        }
                    }
                    if (canPlaceOnOppositeSurface && random.nextFloat() < CHANCE_PER_SURFACE && IVY_BLOCK.canGrowWithDirection(world, ivyState, ivyPos, directionFromLog))
                        ivyState = ivyState.with(IvyBlock.getProperty(directionFromLog), true);

                    generator.replace(ivyPos, ivyState);
                }
            }
        });
    }
}
