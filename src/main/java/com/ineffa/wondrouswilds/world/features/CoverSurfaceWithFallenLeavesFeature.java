package com.ineffa.wondrouswilds.world.features;

import com.ineffa.wondrouswilds.blocks.FallenLeavesBlock;
import com.ineffa.wondrouswilds.registry.WondrousWildsTags;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import com.ineffa.wondrouswilds.world.features.configs.CoverSurfaceWithFallenLeavesFeatureConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class CoverSurfaceWithFallenLeavesFeature extends Feature<CoverSurfaceWithFallenLeavesFeatureConfig> {

    public CoverSurfaceWithFallenLeavesFeature() {
        super(CoverSurfaceWithFallenLeavesFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<CoverSurfaceWithFallenLeavesFeatureConfig> context) {
        CoverSurfaceWithFallenLeavesFeatureConfig config = context.getConfig();
        Random random = context.getRandom();
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        BlockPos.Mutable leavesPos = new BlockPos.Mutable();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int x = origin.getX() + i;
                int z = origin.getZ() + j;
                int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
                leavesPos.set(x, y, z);

                if (!world.isAir(leavesPos) || !config.canPlaceOn().test(world, leavesPos.down())) continue;

                BlockState fallenLeavesState = config.fallenLeavesProvider().getBlockState(random, leavesPos);
                if (!(fallenLeavesState.getBlock() instanceof FallenLeavesBlock)) continue;

                fallenLeavesState = fallenLeavesState
                        .with(FallenLeavesBlock.FACING, WondrousWildsUtils.getRandomHorizontalDirection(random))
                        .with(FallenLeavesBlock.DENSITY, Math.min(FallenLeavesBlock.MAX_DENSITY, config.density().get(random)));
                if (!fallenLeavesState.canPlaceAt(world, leavesPos)) continue;

                if (!world.getBiome(leavesPos).isIn(WondrousWildsTags.BiomeTags.HAS_FALLEN_LEAVES_LAYER)) continue;

                world.setBlockState(leavesPos, fallenLeavesState, Block.NOTIFY_LISTENERS);
            }
        }
        return true;
    }
}
