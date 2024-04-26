package com.ineffa.wondrouswilds.registry;

import com.ineffa.wondrouswilds.WondrousWilds;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class WondrousWildsTags {

    public static class BlockTags {
        public static final TagKey<Block> VIOLETS = createBlockTag("violets");
        public static final TagKey<Block> WOODPECKERS_INTERACT_WITH = createBlockTag("woodpeckers_interact_with");
        public static final TagKey<Block> WOODPECKER_BONDING_BLOCKS = createBlockTag("woodpecker_bonding_blocks");

        public static final TagKey<Block> FIREFLIES_SPAWNABLE_ON = createBlockTag("fireflies_spawnable_on");
        public static final TagKey<Block> FIREFLIES_HIDE_IN = createBlockTag("fireflies_hide_in");

        public static final TagKey<Block> BOULDERS_CANNOT_REPLACE = createBlockTag("boulders_cannot_replace");
    }

    public static class ItemTags {
        public static final TagKey<Item> VIOLETS = createItemTag("violets");
        public static final TagKey<Item> BYCOCKETS = createItemTag("bycockets");
    }

    public static class EntityTypeTags {
        public static final TagKey<EntityType<?>> DEFAULT_BYCOCKET_PROJECTILES = createEntityTypeTag("default_bycocket_projectiles");
    }

    public static class BiomeTags {
        public static final TagKey<Biome> SPAWNS_FIREFLIES_ON_SURFACE = createBiomeTag("spawns_fireflies_on_surface");
        public static final TagKey<Biome> SPAWNS_FIREFLIES_ON_SURFACE_ONLY_IN_RAIN = createBiomeTag("spawns_fireflies_on_surface_only_in_rain");
        public static final TagKey<Biome> SPAWNS_FIREFLIES_UNDERGROUND = createBiomeTag("spawns_fireflies_underground");

        public static final TagKey<Biome> HAS_FALLEN_LEAVES_LAYER = createBiomeTag("has_fallen_leaves_layer");
    }

    private static TagKey<Block> createBlockTag(String name) {
        return TagKey.of(Registry.BLOCK_KEY, new Identifier(WondrousWilds.MOD_ID, name));
    }

    private static TagKey<Item> createItemTag(String name) {
        return TagKey.of(Registry.ITEM_KEY, new Identifier(WondrousWilds.MOD_ID, name));
    }

    private static TagKey<EntityType<?>> createEntityTypeTag(String name) {
        return TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WondrousWilds.MOD_ID, name));
    }

    private static TagKey<Biome> createBiomeTag(String name) {
        return TagKey.of(Registry.BIOME_KEY, new Identifier(WondrousWilds.MOD_ID, name));
    }
}
