package com.ineffa.wondrouswilds.client.rendering.entity;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class WoodpeckerRenderer extends ExtendedGeoEntityRenderer<WoodpeckerEntity> {

    public static final String HELD_ITEM_BONE_NAME = "heldItem";

    public WoodpeckerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new WoodpeckerModel());

        this.shadowRadius = 0.225F;
    }

    @Override
    public RenderLayer getRenderType(WoodpeckerEntity entity, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(this.getTextureResource(entity));
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, WoodpeckerEntity currentEntity) {
        if (boneName.equals(HELD_ITEM_BONE_NAME)) return currentEntity.getMainHandStack();

        return null;
    }

    @Override
    protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
        if (boneName.equals(HELD_ITEM_BONE_NAME)) return ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;

        return ModelTransformation.Mode.NONE;
    }

    @Override
    protected void preRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, WoodpeckerEntity currentEntity, IBone bone) {
        if (item == currentEntity.getMainHandStack()) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrixStack.translate(0.0D, 0.0D, -0.035D);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, WoodpeckerEntity currentEntity, IBone bone) {}

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName, WoodpeckerEntity currentEntity) {
        return null;
    }

    @Override
    protected void preRenderBlock(MatrixStack matrixStack, BlockState block, String boneName, WoodpeckerEntity currentEntity) {}

    @Override
    protected void postRenderBlock(MatrixStack matrixStack, BlockState block, String boneName, WoodpeckerEntity currentEntity) {}

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Nullable
    @Override
    protected Identifier getTextureForBone(String boneName, WoodpeckerEntity currentEntity) {
        return null;
    }
}
