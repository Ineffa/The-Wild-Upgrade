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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.ineffa.wondrouswilds.util.WondrousWildsUtils.*;

public class FancyBirchFoliagePlacer extends FoliagePlacer {

    public static final Codec<FancyBirchFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> FancyBirchFoliagePlacer.fillFoliagePlacerFields(instance).apply(instance, FancyBirchFoliagePlacer::new));

    public FancyBirchFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return WondrousWildsFeatures.Trees.FoliagePlacers.FANCY_BIRCH;
    }

    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
        BlockPos origin = treeNode.getCenter();
        BlockPos.Mutable currentCenter = origin.mutableCopy();

        List<BlockPos> leaves = new ArrayList<>();

        // Top layers
        leaves.add(origin); for (Direction direction : HORIZONTAL_DIRECTIONS) leaves.add(origin.offset(direction));

        // Intermediate & middle layers
        currentCenter.move(Direction.DOWN);

        final int middleLayers = random.nextBetween(3, 5);

        boolean finishedEdges = false;
        boolean shrinkEdgeLength = false;
        int nextEdgeLength = 0;

        for (int layerCount = -1; layerCount <= middleLayers; ++layerCount) {
            boolean intermediate = layerCount == -1 || layerCount == middleLayers;

            leaves.addAll(getCenteredCuboid(currentCenter, intermediate ? 1 : 2));
            if (intermediate) leaves.addAll(getEdges(currentCenter, 2, 1));
            else if (!finishedEdges) {
                boolean reachedMaxLength = nextEdgeLength >= 2;
                boolean reachedMinLength = nextEdgeLength <= 0;

                if (layerCount == 0 && random.nextBoolean()) {
                    currentCenter.move(Direction.DOWN);
                    if (middleLayers == 3 || random.nextBoolean()) ++nextEdgeLength;
                    continue;
                }

                leaves.addAll(getEdges(currentCenter, 3, nextEdgeLength));

                if (!shrinkEdgeLength && reachedMaxLength) shrinkEdgeLength = true;

                if (shrinkEdgeLength) --nextEdgeLength;
                else ++nextEdgeLength;

                if (layerCount > 1 && reachedMinLength) finishedEdges = true;
            }

            currentCenter.move(Direction.DOWN);
        }

        // Bottom layer
        for (Direction direction : HORIZONTAL_DIRECTIONS) leaves.add(currentCenter.offset(direction));

        // Final placement
        for (BlockPos pos : leaves) FancyBirchFoliagePlacer.placeFoliageBlock(world, replacer, random, config, pos);
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
