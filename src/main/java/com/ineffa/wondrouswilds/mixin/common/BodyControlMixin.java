package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.FireflyEntity;
import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BodyControl.class)
public class BodyControlMixin {

    @Shadow @Final private MobEntity entity;

    @Inject(at = @At("HEAD"), method = "isIndependent", cancellable = true)
    private void stopSpecificMobsFromControlling(CallbackInfoReturnable<Boolean> callback) {
        Entity firstPassenger = this.entity.getFirstPassenger();
        if (firstPassenger instanceof FireflyEntity || firstPassenger instanceof WoodpeckerEntity) callback.setReturnValue(true);
    }
}
