package com.ineffa.wondrouswilds.mixin.client;

import com.ineffa.wondrouswilds.entities.BycocketUser;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {

    @ModifyVariable(method = "getFovMultiplier", at = @At("STORE"), ordinal = 0)
    private float applyBycocketOverchargeFovAnimations(float f) {
        BycocketUser bycocketUser = (BycocketUser) this;

        if (bycocketUser.wondrouswilds$isFullyOvercharged()) f *= 0.85F;

        else if (bycocketUser.wondrouswilds$isOvercharging()) {
            float overchargeProgress = WondrousWildsUtils.normalizeValue(((LivingEntity) (Object) this).getItemUseTime(), bycocketUser.wondrouswilds$getOverchargeStartDelay(), bycocketUser.wondrouswilds$getFullOverchargeThreshold());
            f *= (1.0F - MathHelper.clamp(overchargeProgress * 0.1F, 0.0F, 0.1F));
        }

        return f;
    }
}
