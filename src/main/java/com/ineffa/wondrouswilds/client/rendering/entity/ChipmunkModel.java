package com.ineffa.wondrouswilds.client.rendering.entity;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.entities.ChipmunkEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class ChipmunkModel extends AnimatedGeoModel<ChipmunkEntity> {

    public static final Identifier MODEL_PATH = new Identifier(WondrousWilds.MOD_ID, "geo/chipmunk.geo.json");
    public static final Identifier TEXTURE_PATH = new Identifier(WondrousWilds.MOD_ID, "textures/entity/chipmunk/chipmunk.png");
    public static final Identifier ANIMATION_PATH = new Identifier(WondrousWilds.MOD_ID, "animations/chipmunk.animation.json");

    @Override
    public Identifier getModelResource(ChipmunkEntity chipmunk) {
        return MODEL_PATH;
    }

    @Override
    public Identifier getTextureResource(ChipmunkEntity chipmunk) {
        return TEXTURE_PATH;
    }

    @Override
    public Identifier getAnimationResource(ChipmunkEntity chipmunk) {
        return ANIMATION_PATH;
    }

    @Override
    public void setMolangQueries(IAnimatable animatable, double currentTick) {
        super.setMolangQueries(animatable, currentTick);

        ChipmunkEntity chipmunk = (ChipmunkEntity) animatable;
        MolangParser parser = GeckoLibCache.getInstance().parser;
        float delta = MinecraftClient.getInstance().getTickDelta();

        parser.setValue("query.head_pitch", () -> MathHelper.lerp(delta, chipmunk.prevPitch, chipmunk.getPitch()));
        parser.setValue("query.head_yaw", () -> MathHelper.lerpAngleDegrees(delta, chipmunk.prevHeadYaw, chipmunk.getHeadYaw()) - MathHelper.lerpAngleDegrees(delta, chipmunk.prevBodyYaw, chipmunk.getBodyYaw()));

        float swing = chipmunk.limbAngle - chipmunk.limbDistance * (1.0F - delta);
        float swingAmount = MathHelper.lerp(delta, chipmunk.lastLimbDistance, chipmunk.limbDistance);

        parser.setValue("query.swing", () -> swing * (chipmunk.isBaby() ? 2.0D : 1.0D));
        parser.setValue("query.swing_amount", () -> Math.min(1.0D, swingAmount * 2.0D));
        parser.setValue("query.swing_multiplier", () -> 300.0D);

        parser.setValue("query.tilt_angle", () -> chipmunk.getMovementTiltAngle(delta));
        parser.setValue("query.tilt_angle_tail_upper", () -> chipmunk.getMovementTiltAngleTailUpper(delta));
        parser.setValue("query.tilt_angle_tail_lower", () -> chipmunk.getMovementTiltAngleTailLower(delta));
    }
}
