package com.ineffa.wondrouswilds.world.features;

import com.ineffa.wondrouswilds.world.features.configs.FallenLogFeatureConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;

public class FallenLogFeature extends Feature<FallenLogFeatureConfig> {
    private static final BlockState MOSS_CARPET_STATE = Blocks.MOSS_CARPET.getDefaultState();

    public FallenLogFeature() {
        super(FallenLogFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<FallenLogFeatureConfig> context) {
        Random random = context.getRandom();
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        FallenLogFeatureConfig config = context.getConfig();
        int minLength = config.minLength;
        int maxLength = config.maxLength;

        List<BlockPos> logs = new ArrayList<>();
        List<BlockPos> stumpLogs = new ArrayList<>();

        int logLimit = random.nextBetween(minLength, maxLength);
        int nextPositiveOffsetDistance = 0;
        int nextNegativeOffsetDistance = 0;
        Direction nextOffsetDirection = Direction.fromHorizontal(random.nextInt(4));
        boolean oneDirectional = false;
        while (logs.size() < logLimit) {
            Direction.AxisDirection axisDirection = nextOffsetDirection.getDirection();

            BlockPos tryLogPos = origin.offset(nextOffsetDirection, axisDirection == Direction.AxisDirection.POSITIVE ? nextPositiveOffsetDistance : nextNegativeOffsetDistance);

            if (!oneDirectional) nextOffsetDirection = nextOffsetDirection.getOpposite();

            if (!TreeFeature.canReplace(world, tryLogPos) || !(world.testBlockState(tryLogPos.down(), state -> state.isOpaqueFullCube(world, tryLogPos.down())))) {
                if (oneDirectional) break;

                oneDirectional = true;
                continue;
            }

            logs.add(tryLogPos);

            if (config.maxStumpHeight > 0 && logs.size() == logLimit && random.nextBoolean()) {
                BlockPos tryStumpPos = tryLogPos.offset(oneDirectional ? nextOffsetDirection : nextOffsetDirection.getOpposite(), random.nextBoolean() ? 2 : 3);
                if (world.testBlockState(tryStumpPos.down(), state -> state.isOpaqueFullCube(world, tryStumpPos.down()))) {
                    int desiredHeight = 1; while (desiredHeight < config.maxStumpHeight) if (random.nextBoolean()) ++desiredHeight; else break;
                    for (int height = 0; height < desiredHeight; ++height) {
                        BlockPos nextTryStumpPos = tryStumpPos.up(height);
                        if (TreeFeature.canReplace(world, nextTryStumpPos)) stumpLogs.add(nextTryStumpPos);
                        else break;
                    }
                }
            }

            if (nextOffsetDirection.getDirection() == Direction.AxisDirection.POSITIVE) ++nextPositiveOffsetDistance;
            else if (nextOffsetDirection.getDirection() == Direction.AxisDirection.NEGATIVE) ++nextNegativeOffsetDistance;
        }

        if (logs.size() < minLength) return false;

        for (BlockPos stumpPos : stumpLogs) this.setBlockState(world, stumpPos, config.stumpProvider.getBlockState(random, stumpPos));
        for (BlockPos logPos : logs) {
            BlockState state = config.logProvider.getBlockState(random, logPos);
            if (state.getBlock() instanceof PillarBlock) state = state.with(PillarBlock.AXIS, nextOffsetDirection.getAxis());

            this.setBlockState(world, logPos, state);

            if (random.nextInt(3) == 0) {
                BlockPos tryMossCarpetPos = logPos.up();
                if (world.isAir(tryMossCarpetPos)) this.setBlockState(world, tryMossCarpetPos, MOSS_CARPET_STATE);
            }
        }

        return true;
    }
}
