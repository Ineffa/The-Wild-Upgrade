package com.ineffa.wondrouswilds.world.features;

import com.ineffa.wondrouswilds.world.features.configs.RandomFromWeightedGroupsFeatureConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomFromWeightedGroupsFeature extends Feature<RandomFromWeightedGroupsFeatureConfig> {

    public RandomFromWeightedGroupsFeature() {
        super(RandomFromWeightedGroupsFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<RandomFromWeightedGroupsFeatureConfig> context) {
        RandomFromWeightedGroupsFeatureConfig config = context.getConfig();
        Random random = context.getRandom();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        ChunkGenerator chunkGenerator = context.getGenerator();
        BlockPos blockPos = context.getOrigin();

        for (WeightedFeatureGroup.RandomEntry randomEntry : config.randomEntries()) {
            if (!(random.nextFloat() < randomEntry.chance)) continue;
            return randomEntry.generate(structureWorldAccess, chunkGenerator, random, blockPos);
        }

        return config.defaultGroup().generate(structureWorldAccess, chunkGenerator, random, blockPos);
    }
}
