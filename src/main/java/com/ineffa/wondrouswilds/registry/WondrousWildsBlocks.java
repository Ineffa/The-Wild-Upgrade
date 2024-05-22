package com.ineffa.wondrouswilds.registry;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.blocks.*;
import com.ineffa.wondrouswilds.blocks.entity.NestBoxBlockEntity;
import com.ineffa.wondrouswilds.blocks.entity.TreeHollowBlockEntity;
import com.ineffa.wondrouswilds.mixin.common.FireBlockInvoker;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WondrousWildsBlocks {

    public static final Block PURPLE_VIOLET = registerBlock("purple_violet", new VioletBlock(FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS)));
    public static final Block PINK_VIOLET = registerBlock("pink_violet", new VioletBlock(FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS)));
    public static final Block RED_VIOLET = registerBlock("red_violet", new VioletBlock(FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS)));
    public static final Block WHITE_VIOLET = registerBlock("white_violet", new VioletBlock(FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS)));
    public static final Block POTTED_PURPLE_VIOLET = registerBlock("potted_purple_violet", new FlowerPotBlock(PURPLE_VIOLET, FabricBlockSettings.of(Material.DECORATION).breakInstantly().nonOpaque()));
    public static final Block POTTED_PINK_VIOLET = registerBlock("potted_pink_violet", new FlowerPotBlock(PINK_VIOLET, FabricBlockSettings.of(Material.DECORATION).breakInstantly().nonOpaque()));
    public static final Block POTTED_RED_VIOLET = registerBlock("potted_red_violet", new FlowerPotBlock(RED_VIOLET, FabricBlockSettings.of(Material.DECORATION).breakInstantly().nonOpaque()));
    public static final Block POTTED_WHITE_VIOLET = registerBlock("potted_white_violet", new FlowerPotBlock(WHITE_VIOLET, FabricBlockSettings.of(Material.DECORATION).breakInstantly().nonOpaque()));

    public static final Block SMALL_POLYPORE = registerBlock("small_polypore", new SmallPolyporeBlock(FabricBlockSettings.of(Material.PLANT, MapColor.BROWN).sounds(BlockSoundGroup.GRASS).nonOpaque().breakInstantly().noCollision()));
    public static final Block BIG_POLYPORE = registerBlock("big_polypore", new BigPolyporeBlock(FabricBlockSettings.of(Material.PLANT, MapColor.BROWN).sounds(BlockSoundGroup.GRASS).nonOpaque().breakInstantly()));

    public static final Block BUSH = registerBlock("bush", new FernBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.SWEET_BERRY_BUSH).offsetType(AbstractBlock.OffsetType.XYZ)));
    public static final Block IVY = registerBlock("ivy", new IvyBlock(FabricBlockSettings.of(Material.REPLACEABLE_PLANT, MapColor.LIME).noCollision().strength(0.2F).sounds(BlockSoundGroup.SMALL_DRIPLEAF)));

    public static final Block FALLEN_BIRCH_LEAVES = registerBlock("fallen_birch_leaves", new FallenLeavesBlock(FabricBlockSettings.of(Material.REPLACEABLE_PLANT, MapColor.YELLOW).noCollision().strength(0.1F).sounds(BlockSoundGroup.GRASS).offsetType(AbstractBlock.OffsetType.XYZ)));
    public static final Block YELLOW_BIRCH_LEAVES = registerBlock("yellow_birch_leaves", new SheddingLeavesBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LEAVES), WondrousWildsParticles.BIRCH_LEAF, 8, 0.917647F, 0.694117F, 0.172549F));
    public static final Block ORANGE_BIRCH_LEAVES = registerBlock("orange_birch_leaves", new SheddingLeavesBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LEAVES), WondrousWildsParticles.BIRCH_LEAF, 8, 0.945098F, 0.509803F, 0.164705F));
    public static final Block RED_BIRCH_LEAVES = registerBlock("red_birch_leaves", new SheddingLeavesBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LEAVES), WondrousWildsParticles.BIRCH_LEAF, 8, 0.968627F, 0.254901F, 0.188235F));

    public static final Block DEAD_OAK_LOG = registerBlock("dead_oak_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG)));
    public static final Block DEAD_SPRUCE_LOG = registerBlock("dead_spruce_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_LOG)));
    public static final Block DEAD_BIRCH_LOG = registerBlock("dead_birch_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LOG)));
    public static final Block DEAD_JUNGLE_LOG = registerBlock("dead_jungle_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_LOG)));
    public static final Block DEAD_ACACIA_LOG = registerBlock("dead_acacia_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.ACACIA_LOG)));
    public static final Block DEAD_DARK_OAK_LOG = registerBlock("dead_dark_oak_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.DARK_OAK_LOG)));
    public static final Block DEAD_MANGROVE_LOG = registerBlock("dead_mangrove_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.MANGROVE_LOG)));
    public static final Block DEAD_CRIMSON_STEM = registerBlock("dead_crimson_stem", new PillarBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_STEM)));
    public static final Block DEAD_WARPED_STEM = registerBlock("dead_warped_stem", new PillarBlock(FabricBlockSettings.copyOf(Blocks.WARPED_STEM)));

    public static final Block DEAD_OAK_WOOD = registerBlock("dead_oak_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
    public static final Block DEAD_SPRUCE_WOOD = registerBlock("dead_spruce_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_WOOD)));
    public static final Block DEAD_BIRCH_WOOD = registerBlock("dead_birch_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_WOOD)));
    public static final Block DEAD_JUNGLE_WOOD = registerBlock("dead_jungle_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_WOOD)));
    public static final Block DEAD_ACACIA_WOOD = registerBlock("dead_acacia_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.ACACIA_WOOD)));
    public static final Block DEAD_DARK_OAK_WOOD = registerBlock("dead_dark_oak_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.DARK_OAK_WOOD)));
    public static final Block DEAD_MANGROVE_WOOD = registerBlock("dead_mangrove_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.MANGROVE_WOOD)));
    public static final Block DEAD_CRIMSON_HYPHAE = registerBlock("dead_crimson_hyphae", new PillarBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_HYPHAE)));
    public static final Block DEAD_WARPED_HYPHAE = registerBlock("dead_warped_hyphae", new PillarBlock(FabricBlockSettings.copyOf(Blocks.WARPED_HYPHAE)));

    public static final Block HOLLOW_OAK_LOG = registerBlock("hollow_oak_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).nonOpaque()));
    public static final Block HOLLOW_SPRUCE_LOG = registerBlock("hollow_spruce_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_LOG).nonOpaque()));
    public static final Block HOLLOW_BIRCH_LOG = registerBlock("hollow_birch_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LOG).nonOpaque()));
    public static final Block HOLLOW_JUNGLE_LOG = registerBlock("hollow_jungle_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_LOG).nonOpaque()));
    public static final Block HOLLOW_ACACIA_LOG = registerBlock("hollow_acacia_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.ACACIA_LOG).nonOpaque()));
    public static final Block HOLLOW_DARK_OAK_LOG = registerBlock("hollow_dark_oak_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.DARK_OAK_LOG).nonOpaque()));
    public static final Block HOLLOW_MANGROVE_LOG = registerBlock("hollow_mangrove_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.MANGROVE_LOG).nonOpaque()));
    public static final Block HOLLOW_CRIMSON_STEM = registerBlock("hollow_crimson_stem", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_STEM).nonOpaque()));
    public static final Block HOLLOW_WARPED_STEM = registerBlock("hollow_warped_stem", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.WARPED_STEM).nonOpaque()));

    public static final Block HOLLOW_DEAD_OAK_LOG = registerBlock("hollow_dead_oak_log", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_OAK_LOG).nonOpaque()));
    public static final Block HOLLOW_DEAD_SPRUCE_LOG = registerBlock("hollow_dead_spruce_log", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_SPRUCE_LOG).nonOpaque()));
    public static final Block HOLLOW_DEAD_BIRCH_LOG = registerBlock("hollow_dead_birch_log", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_BIRCH_LOG).nonOpaque()));
    public static final Block HOLLOW_DEAD_JUNGLE_LOG = registerBlock("hollow_dead_jungle_log", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_JUNGLE_LOG).nonOpaque()));
    public static final Block HOLLOW_DEAD_ACACIA_LOG = registerBlock("hollow_dead_acacia_log", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_ACACIA_LOG).nonOpaque()));
    public static final Block HOLLOW_DEAD_DARK_OAK_LOG = registerBlock("hollow_dead_dark_oak_log", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_DARK_OAK_LOG).nonOpaque()));
    public static final Block HOLLOW_DEAD_MANGROVE_LOG = registerBlock("hollow_dead_mangrove_log", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_MANGROVE_LOG).nonOpaque()));
    public static final Block HOLLOW_DEAD_CRIMSON_STEM = registerBlock("hollow_dead_crimson_stem", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_CRIMSON_STEM).nonOpaque()));
    public static final Block HOLLOW_DEAD_WARPED_STEM = registerBlock("hollow_dead_warped_stem", new HollowLogBlock(FabricBlockSettings.copyOf(DEAD_WARPED_STEM).nonOpaque()));

    public static final Block HOLLOW_STRIPPED_OAK_LOG = registerBlock("hollow_stripped_oak_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_LOG).nonOpaque()));
    public static final Block HOLLOW_STRIPPED_SPRUCE_LOG = registerBlock("hollow_stripped_spruce_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_SPRUCE_LOG).nonOpaque()));
    public static final Block HOLLOW_STRIPPED_BIRCH_LOG = registerBlock("hollow_stripped_birch_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_BIRCH_LOG).nonOpaque()));
    public static final Block HOLLOW_STRIPPED_JUNGLE_LOG = registerBlock("hollow_stripped_jungle_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_JUNGLE_LOG).nonOpaque()));
    public static final Block HOLLOW_STRIPPED_ACACIA_LOG = registerBlock("hollow_stripped_acacia_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_ACACIA_LOG).nonOpaque()));
    public static final Block HOLLOW_STRIPPED_DARK_OAK_LOG = registerBlock("hollow_stripped_dark_oak_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_DARK_OAK_LOG).nonOpaque()));
    public static final Block HOLLOW_STRIPPED_MANGROVE_LOG = registerBlock("hollow_stripped_mangrove_log", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_MANGROVE_LOG).nonOpaque()));
    public static final Block HOLLOW_STRIPPED_CRIMSON_STEM = registerBlock("hollow_stripped_crimson_stem", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_CRIMSON_STEM).nonOpaque()));
    public static final Block HOLLOW_STRIPPED_WARPED_STEM = registerBlock("hollow_stripped_warped_stem", new HollowLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_WARPED_STEM).nonOpaque()));

    public static final TreeHollowBlock OAK_TREE_HOLLOW = (TreeHollowBlock) registerBlock("oak_tree_hollow", new TreeHollowBlock(FabricBlockSettings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final TreeHollowBlock SPRUCE_TREE_HOLLOW = (TreeHollowBlock) registerBlock("spruce_tree_hollow", new TreeHollowBlock(FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final TreeHollowBlock BIRCH_TREE_HOLLOW = (TreeHollowBlock) registerBlock("birch_tree_hollow", new TreeHollowBlock(FabricBlockSettings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final TreeHollowBlock JUNGLE_TREE_HOLLOW = (TreeHollowBlock) registerBlock("jungle_tree_hollow", new TreeHollowBlock(FabricBlockSettings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final TreeHollowBlock ACACIA_TREE_HOLLOW = (TreeHollowBlock) registerBlock("acacia_tree_hollow", new TreeHollowBlock(FabricBlockSettings.of(Material.WOOD, MapColor.ORANGE).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final TreeHollowBlock DARK_OAK_TREE_HOLLOW = (TreeHollowBlock) registerBlock("dark_oak_tree_hollow", new TreeHollowBlock(FabricBlockSettings.of(Material.WOOD, MapColor.BROWN).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final TreeHollowBlock MANGROVE_TREE_HOLLOW = (TreeHollowBlock) registerBlock("mangrove_tree_hollow", new TreeHollowBlock(FabricBlockSettings.of(Material.WOOD, MapColor.RED).strength(2.0f).sounds(BlockSoundGroup.WOOD)));

    public static final NestBoxBlock BIRCH_NEST_BOX = (NestBoxBlock) registerBlock("birch_nest_box", new BirchNestBoxBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_PLANKS).nonOpaque()));

    public static final class BlockEntities {
        public static final BlockEntityType<TreeHollowBlockEntity> TREE_HOLLOW = registerBlockEntity("tree_hollow", FabricBlockEntityTypeBuilder.create(TreeHollowBlockEntity::new,
                OAK_TREE_HOLLOW, SPRUCE_TREE_HOLLOW, BIRCH_TREE_HOLLOW, JUNGLE_TREE_HOLLOW, ACACIA_TREE_HOLLOW, DARK_OAK_TREE_HOLLOW, MANGROVE_TREE_HOLLOW
        ).build(null));

        public static final BlockEntityType<NestBoxBlockEntity> NEST_BOX = registerBlockEntity("nest_box", FabricBlockEntityTypeBuilder.create(NestBoxBlockEntity::new, BIRCH_NEST_BOX).build(null));

        private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> blockEntityType) {
            return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(WondrousWilds.MOD_ID, name), blockEntityType);
        }

        public static void init() {}
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(WondrousWilds.MOD_ID, name), block);
    }

    public static void initialize() {
        BlockEntities.init();
        registerStrippableBlocks();
        registerFlammableBlocks();
    }

    private static void registerStrippableBlocks() {
        StrippableBlockRegistry.register(HOLLOW_OAK_LOG, HOLLOW_STRIPPED_OAK_LOG);
        StrippableBlockRegistry.register(HOLLOW_SPRUCE_LOG, HOLLOW_STRIPPED_SPRUCE_LOG);
        StrippableBlockRegistry.register(HOLLOW_BIRCH_LOG, HOLLOW_STRIPPED_BIRCH_LOG);
        StrippableBlockRegistry.register(HOLLOW_JUNGLE_LOG, HOLLOW_STRIPPED_JUNGLE_LOG);
        StrippableBlockRegistry.register(HOLLOW_ACACIA_LOG, HOLLOW_STRIPPED_ACACIA_LOG);
        StrippableBlockRegistry.register(HOLLOW_DARK_OAK_LOG, HOLLOW_STRIPPED_DARK_OAK_LOG);
        StrippableBlockRegistry.register(HOLLOW_MANGROVE_LOG, HOLLOW_STRIPPED_MANGROVE_LOG);
        StrippableBlockRegistry.register(HOLLOW_CRIMSON_STEM, HOLLOW_STRIPPED_CRIMSON_STEM);
        StrippableBlockRegistry.register(HOLLOW_WARPED_STEM, HOLLOW_STRIPPED_WARPED_STEM);
    }

    private static void registerFlammableBlocks() {
        FireBlockInvoker fireBlock = (FireBlockInvoker) Blocks.FIRE;

        fireBlock.invokeRegisterFlammableBlock(PURPLE_VIOLET, 60, 100);
        fireBlock.invokeRegisterFlammableBlock(PINK_VIOLET, 60, 100);
        fireBlock.invokeRegisterFlammableBlock(RED_VIOLET, 60, 100);
        fireBlock.invokeRegisterFlammableBlock(WHITE_VIOLET, 60, 100);
        fireBlock.invokeRegisterFlammableBlock(BUSH, 60, 100);

        fireBlock.invokeRegisterFlammableBlock(IVY, 15, 100);

        fireBlock.invokeRegisterFlammableBlock(FALLEN_BIRCH_LEAVES, 30, 60);
        fireBlock.invokeRegisterFlammableBlock(YELLOW_BIRCH_LEAVES, 30, 60);
        fireBlock.invokeRegisterFlammableBlock(ORANGE_BIRCH_LEAVES, 30, 60);
        fireBlock.invokeRegisterFlammableBlock(RED_BIRCH_LEAVES, 30, 60);

        fireBlock.invokeRegisterFlammableBlock(DEAD_OAK_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_SPRUCE_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_BIRCH_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_JUNGLE_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_ACACIA_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_DARK_OAK_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_MANGROVE_LOG, 5, 5);

        fireBlock.invokeRegisterFlammableBlock(DEAD_OAK_WOOD, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_SPRUCE_WOOD, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_BIRCH_WOOD, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_JUNGLE_WOOD, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_ACACIA_WOOD, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_DARK_OAK_WOOD, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DEAD_MANGROVE_WOOD, 5, 5);

        fireBlock.invokeRegisterFlammableBlock(HOLLOW_OAK_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_SPRUCE_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_BIRCH_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_JUNGLE_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_ACACIA_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_DARK_OAK_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_MANGROVE_LOG, 5, 5);

        fireBlock.invokeRegisterFlammableBlock(HOLLOW_DEAD_OAK_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_DEAD_SPRUCE_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_DEAD_BIRCH_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_DEAD_JUNGLE_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_DEAD_ACACIA_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_DEAD_DARK_OAK_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_DEAD_MANGROVE_LOG, 5, 5);

        fireBlock.invokeRegisterFlammableBlock(HOLLOW_STRIPPED_OAK_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_STRIPPED_SPRUCE_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_STRIPPED_BIRCH_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_STRIPPED_JUNGLE_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_STRIPPED_ACACIA_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_STRIPPED_DARK_OAK_LOG, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(HOLLOW_STRIPPED_MANGROVE_LOG, 5, 5);

        fireBlock.invokeRegisterFlammableBlock(OAK_TREE_HOLLOW, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(SPRUCE_TREE_HOLLOW, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(BIRCH_TREE_HOLLOW, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(JUNGLE_TREE_HOLLOW, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(ACACIA_TREE_HOLLOW, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(DARK_OAK_TREE_HOLLOW, 5, 5);
        fireBlock.invokeRegisterFlammableBlock(MANGROVE_TREE_HOLLOW, 5, 5);

        fireBlock.invokeRegisterFlammableBlock(BIRCH_NEST_BOX, 5, 20);
    }
}
