package com.ineffa.wondrouswilds.mixin.common;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LandPathNodeMaker.class)
public interface LandPathNodeMakerAccessor {

    @Accessor
    Object2BooleanMap<Box> getCollidedBoxes();
}
