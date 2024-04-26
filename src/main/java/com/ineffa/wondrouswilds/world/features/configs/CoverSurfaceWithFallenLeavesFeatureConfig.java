package com.ineffa.wondrouswilds.world.features.configs;

import com.ineffa.wondrouswilds.blocks.FallenLeavesBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record CoverSurfaceWithFallenLeavesFeatureConfig(BlockStateProvider fallenLeavesProvider, BlockPredicate canPlaceOn, IntProvider density) implements FeatureConfig {

    public static final Codec<IntProvider> DENSITY_CODEC = IntProvider.createValidatingCodec(FallenLeavesBlock.MIN_DENSITY, FallenLeavesBlock.MAX_DENSITY);

    public static final Codec<CoverSurfaceWithFallenLeavesFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("fallen_leaves_provider").forGetter(config -> config.fallenLeavesProvider),
            BlockPredicate.BASE_CODEC.fieldOf("can_place_on").forGetter(config -> config.canPlaceOn),
            DENSITY_CODEC.fieldOf("density").forGetter(config -> config.density)
    ).apply(instance, CoverSurfaceWithFallenLeavesFeatureConfig::new));
}
