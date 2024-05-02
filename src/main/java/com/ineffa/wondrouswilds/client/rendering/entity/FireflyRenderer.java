package com.ineffa.wondrouswilds.client.rendering.entity;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.client.rendering.WondrousWildsRenderLayers;
import com.ineffa.wondrouswilds.entities.FireflyEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class FireflyRenderer extends GeoEntityRenderer<FireflyEntity> {

    public FireflyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new FireflyModel());
        this.addLayer(new FireflyGlowLayer(this));
        this.shadowRadius = 0.2F;
    }

    @Override
    public RenderLayer getRenderType(FireflyEntity entity, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(this.getTextureResource(entity));
    }

    private static class FireflyGlowLayer extends GeoLayerRenderer<FireflyEntity> {
        private static final Identifier GLOW_TEXTURE = new Identifier(WondrousWilds.MOD_ID, "textures/entity/firefly/firefly_glow.png");

        public FireflyGlowLayer(IGeoRenderer<FireflyEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLight, FireflyEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            this.renderModel(this.getEntityModel(), GLOW_TEXTURE, matrixStack, vertexConsumerProvider, packedLight, entity, partialTicks, 1.0F, 1.0F, ageInTicks);
        }

        @Override
        protected void renderModel(GeoModelProvider<FireflyEntity> modelProviderIn, Identifier textureLocationIn, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, FireflyEntity entityIn, float partialTicks, float red, float green, float ageInTicks) {
            GeoModel model = modelProviderIn.getModel(modelProviderIn.getModelResource(entityIn));
            RenderLayer renderType = getRenderType(textureLocationIn);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(renderType);

            float alpha = MathHelper.clamp((MathHelper.cos(ageInTicks * 0.1F) * 2.0F) - 1.0F, 0.0F, 1.0F);

            this.getRenderer().render(model, entityIn, partialTicks, renderType, matrixStackIn, bufferIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlay(entityIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
        }

        @Override
        public RenderLayer getRenderType(Identifier textureLocation) {
            return WondrousWildsRenderLayers.getTranslucentGlow(textureLocation);
        }
    }
}
