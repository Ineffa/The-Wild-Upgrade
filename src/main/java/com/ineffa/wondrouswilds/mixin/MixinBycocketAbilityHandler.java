package com.ineffa.wondrouswilds.mixin;

import com.ineffa.wondrouswilds.entities.BycocketUser;
import com.ineffa.wondrouswilds.entities.projectiles.CanSharpshot;
import com.ineffa.wondrouswilds.registry.WondrousWildsParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
 * <p> Handles most of the triggering and application of Bycocket abilities through projectile entities.
 * <p> Sharpshot detection/registration is performed through {@link ProjectileUtilMixin#detectAndRegisterSharpshots}
 */
@Mixin(ProjectileEntity.class)
public abstract class MixinBycocketAbilityHandler extends Entity implements CanSharpshot {

    private MixinBycocketAbilityHandler(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract @Nullable Entity getOwner();

    /**
     * Cancels out any spread/inaccuracy being applied to a projectile that is shot by a Bycocket user, when possible.
     */
    @ModifyVariable(method = "setVelocity(DDDFF)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    public float preventSpreadWithBycocket(float divergence) {
        if (this.getOwner() instanceof BycocketUser bycocketUser && bycocketUser.wondrouswilds$isAccurateWith((ProjectileEntity) (Object) this))
            return 0.0F;

        return divergence;
    }

    @Override
    public boolean canLandSharpshot() {
        return this.getOwner() instanceof BycocketUser owner && owner.wondrouswilds$canSharpshotWith((ProjectileEntity) (Object) this);
    }

    @Unique
    private boolean hasRegisteredSharpshot = false;

    @Override
    public boolean wondrouswilds$hasRegisteredSharpshot() {
        return this.hasRegisteredSharpshot;
    }

    @Override
    public void registerSharpshot() {
        this.hasRegisteredSharpshot = true;

        if (!this.getWorld().isClient()) {
            this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 2.0F);

            if (this.getWorld() instanceof ServerWorld serverWorld) serverWorld.spawnParticles(WondrousWildsParticles.SHARPSHOT_HIT, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
