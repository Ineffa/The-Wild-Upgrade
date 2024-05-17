package com.ineffa.wondrouswilds.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(LargeOakFoliagePlacer.class)
public abstract class LargeOakFoliagePlacerMixin extends BlobFoliagePlacer {

    private LargeOakFoliagePlacerMixin(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset, height);
    }

    @ModifyVariable(method = "generate", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private int addSizeVariation(int radius, @Local(ordinal = 0) Random random, @Local(ordinal = 0) int trunkHeight, @Local(ordinal = 3) LocalIntRef offset) {
        if (random.nextInt(6) == 0 && trunkHeight > 6) {
            ++radius;
            offset.set(offset.get() - 1);
        }
        return radius;
    }

    @Inject(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/foliage/LargeOakFoliagePlacer;generateSquare(Lnet/minecraft/world/TestableWorld;Ljava/util/function/BiConsumer;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/world/gen/feature/TreeFeatureConfig;Lnet/minecraft/util/math/BlockPos;IIZ)V", shift = At.Shift.AFTER))
    private void verticallyExtendLargeBlobs(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset, CallbackInfo callback, @Local(ordinal = 4) int i, @Local(ordinal = 5) int j) {
        if (radius != 3) return;

        if (i == offset) this.generateSquare(world, replacer, random, config, treeNode.getCenter(), j - 1, i + 1, treeNode.isGiantTrunk());
        else if (i == offset - foliageHeight) this.generateSquare(world, replacer, random, config, treeNode.getCenter(), j - 1, i - 1, treeNode.isGiantTrunk());
    }

    @Inject(method = "generate", at = @At("TAIL"))
    private void addVariationToCorners(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset, CallbackInfo callback) {
        if (radius != 2 || random.nextInt(3) != 0) return;

        BlockPos foliageCenter = treeNode.getCenter().up(foliageHeight / 2);
        placeFoliageBlock(world, replacer, random, config, foliageCenter.add(radius, 0, radius));
        placeFoliageBlock(world, replacer, random, config, foliageCenter.add(-radius, 0, -radius));
        placeFoliageBlock(world, replacer, random, config, foliageCenter.add(-radius, 0, radius));
        placeFoliageBlock(world, replacer, random, config, foliageCenter.add(radius, 0, -radius));
    }
}
