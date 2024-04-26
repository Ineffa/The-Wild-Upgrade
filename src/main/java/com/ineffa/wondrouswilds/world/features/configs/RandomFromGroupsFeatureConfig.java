package com.ineffa.wondrouswilds.world.features.configs;

import com.ineffa.wondrouswilds.world.features.FeatureGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record RandomFromGroupsFeatureConfig(List<FeatureGroup.RandomEntry> randomEntries, FeatureGroup defaultGroup) implements FeatureConfig {

    public static final Codec<RandomFromGroupsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FeatureGroup.RandomEntry.CODEC.listOf().fieldOf("random_groups").forGetter(config -> config.randomEntries),
            FeatureGroup.CODEC.fieldOf("default_group").forGetter(config -> config.defaultGroup)
    ).apply(instance, RandomFromGroupsFeatureConfig::new));

    @Override
    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        List<ConfiguredFeature<?, ?>> randomGroupFeatures = new ArrayList<>();
        this.randomEntries.forEach(randomGroupEntry -> randomGroupFeatures.addAll(randomGroupEntry.features.stream().flatMap(feature -> feature.value().getDecoratedFeatures()).toList()));
        return Stream.concat(randomGroupFeatures.stream(), this.defaultGroup.features.stream().flatMap(feature -> feature.value().getDecoratedFeatures()));
    }
}
