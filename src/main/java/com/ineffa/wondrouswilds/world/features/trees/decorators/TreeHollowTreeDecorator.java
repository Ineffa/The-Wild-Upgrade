package com.ineffa.wondrouswilds.world.features.trees.decorators;

import com.ineffa.wondrouswilds.blocks.TreeHollowBlock;
import com.ineffa.wondrouswilds.entities.eggs.NesterEgg;
import com.ineffa.wondrouswilds.registry.WondrousWildsBlocks;
import com.ineffa.wondrouswilds.registry.WondrousWildsEntities;
import com.ineffa.wondrouswilds.registry.WondrousWildsFeatures;
import com.mojang.serialization.Codec;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.ineffa.wondrouswilds.util.WondrousWildsUtils.HORIZONTAL_DIRECTIONS;
import static com.ineffa.wondrouswilds.util.WondrousWildsUtils.TREE_HOLLOW_MAP;

public class TreeHollowTreeDecorator extends TreeDecorator {

    public static final TreeHollowTreeDecorator INSTANCE = new TreeHollowTreeDecorator();
    public static final Codec<TreeHollowTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> getType() {
        return WondrousWildsFeatures.Trees.Decorators.TREE_HOLLOW_TYPE;
    }

    @Override
    public void generate(Generator generator) {
        TestableWorld world = generator.getWorld();
        Random random = generator.getRandom();

        List<BlockPos> suitableLogs = generator.getLogPositions().stream().filter(pos -> world.testBlockState(pos, state -> TREE_HOLLOW_MAP.containsKey(state.getBlock()) && state.contains(PillarBlock.AXIS) && state.get(PillarBlock.AXIS).isVertical()) && hasSpaceAround(generator, pos)).sorted(Comparator.reverseOrder()).toList();
        if (suitableLogs.isEmpty()) return;

        BlockPos chosenLog = suitableLogs.get(random.nextInt(MathHelper.ceil(suitableLogs.size() / 3.0D)));
        if (chosenLog == null) return;

        List<Direction> suitableFacingDirections = new ArrayList<>();
        for (Direction direction : HORIZONTAL_DIRECTIONS) if (generator.isAir(chosenLog.offset(direction))) suitableFacingDirections.add(direction);

        Direction facingDirection = Util.getRandom(!suitableFacingDirections.isEmpty() ? suitableFacingDirections.toArray(Direction[]::new) : HORIZONTAL_DIRECTIONS, random);

        world.testBlockState(chosenLog, state -> {
            generator.replace(chosenLog, TREE_HOLLOW_MAP.get(state.getBlock()).getDefaultState().with(TreeHollowBlock.FACING, facingDirection));
            world.getBlockEntity(chosenLog, WondrousWildsBlocks.BlockEntities.TREE_HOLLOW).ifPresent(treeHollow -> {
                // TODO: Make variables configurable for use with other mobs
                treeHollow.addFreshInhabitant(WondrousWildsEntities.WOODPECKER, false);

                int babies = 0;
                while (babies < 3) if (random.nextBoolean()) ++babies; else break;
                if (babies > 0) {
                    boolean eggs = random.nextBoolean();
                    for (int i = babies; i > 0; --i) {
                        if (eggs) treeHollow.addEgg(new NesterEgg(WondrousWildsEntities.WOODPECKER, null, false, new Pair<>(48000, 72000), random));
                        else treeHollow.addFreshInhabitant(WondrousWildsEntities.WOODPECKER, true);
                    }
                }
            });

            return true;
        });
    }

    private static boolean hasSpaceAround(Generator generator, BlockPos pos) {
        boolean hasOpenSpace = false;
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos offsetPos = pos.offset(direction);
            if (generator.isAir(offsetPos)) {
                hasOpenSpace = true;
                break;
            }
        }

        return hasOpenSpace;
    }
}
