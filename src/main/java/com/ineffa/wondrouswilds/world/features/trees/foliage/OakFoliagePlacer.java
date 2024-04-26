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

public class OakFoliagePlacer extends FoliagePlacer {

    public static final Codec<OakFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> fillFoliagePlacerFields(instance).apply(instance, OakFoliagePlacer::new));

    public OakFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return WondrousWildsFeatures.Trees.FoliagePlacers.OAK;
    }

    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
        BlockPos.Mutable currentCenter = treeNode.getCenter().mutableCopy();
        Set<BlockPos> leaves = new HashSet<>();

        leaves.add(currentCenter.toImmutable()); for (Direction direction : HORIZONTAL_DIRECTIONS) leaves.add(currentCenter.offset(direction));

        currentCenter.move(Direction.DOWN);
        if (random.nextBoolean()) leaves.addAll(getEdges(currentCenter, 2, random.nextBoolean() ? 1 : 0));

        currentCenter.move(Direction.DOWN);
        leaves.addAll(getCenteredCuboid(currentCenter, 1, 1));

        for (int i = 0; i < 2; ++i) {
            leaves.addAll(getEdges(currentCenter, 2, 1));
            currentCenter.move(Direction.DOWN);
        }

        int bottomSize = random.nextInt(3);
        if (bottomSize != 0) {
            for (Direction direction : HORIZONTAL_DIRECTIONS) leaves.add(currentCenter.offset(direction));
            if (bottomSize >= 2) for (Direction direction : HORIZONTAL_DIRECTIONS) leaves.add(currentCenter.offset(direction).offset(direction.rotateYClockwise()));
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
