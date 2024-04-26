package com.ineffa.wondrouswilds.world.features;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;

public class FeatureGroup {

    public static final Codec<FeatureGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlacedFeature.REGISTRY_CODEC.listOf().fieldOf("features").forGetter(featureGroup -> featureGroup.features)
    ).apply(instance, FeatureGroup::new));

    public final ImmutableList<RegistryEntry<PlacedFeature>> features;

    public FeatureGroup(List<RegistryEntry<PlacedFeature>> features) {
        this.features = ImmutableList.copyOf(features);
    }

    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
        return this.features.get(random.nextInt(this.features.size())).value().generateUnregistered(world, chunkGenerator, random, pos);
    }

    public static class RandomEntry extends FeatureGroup {

        public static final Codec<RandomEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PlacedFeature.REGISTRY_CODEC.listOf().fieldOf("features").forGetter(config -> config.features),
                Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(config -> config.chance)
        ).apply(instance, RandomEntry::new));

        public final float chance;

        public RandomEntry(List<RegistryEntry<PlacedFeature>> features, float chance) {
            super(features);
            this.chance = chance;
        }
    }
}
