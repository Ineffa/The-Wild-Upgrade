package com.ineffa.wondrouswilds.world.features;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.ArrayList;
import java.util.List;

public class WeightedFeatureGroup {

    public static final Codec<WeightedFeatureGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.mapPair(PlacedFeature.REGISTRY_CODEC.fieldOf("feature"), IntProvider.POSITIVE_CODEC.fieldOf("weight")).codec().listOf().fieldOf("entries").forGetter(weightedFeatureGroup -> weightedFeatureGroup.featureEntries)
    ).apply(instance, WeightedFeatureGroup::new));

    public final ImmutableList<Pair<RegistryEntry<PlacedFeature>, IntProvider>> featureEntries;

    public WeightedFeatureGroup(List<Pair<RegistryEntry<PlacedFeature>, IntProvider>> featureEntries) {
        this.featureEntries = ImmutableList.copyOf(featureEntries);
    }

    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
        List<Pair<RegistryEntry<PlacedFeature>, Integer>> preparedEntries = new ArrayList<>();
        for (Pair<RegistryEntry<PlacedFeature>, IntProvider> rawEntry : this.featureEntries) preparedEntries.add(new Pair<>(rawEntry.getFirst(), rawEntry.getSecond().get(random)));

        int completeWeight = 0;
        for (Pair<RegistryEntry<PlacedFeature>, Integer> preparedEntry : preparedEntries) {
            int weight = preparedEntry.getSecond();
            if (weight <= 0) return false;
            completeWeight += weight;
        }
        float f = random.nextFloat() * completeWeight;
        int weightCount = 0;
        for (Pair<RegistryEntry<PlacedFeature>, Integer> preparedEntry : preparedEntries) {
            weightCount += preparedEntry.getSecond();
            if (weightCount >= f) return preparedEntry.getFirst().value().generateUnregistered(world, chunkGenerator, random, pos);
        }

        return false;
    }

    public static class RandomEntry extends WeightedFeatureGroup {

        public static final Codec<WeightedFeatureGroup.RandomEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.mapPair(PlacedFeature.REGISTRY_CODEC.fieldOf("feature"), IntProvider.POSITIVE_CODEC.fieldOf("weight")).codec().listOf().fieldOf("entries").forGetter(weightedFeatureGroup -> weightedFeatureGroup.featureEntries),
                Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(entry -> entry.chance)
        ).apply(instance, WeightedFeatureGroup.RandomEntry::new));

        public final float chance;

        public RandomEntry(List<Pair<RegistryEntry<PlacedFeature>, IntProvider>> featureEntries, float chance) {
            super(featureEntries);
            this.chance = chance;
        }
    }
}
