package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.BycocketUser;
import com.ineffa.wondrouswilds.entities.projectiles.CanSharpshot;
import com.ineffa.wondrouswilds.registry.WondrousWildsParticles;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * @author Ineffa
 * <p> Manages most of the direct effects that Bycocket mechanics have on projectile functionality, as well as conditions for them to occur.
 * <p> Sharpshot detection/registration is performed through {@link ProjectileUtilMixin#detectAndRegisterSharpshots}
 */
@Mixin(ProjectileEntity.class)
public abstract class MixinBycocketProjectileManager extends Entity implements CanSharpshot {

    private MixinBycocketProjectileManager(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "setVelocity(DDDFF)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public float amplifySpeedWithBycocket(float speed) {
        if (this.getOwner() instanceof LivingEntity owner) {
            BycocketUser bycocketUser = (BycocketUser) owner;
            if (bycocketUser.wondrouswilds$isOvercharging())
                return speed * Math.min(1.0F + WondrousWildsUtils.normalizeValue(owner.getItemUseTime(), bycocketUser.wondrouswilds$getOverchargeStartDelay(), bycocketUser.wondrouswilds$getFullOverchargeThreshold()), 2.0F);
        }

        return speed;
    }

    @ModifyVariable(method = "setVelocity(DDDFF)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    public float preventSpreadWithBycocket(float divergence) {
        if (this.getOwner() instanceof BycocketUser bycocketUser && bycocketUser.wondrouswilds$isAccurateWith((ProjectileEntity) (Object) this))
            return 0.0F;

        return divergence;
    }

    @Override
    public boolean wondrouswilds$canLandSharpshot() {
        return this.getOwner() instanceof BycocketUser owner && owner.wondrouswilds$canSharpshotWith((ProjectileEntity) (Object) this);
    }

    @Unique
    private boolean hasRegisteredSharpshot = false;

    @Override
    public boolean wondrouswilds$hasRegisteredSharpshot() {
        return this.hasRegisteredSharpshot;
    }

    @Override
    public void wondrouswilds$registerSharpshot() {
        this.hasRegisteredSharpshot = true;

        if (!this.getWorld().isClient()) {
            this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 2.0F);

            if (this.getWorld() instanceof ServerWorld serverWorld) serverWorld.spawnParticles(WondrousWildsParticles.SHARPSHOT_HIT, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void wondrouswilds$unregisterSharpshot() {
        this.hasRegisteredSharpshot = false;
    }

    @Shadow public abstract @Nullable Entity getOwner();
}
