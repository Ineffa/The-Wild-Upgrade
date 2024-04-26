package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.util.math.Box;

import java.util.function.Predicate;

public class WoodpeckerDefendOwnerGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {

    protected final WoodpeckerEntity woodpecker;

    public WoodpeckerDefendOwnerGoal(WoodpeckerEntity woodpeckerEntity, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
        super(woodpeckerEntity, targetClass, checkVisibility, targetPredicate);
        this.woodpecker = woodpeckerEntity;
    }

    @Override
    public boolean canStart() {
        return this.woodpecker.getHealth() == this.woodpecker.getMaxHealth() && this.woodpecker.isFollowing() && super.canStart();
    }

    @Override
    protected Box getSearchBox(double distance) {
        return this.woodpecker.getBoundingBox().expand(distance, distance * 0.5D, distance);
    }
}
