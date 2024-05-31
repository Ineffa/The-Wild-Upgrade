package com.ineffa.wondrouswilds.world.features.trees.decorators;

import com.ineffa.wondrouswilds.registry.WondrousWildsFeatures;
import com.mojang.serialization.Codec;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.ineffa.wondrouswilds.util.WondrousWildsUtils.HORIZONTAL_DIRECTIONS;

public class HangingBeeNestTreeDecorator extends TreeDecorator {

    public static final HangingBeeNestTreeDecorator INSTANCE = new HangingBeeNestTreeDecorator();
    public static final Codec<HangingBeeNestTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> getType() {
        return WondrousWildsFeatures.Trees.Decorators.HANGING_BEE_NEST_TYPE;
    }

    @Override
    public void generate(Generator generator) {
        TestableWorld world = generator.getWorld();
        Random random = generator.getRandom();

        List<BlockPos> horizontalLogs = generator.getLogPositions().stream().filter(pos -> world.testBlockState(pos, state -> state.contains(PillarBlock.AXIS) && state.get(PillarBlock.AXIS).isHorizontal())).collect(Collectors.toList());
        Collections.shuffle(horizontalLogs);

        for (BlockPos logPos : horizontalLogs) {
            BlockPos nestPos = logPos.down();
            if (generator.isAir(nestPos)) {
                List<Direction> suitableFacingDirections = new ArrayList<>();
                for (Direction direction : HORIZONTAL_DIRECTIONS) if (generator.isAir(nestPos.offset(direction))) suitableFacingDirections.add(direction);

                Direction nestFacingDirection = Util.getRandom(!suitableFacingDirections.isEmpty() ? suitableFacingDirections.toArray(Direction[]::new) : HORIZONTAL_DIRECTIONS, random);

                generator.replace(nestPos, Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, nestFacingDirection));
                world.getBlockEntity(nestPos, BlockEntityType.BEEHIVE).ifPresent(blockEntity -> {
                    int beeCount = 2 + random.nextInt(2);
                    for (int i = 0; i < beeCount; ++i) {
                        NbtCompound nbt = new NbtCompound();
                        nbt.putString(Entity.ID_KEY, Registry.ENTITY_TYPE.getId(EntityType.BEE).toString());
                        blockEntity.addBee(nbt, random.nextInt(599), false);
                    }
                });

                return;
            }
        }
    }
}
