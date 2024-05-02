package com.ineffa.wondrouswilds.client.rendering.entity.feature;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.client.rendering.WondrousWildsRenderLayers;
import com.ineffa.wondrouswilds.items.BycocketItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class BycocketFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    private final BycocketEntityModel<T> bycocketModel;

    public BycocketFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.bycocketModel = new BycocketEntityModel<>(loader.getModelPart(BycocketEntityModel.BYCOCKET_MODEL_LAYER));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
        if (!(itemStack.getItem() instanceof BycocketItem bycocketItem)) return;

        Identifier texture = new Identifier(WondrousWilds.MOD_ID, "textures/entity/bycocket/bycocket_" + bycocketItem.flair.name + ".png");

        matrixStack.push();
        this.getContextModel().copyStateTo(this.bycocketModel);
        ((ModelWithHead) this.getContextModel()).getHead().rotate(matrixStack);
        this.bycocketModel.render(matrixStack, vertexConsumerProvider.getBuffer(WondrousWildsRenderLayers.getArmorCutoutCull(texture)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        if (itemStack.hasGlint()) this.bycocketModel.render(matrixStack, vertexConsumerProvider.getBuffer(WondrousWildsRenderLayers.ARMOR_ENTITY_GLINT_CULL), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
    }
}
