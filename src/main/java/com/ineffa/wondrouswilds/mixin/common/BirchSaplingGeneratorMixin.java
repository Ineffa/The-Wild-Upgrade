package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.registry.WondrousWildsFeatures;
import net.minecraft.block.sapling.BirchSaplingGenerator;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BirchSaplingGenerator.class)
public class BirchSaplingGeneratorMixin {

    @Inject(at = @At("HEAD"), method = "getTreeFeature", cancellable = true)
    private void addFancyBirch(Random random, boolean bees, CallbackInfoReturnable<RegistryEntry<? extends ConfiguredFeature<?, ?>>> callback) {
        if (random.nextInt(10) == 0) callback.setReturnValue(bees && random.nextInt(20) == 0 ? WondrousWildsFeatures.Trees.FANCY_BIRCH_WITH_BEES_CONFIGURED : WondrousWildsFeatures.Trees.FANCY_BIRCH_CONFIGURED);
    }
}
