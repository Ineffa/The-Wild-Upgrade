package com.ineffa.wondrouswilds.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class TerrainSplotchFeatureConfig implements FeatureConfig {

    public final BlockStateProvider splotchStateProvider;
    public final BlockPredicate replaceTargetPredicate;
    public final IntProvider horizontalRadius, verticalSpread;
    public final FloatProvider densityMultiplier;

    public static final Codec<TerrainSplotchFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("splotch_state_provider").forGetter(config -> config.splotchStateProvider),
            BlockPredicate.BASE_CODEC.fieldOf("replace_target_predicate").forGetter(config -> config.replaceTargetPredicate),
            IntProvider.createValidatingCodec(1, 16).fieldOf("horizontal_radius").forGetter(config -> config.horizontalRadius),
            IntProvider.POSITIVE_CODEC.fieldOf("vertical_spread").forGetter(config -> config.verticalSpread),
            FloatProvider.createValidatedCodec(0.0F, 1.0F).fieldOf("density_multiplier").forGetter(config -> config.densityMultiplier)
    ).apply(instance, TerrainSplotchFeatureConfig::new));

    public TerrainSplotchFeatureConfig(BlockStateProvider splotchStateProvider, BlockPredicate replaceTargetPredicate, IntProvider horizontalRadius, IntProvider verticalSpread, FloatProvider densityMultiplier) {
        this.splotchStateProvider = splotchStateProvider;
        this.replaceTargetPredicate = replaceTargetPredicate;

        this.horizontalRadius = horizontalRadius;
        this.verticalSpread = verticalSpread;

        this.densityMultiplier = densityMultiplier;
    }
}
