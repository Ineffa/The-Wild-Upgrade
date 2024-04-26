package com.ineffa.wondrouswilds.world.features;

import com.ineffa.wondrouswilds.blocks.VioletBlock;
import com.ineffa.wondrouswilds.world.features.configs.VioletPatchFeatureConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class VioletPatchFeature extends Feature<VioletPatchFeatureConfig> {

    public VioletPatchFeature() {
        super(VioletPatchFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<VioletPatchFeatureConfig> context) {
        VioletPatchFeatureConfig config = context.getConfig();
        Random random = context.getRandom();
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        int horizontalSpread = 12 + 1;
        int verticalSpread = 2 + 1;

        int i = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int j = 0; j < 280; ++j) {
            mutablePos.set(origin, random.nextInt(horizontalSpread) - random.nextInt(horizontalSpread), random.nextInt(verticalSpread) - random.nextInt(verticalSpread), random.nextInt(horizontalSpread) - random.nextInt(horizontalSpread));

            if (world.isAir(mutablePos)) {
                BlockState violetState = config.violetProvider().getBlockState(random, mutablePos);

                if (violetState.getBlock() instanceof VioletBlock) violetState = violetState.with(VioletBlock.VIOLETS, random.nextBetween(VioletBlock.MIN_VIOLETS, VioletBlock.MAX_VIOLETS));
                else continue;

                if (!violetState.canPlaceAt(world, mutablePos)) continue;

                world.setBlockState(mutablePos, violetState, Block.NOTIFY_LISTENERS);
            }

            ++i;
        }

        return i > 0;
    }
}
