package com.ineffa.wondrouswilds.world.features;

import com.ineffa.wondrouswilds.world.features.configs.RandomFromGroupsFeatureConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomFromGroupsFeature extends Feature<RandomFromGroupsFeatureConfig> {

    public RandomFromGroupsFeature() {
        super(RandomFromGroupsFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<RandomFromGroupsFeatureConfig> context) {
        RandomFromGroupsFeatureConfig config = context.getConfig();
        Random random = context.getRandom();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        ChunkGenerator chunkGenerator = context.getGenerator();
        BlockPos blockPos = context.getOrigin();

        for (FeatureGroup.RandomEntry randomGroupEntry : config.randomEntries()) {
            if (!(random.nextFloat() < randomGroupEntry.chance)) continue;
            return randomGroupEntry.generate(structureWorldAccess, chunkGenerator, random, blockPos);
        }

        return config.defaultGroup().generate(structureWorldAccess, chunkGenerator, random, blockPos);
    }
}
