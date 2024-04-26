package com.ineffa.wondrouswilds.world.features.trees.decorators;

import com.ineffa.wondrouswilds.blocks.BigPolyporeBlock;
import com.ineffa.wondrouswilds.blocks.SmallPolyporeBlock;
import com.ineffa.wondrouswilds.registry.WondrousWildsBlocks;
import com.ineffa.wondrouswilds.registry.WondrousWildsFeatures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.ineffa.wondrouswilds.util.WondrousWildsUtils.HORIZONTAL_DIRECTIONS;

public class PolyporeTreeDecorator extends TreeDecorator {

    public static final Codec<PolyporeTreeDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("min_clusters").forGetter(PolyporeTreeDecorator::getMinClusters),
            Codecs.POSITIVE_INT.fieldOf("max_clusters").forGetter(PolyporeTreeDecorator::getMaxClusters),
            Codecs.POSITIVE_INT.fieldOf("max_cluster_size").forGetter(PolyporeTreeDecorator::getMaxClusterSize)
    ).apply(instance, PolyporeTreeDecorator::new));

    private static final BlockState SMALL_POLYPORE_STATE = WondrousWildsBlocks.SMALL_POLYPORE.getDefaultState();
    private static final BlockState BIG_POLYPORE_STATE = WondrousWildsBlocks.BIG_POLYPORE.getDefaultState();

    private final int minClusters, maxClusters, maxClusterSize;

    public int getMinClusters() {
        return this.minClusters;
    }

    public int getMaxClusters() {
        return this.maxClusters;
    }

    public int getMaxClusterSize() {
        return this.maxClusterSize;
    }

    public PolyporeTreeDecorator(int minClusters, int maxClusters, int maxClusterSize) {
        this.minClusters = minClusters;
        this.maxClusters = maxClusters;

        this.maxClusterSize = maxClusterSize;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return WondrousWildsFeatures.Trees.Decorators.POLYPORE_TYPE;
    }

    @Override
    public void generate(Generator generator) {
        Random random = generator.getRandom();
        TestableWorld world = generator.getWorld();

        List<BlockPos> verticalLogs = generator.getLogPositions().stream().filter(pos -> world.testBlockState(pos, state -> state.contains(PillarBlock.AXIS) && state.get(PillarBlock.AXIS).isVertical()) && canPlacePolyporesAround(generator, world, pos)).collect(Collectors.toList());
        Collections.shuffle(verticalLogs);

        int clusterLimit = this.minClusters; while (clusterLimit < this.maxClusters) if (random.nextBoolean()) ++clusterLimit; else break;
        int clustersPlaced = 0;
        for (BlockPos logPos : verticalLogs) {
            if (clustersPlaced >= clusterLimit) break;

            int steps = 1 + random.nextInt(this.maxClusterSize);
            Direction nextOffsetDirection = random.nextBoolean() ? Direction.UP : Direction.DOWN;
            int nextUpOffset = 0;
            int nextDownOffset = 0;
            for (int step = 0; step <= steps; ++step) {
                BlockPos polyporesCenter = logPos.offset(nextOffsetDirection, nextOffsetDirection == Direction.UP ? nextUpOffset : nextDownOffset);

                if (nextOffsetDirection == Direction.UP) ++nextUpOffset;
                else if (nextOffsetDirection == Direction.DOWN) ++nextDownOffset;
                nextOffsetDirection = nextOffsetDirection.getOpposite();

                if (!canPlacePolyporesAround(generator, world, polyporesCenter)) continue;

                for (Direction polyporeDirection : HORIZONTAL_DIRECTIONS) {
                    int polyporeScale = random.nextInt(5);
                    if (polyporeScale <= 0) continue;

                    BlockPos polyporePos = polyporesCenter.offset(polyporeDirection);
                    if (!isOpenSpace(generator, world, polyporePos)) continue;

                    generator.replace(polyporePos, polyporeScale > 3 ? BIG_POLYPORE_STATE.with(BigPolyporeBlock.FACING, polyporeDirection) : SMALL_POLYPORE_STATE.with(SmallPolyporeBlock.POLYPORES, polyporeScale).with(SmallPolyporeBlock.FACING, polyporeDirection));
                }
            }

            ++clustersPlaced;
        }
    }

    private static boolean canPlacePolyporesAround(Generator generator, TestableWorld world, BlockPos center) {
        if (!world.testBlockState(center, state -> state.isIn(BlockTags.LOGS))) return false;

        boolean hasOpenSpace = false;
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos offsetPos = center.offset(direction);
            if (isOpenSpace(generator, world, offsetPos)) {
                hasOpenSpace = true;
                break;
            }
        }

        return hasOpenSpace;
    }

    private static boolean isOpenSpace(Generator generator, TestableWorld world, BlockPos pos) {
        return generator.isAir(pos) || world.testFluidState(pos, state -> state.isOf(Fluids.WATER) && state.isStill());
    }
}
