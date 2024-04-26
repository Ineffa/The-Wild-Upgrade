package com.ineffa.wondrouswilds.world.features.trees.foliage;

import com.ineffa.wondrouswilds.registry.WondrousWildsFeatures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.ineffa.wondrouswilds.util.WondrousWildsUtils.*;

public class BirchFoliagePlacer extends FoliagePlacer {

    public static final Codec<BirchFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> fillFoliagePlacerFields(instance).apply(instance, BirchFoliagePlacer::new));

    public BirchFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return WondrousWildsFeatures.Trees.FoliagePlacers.BIRCH;
    }

    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
        BlockPos origin = treeNode.getCenter();
        BlockPos.Mutable currentCenter = origin.mutableCopy();

        Set<BlockPos> leaves = new HashSet<>();

        leaves.add(origin); for (Direction direction : HORIZONTAL_DIRECTIONS) leaves.add(origin.offset(direction));

        currentCenter.move(Direction.DOWN, 2);
        leaves.addAll(getCenteredCuboid(currentCenter, 1, 1));

        int[] edgeSizes = {0, 0};
        if (random.nextInt(edgeSizes.length + 1) != 0) edgeSizes[random.nextInt(2)] = 1;
        for (int i = 0; i < 2; ++i) {
            leaves.addAll(getEdges(currentCenter, 2, edgeSizes[i]));
            currentCenter.move(Direction.DOWN);
        }

        int bottomSize = random.nextInt(edgeSizes[1] == 0 ? 3 : 4);
        int bottomCurrentStage = 1;
        while (bottomCurrentStage <= bottomSize) {
            for (Direction direction : HORIZONTAL_DIRECTIONS) leaves.add(switch (bottomCurrentStage) {
                default -> currentCenter.offset(direction);
                case 2 -> currentCenter.offset(direction).offset(direction.rotateYClockwise());
                case 3 -> currentCenter.offset(direction, 2);
            });
            ++bottomCurrentStage;
        }

        for (BlockPos pos : leaves) placeFoliageBlock(world, replacer, random, config, pos);
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return 0;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return false;
    }
}
