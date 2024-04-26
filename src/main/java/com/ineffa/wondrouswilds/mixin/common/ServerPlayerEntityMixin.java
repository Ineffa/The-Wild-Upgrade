package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.CanBondWithWoodpecker;
import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.entities.projectiles.CanSharpshot;
import com.ineffa.wondrouswilds.registry.WondrousWildsAdvancementCriteria;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements CanBondWithWoodpecker {

    @Unique
    @Nullable
    private WoodpeckerEntity comparingWoodpecker;

    @Unique
    private int woodpeckerComparisonCount;

    @Override
    public Optional<WoodpeckerEntity> getComparingWoodpecker() {
        return Optional.ofNullable(this.comparingWoodpecker);
    }

    @Override
    public void setComparingWoodpecker(@Nullable WoodpeckerEntity comparingWoodpecker) {
        if (comparingWoodpecker == null) this.stopComparingWithWoodpecker();
        else this.comparingWoodpecker = comparingWoodpecker;
    }

    @Override
    public void tryWoodpeckerBondingWith(Block block, BlockPos pos, Item item) {
        if (this.compareBlockInteractionWithWoodpecker(block, pos, item)) this.stopComparingWithWoodpecker();
    }

    @Override
    public void stopComparingWithWoodpecker() {
        if (this.comparingWoodpecker != null) {
            WoodpeckerEntity.BondingTask currentWoodpeckerBondingTask = this.comparingWoodpecker.getCurrentBondingTask();
            if (currentWoodpeckerBondingTask != null) {
                currentWoodpeckerBondingTask.reset();
                this.comparingWoodpecker.refreshBondingTasks();
            }
        }

        this.comparingWoodpecker = null;
        this.woodpeckerComparisonCount = 0;
    }

    /**
     * @return {@code true} if the given interaction parameters should result in ending the current comparison process
     */
    @Unique
    private boolean compareBlockInteractionWithWoodpecker(Block block, BlockPos pos, Item item) {
        WoodpeckerEntity.BondingTask currentWoodpeckerBondingTask = this.comparingWoodpecker.getCurrentBondingTask();
        if (currentWoodpeckerBondingTask == null || currentWoodpeckerBondingTask.getStatus() != WoodpeckerEntity.BondingTask.BondingTaskStatus.WAITING) return true;

        WoodpeckerEntity.BondingTask.BondingTaskCompletionStep completionStep = currentWoodpeckerBondingTask.getCompletionStep(this.woodpeckerComparisonCount++);
        if (completionStep == null) return true;

        if (!completionStep.matches(block, pos, item)) return true;

        boolean onLastStep = this.woodpeckerComparisonCount >= currentWoodpeckerBondingTask.getCompletionStepAmount();
        if (onLastStep) this.comparingWoodpecker.fulfillCurrentBondingTask();
        return onLastStep;
    }

    @Inject(method = "updateKilledAdvancementCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/OnKilledCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)V", shift = At.Shift.AFTER))
    private void triggerSharpshotKillCriterion(Entity entityKilled, int score, DamageSource damageSource, CallbackInfo callback) {
        if (damageSource.getSource() instanceof CanSharpshot sharpshotProjectile && sharpshotProjectile.wondrouswilds$hasRegisteredSharpshot())
            WondrousWildsAdvancementCriteria.KILLED_WITH_SHARPSHOT.trigger((ServerPlayerEntity) (Object) this, entityKilled, damageSource);
    }
}
