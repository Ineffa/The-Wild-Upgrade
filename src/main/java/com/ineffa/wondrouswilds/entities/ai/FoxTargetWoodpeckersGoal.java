package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.FoxEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class FoxTargetWoodpeckersGoal extends ActiveTargetGoal<WoodpeckerEntity> {

    public FoxTargetWoodpeckersGoal(FoxEntity foxEntity, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(foxEntity, WoodpeckerEntity.class, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
    }

    @Override
    protected double getFollowRange() {
        return super.getFollowRange() * 0.5D;
    }
}
