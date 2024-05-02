package com.ineffa.wondrouswilds.client.rendering;

import com.ineffa.wondrouswilds.WondrousWilds;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class WondrousWildsRenderLayers extends RenderLayer {

    public WondrousWildsRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    private static final Function<Identifier, RenderLayer> TRANSLUCENT_GLOW = Util.memoize(texture -> {
        RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
        return RenderLayer.of(createId("translucent_glow"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, MultiPhaseParameters.builder().shader(EYES_SHADER).texture(texture2).transparency(TRANSLUCENT_TRANSPARENCY).writeMaskState(COLOR_MASK).build(false));
    });

    private static final Function<Identifier, RenderLayer> ARMOR_CUTOUT_CULL = Util.memoize(texture -> {
        MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder().shader(ARMOR_CUTOUT_NO_CULL_SHADER).texture(new RenderPhase.Texture(texture, false, false)).transparency(NO_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING).build(true);
        return RenderLayer.of(createId("armor_cutout_cull"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters);
    });

    public static final RenderLayer ARMOR_ENTITY_GLINT_CULL = RenderLayer.of(createId("armor_entity_glint_cull"), VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, false, false, MultiPhaseParameters.builder().shader(ARMOR_ENTITY_GLINT_SHADER).texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false)).writeMaskState(COLOR_MASK).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY).texturing(ENTITY_GLINT_TEXTURING).layering(VIEW_OFFSET_Z_LAYERING).build(false));

    public static RenderLayer getTranslucentGlow(Identifier texture) {
        return TRANSLUCENT_GLOW.apply(texture);
    }

    public static RenderLayer getArmorCutoutCull(Identifier texture) {
        return ARMOR_CUTOUT_CULL.apply(texture);
    }

    private static String createId(String name) {
        return WondrousWilds.MOD_ID + ":" + name;
    }
}
