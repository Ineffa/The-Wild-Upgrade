package com.ineffa.wondrouswilds.client.rendering.entity.feature;

import com.google.common.collect.ImmutableList;
import com.ineffa.wondrouswilds.WondrousWilds;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class BycocketEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    public static final EntityModelLayer BYCOCKET_MODEL_LAYER = new EntityModelLayer(new Identifier(WondrousWilds.MOD_ID, "bycocket"), "main");

    private final ModelPart cap;

    public BycocketEntityModel(ModelPart root) {
        this.cap = root.getChild("cap");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData cap = modelPartData.addChild("cap", ModelPartBuilder.create()
                .uv(42, 22).cuboid(-5.0F, -5.0F, 5.0F, 10.0F, 5.0F, 0.0F)
                .uv(22, 7).cuboid(5.0F, -5.0F, -5.0F, 0.0F, 5.0F, 10.0F)
                .uv(42, 7).cuboid(-5.0F, -5.0F, -5.0F, 0.0F, 5.0F, 10.0F)
                .uv(42, 27).cuboid(-5.0F, -5.0F, -5.0F, 10.0F, 5.0F, 0.0F)
                .uv(12, 22).cuboid(-5.0F, -5.0F, -5.0F, 10.0F, 0.0F, 10.0F),
                ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0567F, 0.0F, 0.0F)
        );
        cap.addChild("feather", ModelPartBuilder.create()
                .uv(0, 3).cuboid(0.0F, -7.0F, -1.0F, 0.0F, 7.0F, 5.0F),
                ModelTransform.of(5.0F, -2.0F, 0.0F, -0.5236F, 0.1745F, 0.0873F)
        );

        ModelPartData brim = cap.addChild("brim", ModelPartBuilder.create()
                .uv(0, 19).cuboid(-5.5F, -2.0F, 5.5F, 11.0F, 2.0F, 0.0F)
                .uv(0, 4).cuboid(5.5F, -2.0F, -5.5F, 0.0F, 2.0F, 11.0F)
                .uv(0, 6).cuboid(-5.5F, -2.0F, -5.5F, 0.0F, 2.0F, 11.0F)
                .uv(-11, 21).cuboid(-5.5F, 0.001F, -5.5F, 11.0F, 0.0F, 11.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );
        brim.addChild("brimBottomFront", ModelPartBuilder.create()
                .uv(43, 10).cuboid(0.3857F, 0.0F, -7.3888F, 7.0F, 0.0F, 7.0F, new Dilation(0.3887F, 0.0F, 0.3887F)),
                ModelTransform.of(-5.5F, 0.0F, -5.5F, 0.0F, -0.7854F, 0.0F)
        );
        brim.addChild("brimLeftFront", ModelPartBuilder.create()
                .uv(22, 8).cuboid(0.0F, -1.0F, -7.3888F, 0.0F, 2.0F, 7.0F, new Dilation(0.0F, 0.0F, 0.3887F)),
                ModelTransform.of(5.5F, -1.0F, -5.5F, 0.0F, 0.7854F, 0.0F)
        );
        brim.addChild("brimRightFront", ModelPartBuilder.create()
                .uv(36, 8).cuboid(0.0F, -2.0F, -7.3888F, 0.0F, 2.0F, 7.0F, new Dilation(0.0F, 0.0F, 0.3887F)),
                ModelTransform.of(-5.5F, 0.0F, -5.5F, 0.0F, -0.7854F, 0.0F)
        );

        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.cap);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of();
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}
}
