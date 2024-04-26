package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.CanTakeSharpshots;
import com.ineffa.wondrouswilds.entities.projectiles.CanSharpshot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    /**
     * <p> Detects and registers when a projectile should land a sharpshot.
     * <p> First, it will check that the projectile is capable of landing a sharpshot.
     * <p> Then, it will register a sharpshot if the projectile impacts within an entity's sharpshot height range.
     */
    @Inject(method = "getEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;F)Lnet/minecraft/util/hit/EntityHitResult;", at = @At("HEAD"), cancellable = true)
    private static void detectAndRegisterSharpshots(World world, Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, float f, CallbackInfoReturnable<@Nullable EntityHitResult> callback) {
        CanSharpshot sharpshotProjectile = entity instanceof CanSharpshot canSharpshot && canSharpshot.canLandSharpshot() ? canSharpshot : null;
        boolean registerSharpshot = false;

        double d = Double.MAX_VALUE;
        Entity impactedEntity = null;
        for (Entity potentialImpactedEntity : world.getOtherEntities(entity, box, predicate)) {
            double e;
            Optional<Vec3d> impactVec = potentialImpactedEntity.getBoundingBox().expand(f).raycast(min, max);
            if (impactVec.isEmpty() || !((e = min.squaredDistanceTo(impactVec.get())) < d)) continue;
            impactedEntity = potentialImpactedEntity;
            d = e;

            if (sharpshotProjectile != null) registerSharpshot = impactedEntity instanceof CanTakeSharpshots nextValidSharpshotTarget && nextValidSharpshotTarget.wondrouswilds$isValidHeightForSharpshot(impactVec.get().getY());
        }

        if (registerSharpshot) sharpshotProjectile.registerSharpshot();

        callback.setReturnValue(impactedEntity == null ? null : new EntityHitResult(impactedEntity));
    }
}
