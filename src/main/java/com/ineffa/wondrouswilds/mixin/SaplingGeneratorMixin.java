package com.ineffa.wondrouswilds.mixin;

import com.ineffa.wondrouswilds.registry.WondrousWildsFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(SaplingGenerator.class)
public class SaplingGeneratorMixin {

    @ModifyVariable(method = "generate", at = @At("STORE"), ordinal = 0)
    private @Nullable RegistryEntry<? extends ConfiguredFeature<?, ?>> generateAutumnBirches(@Nullable RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
        boolean bees = registryEntry == WondrousWildsFeatures.Trees.FANCY_BIRCH_WITH_BEES_CONFIGURED;
        if (bees || registryEntry == WondrousWildsFeatures.Trees.FANCY_BIRCH_CONFIGURED) {
            Optional<RegistryKey<Biome>> biomeKey = world.getBiome(pos).getKey();
            if (biomeKey.isPresent() && biomeKey.get() == BiomeKeys.OLD_GROWTH_BIRCH_FOREST) {
                registryEntry = switch (random.nextInt(4)) {
                    default -> registryEntry;
                    case 1 -> bees ? WondrousWildsFeatures.Trees.YELLOW_FANCY_BIRCH_WITH_BEES_CONFIGURED : WondrousWildsFeatures.Trees.YELLOW_FANCY_BIRCH_CONFIGURED;
                    case 2 -> bees ? WondrousWildsFeatures.Trees.ORANGE_FANCY_BIRCH_WITH_BEES_CONFIGURED : WondrousWildsFeatures.Trees.ORANGE_FANCY_BIRCH_CONFIGURED;
                    case 3 -> bees ? WondrousWildsFeatures.Trees.RED_FANCY_BIRCH_WITH_BEES_CONFIGURED : WondrousWildsFeatures.Trees.RED_FANCY_BIRCH_CONFIGURED;
                };
            }
        }
        return registryEntry;
    }
}
