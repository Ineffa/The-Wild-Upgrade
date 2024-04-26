package com.ineffa.wondrouswilds.mixin.client;

import com.ineffa.wondrouswilds.entities.CanTakeSharpshots;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderHitbox", at = @At("TAIL"))
    private static void addSharpshotHitboxDebugVisualization(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo callback) {
        if (entity instanceof CanTakeSharpshots canTakeSharpshots) {
            double centerY = MathHelper.lerp(0.5D, entity.getBoundingBox().minY, entity.getBoundingBox().maxY);
            double maxDistanceFromCenterY = canTakeSharpshots.wondrouswilds$getMaxVerticalDistanceForSharpshot();
            WorldRenderer.drawBox(matrices, vertices, new Box(entity.getBoundingBox().minX, centerY - maxDistanceFromCenterY, entity.getBoundingBox().minZ, entity.getBoundingBox().maxX, centerY + maxDistanceFromCenterY, entity.getBoundingBox().maxZ).offset(entity.getPos().negate()), 0.5F, 0.0F, 0.0F, 0.2F);
        }
    }
}
