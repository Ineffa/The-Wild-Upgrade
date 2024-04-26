package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageHolder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {

    @Inject(method = "onBlockChanged", at = @At("HEAD"))
    private void onBlockChangeClearDamage(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo callback) {
        if (oldBlock.getBlock() != newBlock.getBlock() && this instanceof BlockDamageHolder blockDamageHolder) blockDamageHolder.removeDamageAtPos(pos);
    }
}
