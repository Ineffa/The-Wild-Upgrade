package com.ineffa.wondrouswilds.client.rendering.entity.feature;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.items.BycocketItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
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
        if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
            matrixStack.translate(0.0D, 0.03125D, 0.0D);
            matrixStack.scale(0.7F, 0.7F, 0.7F);
            matrixStack.translate(0.0D, 1.0D, 0.0D);
        }
        this.getContextModel().copyStateTo(this.bycocketModel);
        ((ModelWithHead) this.getContextModel()).getHead().rotate(matrixStack);
        this.bycocketModel.render(matrixStack, ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getEntityCutout(texture), false, itemStack.hasGlint()), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
    }
}
