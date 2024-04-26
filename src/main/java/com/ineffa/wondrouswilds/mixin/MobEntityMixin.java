package com.ineffa.wondrouswilds.mixin;

import com.ineffa.wondrouswilds.entities.BlockNester;
import com.ineffa.wondrouswilds.entities.FireflyEntity;
import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends Entity {

    private MobEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "updateGoalControls", at = @At("STORE"), ordinal = 0)
    private boolean stopPassengerMobsFromControlling(boolean bl) {
        Entity primaryPassenger = this.getPrimaryPassenger();
        return !(primaryPassenger instanceof MobEntity) || primaryPassenger instanceof FireflyEntity || primaryPassenger instanceof WoodpeckerEntity;
    }

    @Inject(method = "canMoveVoluntarily", at = @At("HEAD"), cancellable = true)
    private void canMoveVoluntarily(CallbackInfoReturnable<Boolean> callback) {
        if (this instanceof BlockNester nester && (nester.isPeekingOutOfNest() || nester.getCurrentNestTransition().isPresent())) callback.setReturnValue(false);
    }
}
