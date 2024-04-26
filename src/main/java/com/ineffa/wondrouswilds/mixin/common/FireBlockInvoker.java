package com.ineffa.wondrouswilds.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockInvoker {

    @Invoker("registerFlammableBlock")
    void invokeRegisterFlammableBlock(Block block, int burnChance, int spreadChance);
}
