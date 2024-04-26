package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.registry.WondrousWildsStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.CreeperEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class WoodpeckerRescueOwnerGoal extends TrackTargetGoal {

    private static final TargetPredicate TARGET_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility().setPredicate(livingEntity -> !(livingEntity instanceof CreeperEntity));

    private final WoodpeckerEntity woodpecker;

    private LivingEntity currentOwner;
    @Nullable
    private LivingEntity attacker;
    private int lastAttackedTime;

    public WoodpeckerRescueOwnerGoal(WoodpeckerEntity woodpecker) {
        super(woodpecker, false);
        this.woodpecker = woodpecker;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (this.woodpecker.hasVehicle() || this.woodpecker.getHealth() < this.woodpecker.getMaxHealth() || !this.woodpecker.isFollowing()) return false;

        this.currentOwner = this.woodpecker.getOwner();
        if (this.currentOwner == null) return false;

        return this.currentOwner.getLastAttackedTime() != this.lastAttackedTime && (this.attacker = this.currentOwner.getAttacker()) != this.currentOwner && this.currentOwner.hasStatusEffect(WondrousWildsStatusEffects.WOODPECKERS_WARCRY) && this.canTrack(this.attacker, TARGET_PREDICATE);
    }

    @Override
    public void start() {
        this.woodpecker.setTarget(this.attacker);
        this.lastAttackedTime = this.currentOwner.getLastAttackedTime();

        super.start();
    }
}
