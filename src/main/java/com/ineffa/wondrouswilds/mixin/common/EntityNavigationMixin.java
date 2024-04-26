package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.ai.navigation.HasCustomReachedDestinationDistance;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityNavigation.class)
public abstract class EntityNavigationMixin {

    @Shadow @Final protected MobEntity entity;

    @Shadow protected float nodeReachProximity;

    @Shadow public abstract @Nullable Path getCurrentPath();

    @Redirect(method = "continueFollowingPath", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/pathing/EntityNavigation;nodeReachProximity:F", opcode = Opcodes.PUTFIELD))
    private void modifyReachedNodeDistance(EntityNavigation instance, float value) {
        if (this.getCurrentPath().getCurrentNodeIndex() == this.getCurrentPath().getLength() - 1 && this.entity instanceof HasCustomReachedDestinationDistance customReachedDestinationDistance) this.nodeReachProximity = customReachedDestinationDistance.getReachedDestinationDistance();
    }
}
