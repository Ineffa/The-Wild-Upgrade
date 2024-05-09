package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.CanBondWithWoodpecker;
import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements CanBondWithWoodpecker {

    @Unique
    @Nullable
    private WoodpeckerEntity comparingWoodpecker;

    @Unique
    private int woodpeckerComparisonCount;

    @Override
    public Optional<WoodpeckerEntity> wondrouswilds$getComparingWoodpecker() {
        return Optional.ofNullable(this.comparingWoodpecker);
    }

    @Override
    public void wondrouswilds$setComparingWoodpecker(@Nullable WoodpeckerEntity comparingWoodpecker) {
        if (comparingWoodpecker == null) this.wondrouswilds$stopComparingWithWoodpecker();
        else this.comparingWoodpecker = comparingWoodpecker;
    }

    @Override
    public void wondrouswilds$tryWoodpeckerBondingWith(Block block, BlockPos pos, Item item) {
        if (this.compareBlockInteractionWithWoodpecker(block, pos, item)) this.wondrouswilds$stopComparingWithWoodpecker();
    }

    @Override
    public void wondrouswilds$stopComparingWithWoodpecker() {
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
}
