package com.ineffa.wondrouswilds.mixin.common;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ComposterBlock.class)
public interface ComposterBlockInvoker {

    @Invoker("registerCompostableItem")
    static void addCompostableItem(float levelIncreaseChance, ItemConvertible item) {
        throw new AssertionError();
    }
}
