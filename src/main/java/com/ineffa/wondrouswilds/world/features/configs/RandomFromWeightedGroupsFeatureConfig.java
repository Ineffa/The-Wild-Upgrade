package com.ineffa.wondrouswilds.world.features.configs;

import com.ineffa.wondrouswilds.world.features.WeightedFeatureGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record RandomFromWeightedGroupsFeatureConfig(List<WeightedFeatureGroup.RandomEntry> randomEntries, WeightedFeatureGroup defaultGroup) implements FeatureConfig {

    public static final Codec<RandomFromWeightedGroupsFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WeightedFeatureGroup.RandomEntry.CODEC.listOf().fieldOf("random_groups").forGetter(config -> config.randomEntries),
            WeightedFeatureGroup.CODEC.fieldOf("default_group").forGetter(config -> config.defaultGroup)
    ).apply(instance, RandomFromWeightedGroupsFeatureConfig::new));

    @Override
    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        List<ConfiguredFeature<?, ?>> features = new ArrayList<>();
        this.randomEntries.forEach(weightedFeatureGroup -> features.addAll(weightedFeatureGroup.featureEntries.stream().flatMap(weightedEntry -> weightedEntry.getFirst().value().getDecoratedFeatures()).toList()));
        return features.stream();
    }
}
