package com.ineffa.wondrouswilds.registry;

import com.google.common.collect.ImmutableList;
import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.mixin.common.FoliagePlacerTypeInvoker;
import com.ineffa.wondrouswilds.mixin.common.TreeDecoratorTypeInvoker;
import com.ineffa.wondrouswilds.mixin.common.TrunkPlacerTypeInvoker;
import com.ineffa.wondrouswilds.world.features.*;
import com.ineffa.wondrouswilds.world.features.configs.*;
import com.ineffa.wondrouswilds.world.features.trees.decorators.*;
import com.ineffa.wondrouswilds.world.features.trees.foliage.BirchFoliagePlacer;
import com.ineffa.wondrouswilds.world.features.trees.foliage.FancyBirchFoliagePlacer;
import com.ineffa.wondrouswilds.world.features.trees.foliage.OakFoliagePlacer;
import com.ineffa.wondrouswilds.world.features.trees.trunks.StraightBranchingTrunkPlacer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.stream.Stream;

public class WondrousWildsFeatures {

    public static final Feature<RandomFromGroupsFeatureConfig> RANDOM_GROUP_SELECTOR = new RandomFromGroupsFeature();
    public static final Feature<RandomFromWeightedGroupsFeatureConfig> RANDOM_WEIGHTED_GROUP_SELECTOR = new RandomFromWeightedGroupsFeature();

    public static final class Trees {

        public static final class TrunkPlacers {
            public static final TrunkPlacerType<StraightBranchingTrunkPlacer> STRAIGHT_BRANCHING_TRUNK = TrunkPlacerTypeInvoker.registerTrunkPlacer("straight_branching_trunk_placer", StraightBranchingTrunkPlacer.CODEC);

            public static void init() {}
        }

        public static final class FoliagePlacers {
            public static final FoliagePlacerType<BirchFoliagePlacer> BIRCH = FoliagePlacerTypeInvoker.registerFoliagePlacer("birch_foliage_placer", BirchFoliagePlacer.CODEC);
            public static final FoliagePlacerType<FancyBirchFoliagePlacer> FANCY_BIRCH = FoliagePlacerTypeInvoker.registerFoliagePlacer("fancy_birch_foliage_placer", FancyBirchFoliagePlacer.CODEC);

            public static final FoliagePlacerType<OakFoliagePlacer> OAK = FoliagePlacerTypeInvoker.registerFoliagePlacer("oak_foliage_placer", OakFoliagePlacer.CODEC);

            public static void init() {}
        }

        public static final class Decorators {
            public static final TreeDecoratorType<TreeHollowTreeDecorator> TREE_HOLLOW_TYPE = TreeDecoratorTypeInvoker.registerTreeDecorator("tree_hollow", TreeHollowTreeDecorator.CODEC);
            public static final TreeDecoratorType<HangingBeeNestTreeDecorator> HANGING_BEE_NEST_TYPE = TreeDecoratorTypeInvoker.registerTreeDecorator("hanging_bee_nest", HangingBeeNestTreeDecorator.CODEC);
            public static final TreeDecoratorType<PolyporeTreeDecorator> POLYPORE_TYPE = TreeDecoratorTypeInvoker.registerTreeDecorator("polypores", PolyporeTreeDecorator.CODEC);
            public static final TreeDecoratorType<CobwebTreeDecorator> COBWEB_TYPE = TreeDecoratorTypeInvoker.registerTreeDecorator("cobwebs", CobwebTreeDecorator.CODEC);
            public static final TreeDecoratorType<LeafDecayTreeDecorator> LEAF_DECAY_TYPE = TreeDecoratorTypeInvoker.registerTreeDecorator("leaf_decay", LeafDecayTreeDecorator.CODEC);
            public static final TreeDecoratorType<IvyTreeDecorator> IVY_TYPE = TreeDecoratorTypeInvoker.registerTreeDecorator("ivy", IvyTreeDecorator.CODEC);

            public static final TreeDecorator TREE_HOLLOW = TreeHollowTreeDecorator.INSTANCE;
            public static final TreeDecorator HANGING_BEE_NEST = HangingBeeNestTreeDecorator.INSTANCE;
            public static final TreeDecorator POLYPORES_FANCY_BIRCH = new PolyporeTreeDecorator(-1, 2, 2);
            public static final TreeDecorator POLYPORES_DYING_FANCY_BIRCH = new PolyporeTreeDecorator(3, 4, 3);
            public static final TreeDecorator COBWEBS = CobwebTreeDecorator.INSTANCE;
            public static final TreeDecorator LEAF_DECAY_DYING_FANCY_BIRCH = new LeafDecayTreeDecorator(2, 3, 3, true);

            public static void init() {}
        }

        public static final class Configs {
            public static TreeFeatureConfig.Builder tallBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightTrunkPlacer(8, 4, 0),
                        BlockStateProvider.of(Blocks.BIRCH_LEAVES), new BirchFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).ignoreVines();
            }

            public static TreeFeatureConfig.Builder yellowTallBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightTrunkPlacer(8, 4, 0),
                        BlockStateProvider.of(WondrousWildsBlocks.YELLOW_BIRCH_LEAVES), new BirchFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).ignoreVines();
            }

            public static TreeFeatureConfig.Builder orangeTallBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightTrunkPlacer(8, 4, 0),
                        BlockStateProvider.of(WondrousWildsBlocks.ORANGE_BIRCH_LEAVES), new BirchFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).ignoreVines();
            }

            public static TreeFeatureConfig.Builder redTallBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightTrunkPlacer(8, 4, 0),
                        BlockStateProvider.of(WondrousWildsBlocks.RED_BIRCH_LEAVES), new BirchFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).ignoreVines();
            }

            public static TreeFeatureConfig.Builder fancyBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 0, 3, 1, 1),
                        BlockStateProvider.of(Blocks.BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.POLYPORES_FANCY_BIRCH, Decorators.COBWEBS)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder fancyBirchWithBeesConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 1, 3, 1, 1),
                        BlockStateProvider.of(Blocks.BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.HANGING_BEE_NEST, Decorators.POLYPORES_FANCY_BIRCH, Decorators.COBWEBS)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder dyingFancyBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(WondrousWildsBlocks.DEAD_BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 0, 3, 1, 1),
                        BlockStateProvider.of(Blocks.BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.POLYPORES_DYING_FANCY_BIRCH, Decorators.COBWEBS, Decorators.LEAF_DECAY_DYING_FANCY_BIRCH)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder yellowFancyBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 0, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.YELLOW_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.POLYPORES_FANCY_BIRCH, Decorators.COBWEBS)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder yellowFancyBirchWithBeesConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 1, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.YELLOW_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.HANGING_BEE_NEST, Decorators.POLYPORES_FANCY_BIRCH, Decorators.COBWEBS)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder dyingYellowFancyBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(WondrousWildsBlocks.DEAD_BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 0, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.YELLOW_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.POLYPORES_DYING_FANCY_BIRCH, Decorators.COBWEBS, Decorators.LEAF_DECAY_DYING_FANCY_BIRCH)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder orangeFancyBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 0, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.ORANGE_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.POLYPORES_FANCY_BIRCH, Decorators.COBWEBS)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder orangeFancyBirchWithBeesConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 1, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.ORANGE_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.HANGING_BEE_NEST, Decorators.POLYPORES_FANCY_BIRCH, Decorators.COBWEBS)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder dyingOrangeFancyBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(WondrousWildsBlocks.DEAD_BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 0, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.ORANGE_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.POLYPORES_DYING_FANCY_BIRCH, Decorators.COBWEBS, Decorators.LEAF_DECAY_DYING_FANCY_BIRCH)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder redFancyBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 0, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.RED_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.POLYPORES_FANCY_BIRCH, Decorators.COBWEBS)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder redFancyBirchWithBeesConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 1, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.RED_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.HANGING_BEE_NEST, Decorators.POLYPORES_FANCY_BIRCH, Decorators.COBWEBS)).ignoreVines();
            }

            public static TreeFeatureConfig.Builder dyingRedFancyBirchConfig() {
                return new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(WondrousWildsBlocks.DEAD_BIRCH_LOG), new StraightBranchingTrunkPlacer(13, 5, 0, 0, 3, 1, 1),
                        BlockStateProvider.of(WondrousWildsBlocks.RED_BIRCH_LEAVES), new FancyBirchFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(ImmutableList.of(Decorators.POLYPORES_DYING_FANCY_BIRCH, Decorators.COBWEBS, Decorators.LEAF_DECAY_DYING_FANCY_BIRCH)).ignoreVines();
            }

            public static void init() {}
        }

        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> FANCY_BIRCH_CONFIGURED = registerConfigured("fancy_birch", Feature.TREE, Configs.fancyBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> FANCY_BIRCH_PLACED = registerPlaced("fancy_birch", FANCY_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> FANCY_BIRCH_WITH_WOODPECKERS_CONFIGURED = registerConfigured("fancy_birch_with_woodpeckers", Feature.TREE, Configs.fancyBirchConfig().decorators(List.of(Decorators.TREE_HOLLOW)).build());
        public static final RegistryEntry<PlacedFeature> FANCY_BIRCH_WITH_WOODPECKERS_PLACED = registerPlaced("fancy_birch_with_woodpeckers", FANCY_BIRCH_WITH_WOODPECKERS_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> FANCY_BIRCH_WITH_BEES_CONFIGURED = registerConfigured("fancy_birch_with_bees", Feature.TREE, Configs.fancyBirchWithBeesConfig().build());
        public static final RegistryEntry<PlacedFeature> FANCY_BIRCH_WITH_BEES_PLACED = registerPlaced("fancy_birch_with_bees", FANCY_BIRCH_WITH_BEES_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> DYING_FANCY_BIRCH_CONFIGURED = registerConfigured("dying_fancy_birch", Feature.TREE, Configs.dyingFancyBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> DYING_FANCY_BIRCH_PLACED = registerPlaced("dying_fancy_birch", DYING_FANCY_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));

        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> YELLOW_FANCY_BIRCH_CONFIGURED = registerConfigured("yellow_fancy_birch", Feature.TREE, Configs.yellowFancyBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> YELLOW_FANCY_BIRCH_PLACED = registerPlaced("yellow_fancy_birch", YELLOW_FANCY_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> YELLOW_FANCY_BIRCH_WITH_WOODPECKERS_CONFIGURED = registerConfigured("yellow_fancy_birch_with_woodpeckers", Feature.TREE, Configs.yellowFancyBirchConfig().decorators(List.of(Decorators.TREE_HOLLOW)).build());
        public static final RegistryEntry<PlacedFeature> YELLOW_FANCY_BIRCH_WITH_WOODPECKERS_PLACED = registerPlaced("yellow_fancy_birch_with_woodpeckers", YELLOW_FANCY_BIRCH_WITH_WOODPECKERS_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> YELLOW_FANCY_BIRCH_WITH_BEES_CONFIGURED = registerConfigured("yellow_fancy_birch_with_bees", Feature.TREE, Configs.yellowFancyBirchWithBeesConfig().build());
        public static final RegistryEntry<PlacedFeature> YELLOW_FANCY_BIRCH_WITH_BEES_PLACED = registerPlaced("yellow_fancy_birch_with_bees", YELLOW_FANCY_BIRCH_WITH_BEES_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> DYING_YELLOW_FANCY_BIRCH_CONFIGURED = registerConfigured("dying_yellow_fancy_birch", Feature.TREE, Configs.dyingYellowFancyBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> DYING_YELLOW_FANCY_BIRCH_PLACED = registerPlaced("dying_yellow_fancy_birch", DYING_YELLOW_FANCY_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));

        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> ORANGE_FANCY_BIRCH_CONFIGURED = registerConfigured("orange_fancy_birch", Feature.TREE, Configs.orangeFancyBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> ORANGE_FANCY_BIRCH_PLACED = registerPlaced("orange_fancy_birch", ORANGE_FANCY_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> ORANGE_FANCY_BIRCH_WITH_WOODPECKERS_CONFIGURED = registerConfigured("orange_fancy_birch_with_woodpeckers", Feature.TREE, Configs.orangeFancyBirchConfig().decorators(List.of(Decorators.TREE_HOLLOW)).build());
        public static final RegistryEntry<PlacedFeature> ORANGE_FANCY_BIRCH_WITH_WOODPECKERS_PLACED = registerPlaced("orange_fancy_birch_with_woodpeckers", ORANGE_FANCY_BIRCH_WITH_WOODPECKERS_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> ORANGE_FANCY_BIRCH_WITH_BEES_CONFIGURED = registerConfigured("orange_fancy_birch_with_bees", Feature.TREE, Configs.orangeFancyBirchWithBeesConfig().build());
        public static final RegistryEntry<PlacedFeature> ORANGE_FANCY_BIRCH_WITH_BEES_PLACED = registerPlaced("orange_fancy_birch_with_bees", ORANGE_FANCY_BIRCH_WITH_BEES_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> DYING_ORANGE_FANCY_BIRCH_CONFIGURED = registerConfigured("dying_orange_fancy_birch", Feature.TREE, Configs.dyingOrangeFancyBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> DYING_ORANGE_FANCY_BIRCH_PLACED = registerPlaced("dying_orange_fancy_birch", DYING_ORANGE_FANCY_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));

        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> RED_FANCY_BIRCH_CONFIGURED = registerConfigured("red_fancy_birch", Feature.TREE, Configs.redFancyBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> RED_FANCY_BIRCH_PLACED = registerPlaced("red_fancy_birch", RED_FANCY_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> RED_FANCY_BIRCH_WITH_WOODPECKERS_CONFIGURED = registerConfigured("red_fancy_birch_with_woodpeckers", Feature.TREE, Configs.redFancyBirchConfig().decorators(List.of(Decorators.TREE_HOLLOW)).build());
        public static final RegistryEntry<PlacedFeature> RED_FANCY_BIRCH_WITH_WOODPECKERS_PLACED = registerPlaced("red_fancy_birch_with_woodpeckers", RED_FANCY_BIRCH_WITH_WOODPECKERS_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> RED_FANCY_BIRCH_WITH_BEES_CONFIGURED = registerConfigured("red_fancy_birch_with_bees", Feature.TREE, Configs.redFancyBirchWithBeesConfig().build());
        public static final RegistryEntry<PlacedFeature> RED_FANCY_BIRCH_WITH_BEES_PLACED = registerPlaced("red_fancy_birch_with_bees", RED_FANCY_BIRCH_WITH_BEES_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> DYING_RED_FANCY_BIRCH_CONFIGURED = registerConfigured("dying_red_fancy_birch", Feature.TREE, Configs.dyingRedFancyBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> DYING_RED_FANCY_BIRCH_PLACED = registerPlaced("dying_red_fancy_birch", DYING_RED_FANCY_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));

        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> TALL_BIRCH_CONFIGURED = registerConfigured("tall_birch", Feature.TREE, Configs.tallBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> TALL_BIRCH_PLACED = registerPlaced("tall_birch", TALL_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> YELLOW_TALL_BIRCH_CONFIGURED = registerConfigured("yellow_tall_birch", Feature.TREE, Configs.yellowTallBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> YELLOW_TALL_BIRCH_PLACED = registerPlaced("yellow_tall_birch", YELLOW_TALL_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> ORANGE_TALL_BIRCH_CONFIGURED = registerConfigured("orange_tall_birch", Feature.TREE, Configs.orangeTallBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> ORANGE_TALL_BIRCH_PLACED = registerPlaced("orange_tall_birch", ORANGE_TALL_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
        public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> RED_TALL_BIRCH_CONFIGURED = registerConfigured("red_tall_birch", Feature.TREE, Configs.redTallBirchConfig().build());
        public static final RegistryEntry<PlacedFeature> RED_TALL_BIRCH_PLACED = registerPlaced("red_tall_birch", RED_TALL_BIRCH_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));

        public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> BIRCH_FOREST_TREES_CONFIGURED = registerConfigured("birch_forest_trees", Feature.RANDOM_SELECTOR, new RandomFeatureConfig(List.of(
                new RandomFeatureEntry(FANCY_BIRCH_PLACED, 0.925F),
                new RandomFeatureEntry(FANCY_BIRCH_WITH_BEES_PLACED, 0.075F),
                new RandomFeatureEntry(FANCY_BIRCH_WITH_WOODPECKERS_PLACED, 0.05F),
                new RandomFeatureEntry(DYING_FANCY_BIRCH_PLACED, 0.025F)
        ), TALL_BIRCH_PLACED));
        public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_TREES_PLACED = registerPlaced("birch_forest_trees", BIRCH_FOREST_TREES_CONFIGURED, Stream.concat(VegetationPlacedFeatures.modifiers(PlacedFeatures.createCountExtraModifier(5, 0.1F, 1)).stream(), Stream.of(BlockFilterPlacementModifier.of(BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.MOSS_BLOCK))))).toList());

        public static final RegistryEntry<ConfiguredFeature<RandomFromWeightedGroupsFeatureConfig, ?>> OLD_GROWTH_BIRCH_FOREST_TREES_CONFIGURED = registerConfigured("old_growth_birch_forest_trees", RANDOM_WEIGHTED_GROUP_SELECTOR, new RandomFromWeightedGroupsFeatureConfig(List.of(
                new WeightedFeatureGroup.RandomEntry(List.of(new Pair<>(FANCY_BIRCH_PLACED, ConstantIntProvider.create(1)), new Pair<>(YELLOW_FANCY_BIRCH_PLACED, ConstantIntProvider.create(7)), new Pair<>(ORANGE_FANCY_BIRCH_PLACED, ConstantIntProvider.create(10)), new Pair<>(RED_FANCY_BIRCH_PLACED, ConstantIntProvider.create(2))), 0.925F),
                new WeightedFeatureGroup.RandomEntry(List.of(new Pair<>(FANCY_BIRCH_WITH_BEES_PLACED, ConstantIntProvider.create(1)), new Pair<>(YELLOW_FANCY_BIRCH_WITH_BEES_PLACED, ConstantIntProvider.create(7)), new Pair<>(ORANGE_FANCY_BIRCH_WITH_BEES_PLACED, ConstantIntProvider.create(10)), new Pair<>(RED_FANCY_BIRCH_WITH_BEES_PLACED, ConstantIntProvider.create(2))), 0.075F),
                new WeightedFeatureGroup.RandomEntry(List.of(new Pair<>(FANCY_BIRCH_WITH_WOODPECKERS_PLACED, ConstantIntProvider.create(1)), new Pair<>(YELLOW_FANCY_BIRCH_WITH_WOODPECKERS_PLACED, ConstantIntProvider.create(7)), new Pair<>(ORANGE_FANCY_BIRCH_WITH_WOODPECKERS_PLACED, ConstantIntProvider.create(10)), new Pair<>(RED_FANCY_BIRCH_WITH_WOODPECKERS_PLACED, ConstantIntProvider.create(2))), 0.05F),
                new WeightedFeatureGroup.RandomEntry(List.of(new Pair<>(DYING_FANCY_BIRCH_PLACED, ConstantIntProvider.create(1)), new Pair<>(DYING_YELLOW_FANCY_BIRCH_PLACED, ConstantIntProvider.create(7)), new Pair<>(DYING_ORANGE_FANCY_BIRCH_PLACED, ConstantIntProvider.create(10)), new Pair<>(DYING_RED_FANCY_BIRCH_PLACED, ConstantIntProvider.create(2))), 0.025F)
        ), new WeightedFeatureGroup(List.of(new Pair<>(TALL_BIRCH_PLACED, ConstantIntProvider.create(1)), new Pair<>(YELLOW_TALL_BIRCH_PLACED, ConstantIntProvider.create(7)), new Pair<>(ORANGE_TALL_BIRCH_PLACED, ConstantIntProvider.create(10)), new Pair<>(RED_TALL_BIRCH_PLACED, ConstantIntProvider.create(2))))));
        public static final RegistryEntry<PlacedFeature> OLD_GROWTH_BIRCH_FOREST_TREES_PLACED = registerPlaced("old_growth_birch_forest_trees", OLD_GROWTH_BIRCH_FOREST_TREES_CONFIGURED, Stream.concat(VegetationPlacedFeatures.modifiers(PlacedFeatures.createCountExtraModifier(5, 0.1F, 1)).stream(), Stream.of(BlockFilterPlacementModifier.of(BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.MOSS_BLOCK))))).toList());

        public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> FOREST_TREES_CONFIGURED = registerConfigured("forest_trees", Feature.RANDOM_SELECTOR, new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.BIRCH_BEES_0002, 0.15F), new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_BEES_0002, 0.4F)), TreePlacedFeatures.OAK_BEES_0002));
        public static final RegistryEntry<PlacedFeature> FOREST_TREES_PLACED = registerPlaced("forest_trees", FOREST_TREES_CONFIGURED, Stream.concat(VegetationPlacedFeatures.modifiers(PlacedFeatures.createCountExtraModifier(12, 0.1F, 1)).stream(), Stream.of(BlockFilterPlacementModifier.of(BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.MOSS_BLOCK))))).toList());

        public static void init() {}
    }

    public static final Feature<TerrainSplotchFeatureConfig> TERRAIN_SPLOTCH = new TerrainSplotchFeature();
    public static final RegistryEntry<ConfiguredFeature<TerrainSplotchFeatureConfig, ?>> SMALL_COARSE_DIRT_SPLOTCH_CONFIGURED = registerConfigured("small_coarse_dirt_splotch", TERRAIN_SPLOTCH, smallCoarseDirtSplotchConfig());
    public static final RegistryEntry<ConfiguredFeature<TerrainSplotchFeatureConfig, ?>> MEDIUM_COARSE_DIRT_SPLOTCH_CONFIGURED = registerConfigured("medium_coarse_dirt_splotch", TERRAIN_SPLOTCH, mediumCoarseDirtSplotchConfig());
    public static final RegistryEntry<ConfiguredFeature<TerrainSplotchFeatureConfig, ?>> LARGE_COARSE_DIRT_SPLOTCH_CONFIGURED = registerConfigured("large_coarse_dirt_splotch", TERRAIN_SPLOTCH, largeCoarseDirtSplotchConfig());
    public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_MEDIUM_COARSE_DIRT_SPLOTCH_PLACED = registerPlaced("birch_forest_medium_coarse_dirt_splotch", MEDIUM_COARSE_DIRT_SPLOTCH_CONFIGURED, CountPlacementModifier.of(UniformIntProvider.create(0, 2)), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_LARGE_COARSE_DIRT_SPLOTCH_PLACED = registerPlaced("birch_forest_large_coarse_dirt_splotch", LARGE_COARSE_DIRT_SPLOTCH_CONFIGURED, RarityFilterPlacementModifier.of(60), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> FOREST_COARSE_DIRT_SPLOTCH_PLACED = registerPlaced("forest_coarse_dirt_splotch", SMALL_COARSE_DIRT_SPLOTCH_CONFIGURED, CountPlacementModifier.of(BiasedToBottomIntProvider.create(0, 3)), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());

    public static final Feature<BoulderFeatureConfig> BOULDER = new BoulderFeature();
    public static final RegistryEntry<ConfiguredFeature<BoulderFeatureConfig, ?>> SMALL_BOULDER_CONFIGURED = registerConfigured("small_boulder", BOULDER, smallBoulderConfig());
    public static final RegistryEntry<ConfiguredFeature<BoulderFeatureConfig, ?>> VARIED_BOULDER_CONFIGURED = registerConfigured("varied_boulder", BOULDER, variedBoulderConfig());
    public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_BOULDER_PLACED = registerPlaced("birch_forest_boulder", VARIED_BOULDER_CONFIGURED, RarityFilterPlacementModifier.of(5), CountPlacementModifier.of(new WeightedListIntProvider(new DataPool.Builder<IntProvider>().add(ConstantIntProvider.create(1), 7).add(BiasedToBottomIntProvider.create(3, 4), 1).build())), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BlockFilterPlacementModifier.of(BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.MOSS_BLOCK))), BlockFilterPlacementModifier.of(BlockPredicate.anyOf(BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.DIRT), BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.BASE_STONE_OVERWORLD))), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> FOREST_BOULDER_PLACED = registerPlaced("forest_boulder", SMALL_BOULDER_CONFIGURED, RarityFilterPlacementModifier.of(16), CountPlacementModifier.of(BiasedToBottomIntProvider.create(1, 3)), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BlockFilterPlacementModifier.of(BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.MOSS_BLOCK))), BlockFilterPlacementModifier.of(BlockPredicate.anyOf(BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.DIRT), BlockPredicate.matchingBlockTag(Direction.DOWN.getVector(), BlockTags.BASE_STONE_OVERWORLD))), BiomePlacementModifier.of());

    public static final Feature<FallenLogFeatureConfig> FALLEN_LOG = new FallenLogFeature();
    public static final RegistryEntry<ConfiguredFeature<FallenLogFeatureConfig, ?>> FALLEN_FANCY_BIRCH_LOG_CONFIGURED = registerConfigured("fallen_fancy_birch_log", FALLEN_LOG, fallenFancyBirchLogConfig());
    public static final RegistryEntry<ConfiguredFeature<FallenLogFeatureConfig, ?>> FALLEN_BIRCH_LOG_CONFIGURED = registerConfigured("fallen_birch_log", FALLEN_LOG, fallenBirchLogConfig());
    public static final RegistryEntry<ConfiguredFeature<FallenLogFeatureConfig, ?>> FALLEN_OAK_LOG_CONFIGURED = registerConfigured("fallen_oak_log", FALLEN_LOG, fallenOakLogConfig());
    public static final RegistryEntry<PlacedFeature> FALLEN_BIRCH_LOG_PLACED = registerPlaced("fallen_birch_log", FALLEN_BIRCH_LOG_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
    public static final RegistryEntry<PlacedFeature> FALLEN_OAK_LOG_PLACED = registerPlaced("fallen_oak_log", FALLEN_OAK_LOG_CONFIGURED, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
    public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_FALLEN_LOG_PLACED = registerPlaced("birch_forest_fallen_log", FALLEN_FANCY_BIRCH_LOG_CONFIGURED, Stream.concat(VegetationPlacedFeatures.modifiersWithWouldSurvive(RarityFilterPlacementModifier.of(12), Blocks.BIRCH_SAPLING).stream(), Stream.of(BlockFilterPlacementModifier.of(BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.MOSS_BLOCK))))).toList());
    public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> FOREST_FALLEN_LOG_CONFIGURED = registerConfigured("forest_fallen_log", Feature.RANDOM_SELECTOR, new RandomFeatureConfig(List.of(new RandomFeatureEntry(FALLEN_BIRCH_LOG_PLACED, 0.15F)), FALLEN_OAK_LOG_PLACED));
    public static final RegistryEntry<PlacedFeature> FOREST_FALLEN_LOG_PLACED = registerPlaced("forest_fallen_log", FOREST_FALLEN_LOG_CONFIGURED, Stream.concat(VegetationPlacedFeatures.modifiers(RarityFilterPlacementModifier.of(28)).stream(), Stream.of(BlockFilterPlacementModifier.of(BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.MOSS_BLOCK))))).toList());

    public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> LILY_OF_THE_VALLEY_PATCH_CONFIGURED = ConfiguredFeatures.register("lily_of_the_valley_patch", Feature.FLOWER, new RandomPatchFeatureConfig(64, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_OF_THE_VALLEY)))));
    public static final RegistryEntry<PlacedFeature> LILY_OF_THE_VALLEY_PATCH_PLACED = registerPlaced("lily_of_the_valley_patch", LILY_OF_THE_VALLEY_PATCH_CONFIGURED, RarityFilterPlacementModifier.of(10), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

    public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_TALL_FLOWERS_PLACED = registerPlaced("birch_forest_tall_flowers", VegetationConfiguredFeatures.FOREST_FLOWERS, RarityFilterPlacementModifier.of(3), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, CountPlacementModifier.of(ClampedIntProvider.create(UniformIntProvider.create(-3, 1), 0, 1)), BiomePlacementModifier.of());

    public static final Feature<VioletPatchFeatureConfig> VIOLET_PATCH = new VioletPatchFeature();
    public static final RegistryEntry<ConfiguredFeature<VioletPatchFeatureConfig, ?>> PURPLE_VIOLETS_CONFIGURED = registerConfigured("purple_violets", VIOLET_PATCH, purpleVioletPatchConfig());
    public static final RegistryEntry<ConfiguredFeature<VioletPatchFeatureConfig, ?>> PINK_VIOLETS_CONFIGURED = registerConfigured("pink_violets", VIOLET_PATCH, pinkVioletPatchConfig());
    public static final RegistryEntry<ConfiguredFeature<VioletPatchFeatureConfig, ?>> RED_VIOLETS_CONFIGURED = registerConfigured("red_violets", VIOLET_PATCH, redVioletPatchConfig());
    public static final RegistryEntry<ConfiguredFeature<VioletPatchFeatureConfig, ?>> WHITE_VIOLETS_CONFIGURED = registerConfigured("white_violets", VIOLET_PATCH, whiteVioletPatchConfig());
    public static final RegistryEntry<PlacedFeature> PURPLE_VIOLETS_PLACED = registerPlaced("purple_violets", PURPLE_VIOLETS_CONFIGURED, RarityFilterPlacementModifier.of(8), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> PINK_VIOLETS_PLACED = registerPlaced("pink_violets", PINK_VIOLETS_CONFIGURED, RarityFilterPlacementModifier.of(8), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> RED_VIOLETS_PLACED = registerPlaced("red_violets", RED_VIOLETS_CONFIGURED, RarityFilterPlacementModifier.of(8), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> WHITE_VIOLETS_PLACED = registerPlaced("white_violets", WHITE_VIOLETS_CONFIGURED, RarityFilterPlacementModifier.of(8), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

    public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_GRASS_PLACED = registerPlaced("birch_forest_grass", VegetationConfiguredFeatures.PATCH_GRASS, VegetationPlacedFeatures.modifiers(8));
    public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_TALL_GRASS_PLACED = PlacedFeatures.register("birch_forest_tall_grass", VegetationConfiguredFeatures.PATCH_TALL_GRASS, RarityFilterPlacementModifier.of(8), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> FOREST_GRASS_PLACED = registerPlaced("forest_grass", VegetationConfiguredFeatures.PATCH_GRASS, VegetationPlacedFeatures.modifiers(3));

    public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> BUSHES_CONFIGURED = registerConfigured("bushes", Feature.RANDOM_PATCH, ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.BUSH)), List.of(Blocks.GRASS_BLOCK)));
    public static final RegistryEntry<PlacedFeature> BIRCH_FOREST_BUSHES_PLACED = registerPlaced("birch_forest_bushes", BUSHES_CONFIGURED, RarityFilterPlacementModifier.of(4), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> FOREST_BUSHES_PLACED = registerPlaced("forest_bushes", BUSHES_CONFIGURED, RarityFilterPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());

    public static final RegistryEntry<PlacedFeature> AUTUMN_PUMPKIN_PATCH = registerPlaced("autumn_pumpkin_patch", VegetationConfiguredFeatures.PATCH_PUMPKIN, RarityFilterPlacementModifier.of(30), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

    public static final Feature<CoverSurfaceWithFallenLeavesFeatureConfig> COVER_SURFACE_WITH_FALLEN_LEAVES = new CoverSurfaceWithFallenLeavesFeature();
    public static final RegistryEntry<ConfiguredFeature<CoverSurfaceWithFallenLeavesFeatureConfig, ?>> COVER_SURFACE_WITH_FALLEN_BIRCH_LEAVES_CONFIGURED = registerConfigured("cover_surface_with_fallen_birch_leaves", COVER_SURFACE_WITH_FALLEN_LEAVES, coverSurfaceWithFallenBirchLeavesConfig());
    public static final RegistryEntry<PlacedFeature> COVER_SURFACE_WITH_FALLEN_BIRCH_LEAVES_PLACED = registerPlaced("cover_surface_with_fallen_birch_leaves", COVER_SURFACE_WITH_FALLEN_BIRCH_LEAVES_CONFIGURED);

    private static TerrainSplotchFeatureConfig smallCoarseDirtSplotchConfig() {
        return new TerrainSplotchFeatureConfig(BlockStateProvider.of(Blocks.COARSE_DIRT), BlockPredicate.matchingBlocks(Blocks.GRASS_BLOCK), UniformIntProvider.create(2, 4), ConstantIntProvider.create(2), UniformFloatProvider.create(0.3F, 0.5F));
    }

    private static TerrainSplotchFeatureConfig mediumCoarseDirtSplotchConfig() {
        return new TerrainSplotchFeatureConfig(BlockStateProvider.of(Blocks.COARSE_DIRT), BlockPredicate.matchingBlocks(Blocks.GRASS_BLOCK), UniformIntProvider.create(4, 8), ConstantIntProvider.create(3), UniformFloatProvider.create(0.3F, 0.7F));
    }

    private static TerrainSplotchFeatureConfig largeCoarseDirtSplotchConfig() {
        return new TerrainSplotchFeatureConfig(BlockStateProvider.of(Blocks.COARSE_DIRT), BlockPredicate.matchingBlocks(Blocks.GRASS_BLOCK), UniformIntProvider.create(12, 16), ConstantIntProvider.create(5), UniformFloatProvider.create(0.6F, 0.9F));
    }

    private static BoulderFeatureConfig smallBoulderConfig() {
        return new BoulderFeatureConfig(
                new WeightedBlockStateProvider(new DataPool.Builder<BlockState>().add(Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3).add(Blocks.COBBLESTONE.getDefaultState(), 2).add(Blocks.MOSS_BLOCK.getDefaultState(), 1).build()),
                BoulderFeatureConfig.DEFAULT_BOULDER_REPLACE_PREDICATE,
                new WeightedListIntProvider(new DataPool.Builder<IntProvider>().add(ConstantIntProvider.create(1), 5).add(ConstantIntProvider.create(2), 1).build()),
                BiasedToBottomIntProvider.create(1, 3)
        );
    }

    private static BoulderFeatureConfig variedBoulderConfig() {
        return new BoulderFeatureConfig(
                new WeightedBlockStateProvider(new DataPool.Builder<BlockState>().add(Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3).add(Blocks.COBBLESTONE.getDefaultState(), 2).add(Blocks.MOSS_BLOCK.getDefaultState(), 1).build()),
                BoulderFeatureConfig.DEFAULT_BOULDER_REPLACE_PREDICATE,
                new WeightedListIntProvider(new DataPool.Builder<IntProvider>().add(new WeightedListIntProvider(new DataPool.Builder<IntProvider>().add(ConstantIntProvider.create(1), 6).add(ConstantIntProvider.create(2), 3).add(ConstantIntProvider.create(3), 1).build()), 19).add(BiasedToBottomIntProvider.create(4, 6), 1).build()),
                BiasedToBottomIntProvider.create(1, 3)
        );
    }

    private static FallenLogFeatureConfig fallenFancyBirchLogConfig() {
        return new FallenLogFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.HOLLOW_DEAD_BIRCH_LOG), BlockStateProvider.of(WondrousWildsBlocks.DEAD_BIRCH_LOG), 3, 8, 3);
    }

    private static FallenLogFeatureConfig fallenBirchLogConfig() {
        return new FallenLogFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.HOLLOW_DEAD_BIRCH_LOG), BlockStateProvider.of(WondrousWildsBlocks.DEAD_BIRCH_LOG), 2, 5, 0);
    }

    private static FallenLogFeatureConfig fallenOakLogConfig() {
        return new FallenLogFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.HOLLOW_DEAD_OAK_LOG), BlockStateProvider.of(WondrousWildsBlocks.DEAD_OAK_LOG), 2, 4, 0);
    }

    private static VioletPatchFeatureConfig purpleVioletPatchConfig() {
        return new VioletPatchFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.PURPLE_VIOLET));
    }

    private static VioletPatchFeatureConfig pinkVioletPatchConfig() {
        return new VioletPatchFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.PINK_VIOLET));
    }

    private static VioletPatchFeatureConfig redVioletPatchConfig() {
        return new VioletPatchFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.RED_VIOLET));
    }

    private static VioletPatchFeatureConfig whiteVioletPatchConfig() {
        return new VioletPatchFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.WHITE_VIOLET));
    }

    private static CoverSurfaceWithFallenLeavesFeatureConfig coverSurfaceWithFallenBirchLeavesConfig() {
        return new CoverSurfaceWithFallenLeavesFeatureConfig(BlockStateProvider.of(WondrousWildsBlocks.FALLEN_BIRCH_LEAVES), BlockPredicate.bothOf(BlockPredicate.matchingBlockTag(BlockTags.DIRT), BlockPredicate.not(BlockPredicate.matchingBlocks(Blocks.MOSS_BLOCK))), new WeightedListIntProvider(new DataPool.Builder<IntProvider>()
                .add(ConstantIntProvider.create(5), 500)
                .add(ConstantIntProvider.create(4), 100)
                .add(ConstantIntProvider.create(3), 50)
                .add(ConstantIntProvider.create(2), 25)
                .add(ConstantIntProvider.create(1), 1)
                .build()));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> registerConfigured(String name, F feature, FC config) {
        return BuiltinRegistries.addCasted(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(WondrousWilds.MOD_ID, name).toString(), new ConfiguredFeature<>(feature, config));
    }

    private static RegistryEntry<PlacedFeature> registerPlaced(String name, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, PlacementModifier... modifiers) {
        return registerPlaced(name, registryEntry, List.of(modifiers));
    }

    private static RegistryEntry<PlacedFeature> registerPlaced(String name, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, List<PlacementModifier> modifiers) {
        String id = new Identifier(WondrousWilds.MOD_ID, name).toString();
        return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, id, new PlacedFeature(RegistryEntry.upcast(registryEntry), List.copyOf(modifiers)));
    }

    public static void initialize() {
        Trees.TrunkPlacers.init();
        Trees.FoliagePlacers.init();
        Trees.Decorators.init();
        Trees.Configs.init();
        Trees.init();

        Registry.register(Registry.FEATURE, new Identifier(WondrousWilds.MOD_ID, "random_group_selector"), RANDOM_GROUP_SELECTOR);
        Registry.register(Registry.FEATURE, new Identifier(WondrousWilds.MOD_ID, "random_weighted_group_selector"), RANDOM_WEIGHTED_GROUP_SELECTOR);

        Registry.register(Registry.FEATURE, new Identifier(WondrousWilds.MOD_ID, "terrain_splotch"), TERRAIN_SPLOTCH);
        Registry.register(Registry.FEATURE, new Identifier(WondrousWilds.MOD_ID, "boulder"), BOULDER);

        Registry.register(Registry.FEATURE, new Identifier(WondrousWilds.MOD_ID, "fallen_log"), FALLEN_LOG);

        Registry.register(Registry.FEATURE, new Identifier(WondrousWilds.MOD_ID, "violet_patch"), VIOLET_PATCH);

        Registry.register(Registry.FEATURE, new Identifier(WondrousWilds.MOD_ID, "fallen_leaves_patch"), COVER_SURFACE_WITH_FALLEN_LEAVES);
    }
}
