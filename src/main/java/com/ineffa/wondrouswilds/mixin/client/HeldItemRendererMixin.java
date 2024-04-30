package com.ineffa.wondrouswilds.mixin.client;

import com.ineffa.wondrouswilds.entities.BycocketUser;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F", ordinal = 4))
    private float bycocketHandAnimations(float g, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float shakeMultiplier = 1.0F;
        float shakeSpeedMultiplier = 1.0F;

        BycocketUser bycocketUser = (BycocketUser) player;
        if (bycocketUser.wondrouswilds$isFullyOvercharged()) {
            shakeMultiplier += 0.3F;
            shakeSpeedMultiplier += 2.0F;
        }
        else if (bycocketUser.wondrouswilds$isOvercharging()) {
            float concentrationProgress = WondrousWildsUtils.normalizeValue(player.getItemUseTime(), bycocketUser.wondrouswilds$getOverchargeStartDelay(), bycocketUser.wondrouswilds$getFullOverchargeThreshold());
            shakeMultiplier += concentrationProgress * 0.2F;
            shakeSpeedMultiplier += concentrationProgress * 0.6F;
        }

        float m = (float) item.getMaxUseTime() - ((float) player.getItemUseTimeLeft() - tickDelta + 1.0F);
        return MathHelper.sin((m - 0.1F) * (1.3F * shakeSpeedMultiplier)) * shakeMultiplier;
    }
}
