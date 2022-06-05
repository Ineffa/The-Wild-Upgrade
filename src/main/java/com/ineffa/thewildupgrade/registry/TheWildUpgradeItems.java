package com.ineffa.thewildupgrade.registry;

import com.ineffa.thewildupgrade.TheWildUpgrade;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TheWildUpgradeItems {

    public static final Item FIREFLY_SPAWN_EGG = new SpawnEggItem(TheWildUpgradeEntities.FIREFLY, 2563094, 14876540, new Item.Settings().group(ItemGroup.MISC));

    public static final BlockItem SMALL_POLYPORE = new BlockItem(TheWildUpgradeBlocks.SMALL_POLYPORE, new FabricItemSettings().group(ItemGroup.DECORATIONS));
    public static final BlockItem BIG_POLYPORE = new BlockItem(TheWildUpgradeBlocks.BIG_POLYPORE, new FabricItemSettings().group(ItemGroup.DECORATIONS));

    public static final BlockItem PURPLE_VIOLET = new BlockItem(TheWildUpgradeBlocks.PURPLE_VIOLET, new FabricItemSettings().group(ItemGroup.DECORATIONS));
    public static final BlockItem PINK_VIOLET = new BlockItem(TheWildUpgradeBlocks.PINK_VIOLET, new FabricItemSettings().group(ItemGroup.DECORATIONS));
    public static final BlockItem RED_VIOLET = new BlockItem(TheWildUpgradeBlocks.RED_VIOLET, new FabricItemSettings().group(ItemGroup.DECORATIONS));
    public static final BlockItem WHITE_VIOLET = new BlockItem(TheWildUpgradeBlocks.WHITE_VIOLET, new FabricItemSettings().group(ItemGroup.DECORATIONS));

    public static void initialize() {
        Registry.register(Registry.ITEM, new Identifier(TheWildUpgrade.MOD_ID, "firefly_spawn_egg"), FIREFLY_SPAWN_EGG);

        Registry.register(Registry.ITEM, new Identifier(TheWildUpgrade.MOD_ID, "small_polypore"), SMALL_POLYPORE);
        Registry.register(Registry.ITEM, new Identifier(TheWildUpgrade.MOD_ID, "big_polypore"), BIG_POLYPORE);

        Registry.register(Registry.ITEM, new Identifier(TheWildUpgrade.MOD_ID, "purple_violet"), PURPLE_VIOLET);
        Registry.register(Registry.ITEM, new Identifier(TheWildUpgrade.MOD_ID, "pink_violet"), PINK_VIOLET);
        Registry.register(Registry.ITEM, new Identifier(TheWildUpgrade.MOD_ID, "red_violet"), RED_VIOLET);
        Registry.register(Registry.ITEM, new Identifier(TheWildUpgrade.MOD_ID, "white_violet"), WHITE_VIOLET);
    }
}
