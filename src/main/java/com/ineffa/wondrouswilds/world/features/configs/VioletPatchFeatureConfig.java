package com.ineffa.wondrouswilds.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record VioletPatchFeatureConfig(BlockStateProvider violetProvider) implements FeatureConfig {

    public static final Codec<VioletPatchFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("violet_provider").forGetter(config -> config.violetProvider)
    ).apply(instance, VioletPatchFeatureConfig::new));
}
