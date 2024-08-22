package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.entities.ai.FoxTargetChipmunksGoal;
import com.ineffa.wondrouswilds.entities.ai.FoxTargetWoodpeckersGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin extends AnimalEntity {

    @Unique
    private Goal huntChipmunksGoal, huntWoodpeckersGoal;

    private FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/passive/FoxEntity;followChickenAndRabbitGoal:Lnet/minecraft/entity/ai/goal/Goal;", shift = At.Shift.AFTER))
    private void initializeNewGoals(CallbackInfo callback) {
        this.huntChipmunksGoal = new FoxTargetChipmunksGoal((FoxEntity) (Object) this, false, false);
        this.huntWoodpeckersGoal = new FoxTargetWoodpeckersGoal((FoxEntity) (Object) this, 20, false, false, entity -> !((WoodpeckerEntity) entity).isFlying());
    }

    @Inject(method = "addTypeSpecificGoals", at = @At("TAIL"))
    private void addNewTypeSpecificGoals(CallbackInfo callback) {
        if (this.getFoxType() == FoxEntity.Type.RED) {
            this.targetSelector.add(4, this.huntChipmunksGoal);
        }
        else {
            this.targetSelector.add(6, this.huntChipmunksGoal);
        }
        this.targetSelector.add(7, this.huntWoodpeckersGoal);
    }

    @Shadow public abstract FoxEntity.Type getFoxType();
}
