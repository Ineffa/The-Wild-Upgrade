package com.ineffa.wondrouswilds.mixin.client;

import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageHolder;
import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow private @Nullable ClientWorld world;

    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;checkEmpty(Lnet/minecraft/client/util/math/MatrixStack;)V", ordinal = 2, shift = At.Shift.BEFORE))
    private void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo callback) {
        BlockDamageHolder blockDamageHolder = (BlockDamageHolder) this.world;
        if (blockDamageHolder != null) {
            for (Map.Entry<Long, BlockDamageInstance> blockDamageEntry : blockDamageHolder.getBlockDamageInstanceMap().entrySet()) {
                BlockPos blockPos = BlockPos.fromLong(blockDamageEntry.getKey());
                int damageStage = blockDamageEntry.getValue().getStage() - 1;

                matrices.push();
                matrices.translate((double) blockPos.getX() - camera.getPos().getX(), (double) blockPos.getY() - camera.getPos().getY(), (double) blockPos.getZ() - camera.getPos().getZ());
                MatrixStack.Entry matrixStackEntry = matrices.peek();
                OverlayVertexConsumer vertexConsumer2 = new OverlayVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(damageStage)), matrixStackEntry.getPositionMatrix(), matrixStackEntry.getNormalMatrix());
                this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(blockPos), blockPos, this.world, matrices, vertexConsumer2);
                matrices.pop();
            }
        }
    }
}
