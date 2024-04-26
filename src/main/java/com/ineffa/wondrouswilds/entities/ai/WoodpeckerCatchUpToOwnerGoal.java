package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WoodpeckerCatchUpToOwnerGoal extends Goal {

    private final WoodpeckerEntity woodpecker;
    private final float followDistance;
    private final double speed;

    private LivingEntity owner;

    public WoodpeckerCatchUpToOwnerGoal(WoodpeckerEntity woodpecker, float followDistance, double speed) {
        this.woodpecker = woodpecker;
        this.followDistance = followDistance;
        this.speed = speed;

        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.woodpecker.isFollowing() || this.woodpecker.isLeashed()) return false;

        this.owner = this.woodpecker.getOwner();
        if (this.owner == null) return false;

        double distanceFromOwner = this.woodpecker.distanceTo(this.owner);
        if (distanceFromOwner > this.woodpecker.getMaximumFollowingDistance()) {
            this.woodpecker.setIsFollowing(false);
            return false;
        }

        return distanceFromOwner > this.woodpecker.getFollowingWanderRadiusAroundOwner();
    }

    @Override
    public void start() {
        if (!this.woodpecker.isFlying()) this.woodpecker.setFlying(true);

        this.woodpecker.getNavigation().startMovingTo(this.owner, this.speed);
    }

    @Override
    public boolean shouldContinue() {
        return this.woodpecker.getNavigation().isFollowingPath() && this.woodpecker.distanceTo(this.owner) > this.followDistance;
    }

    @Override
    public void stop() {
        this.woodpecker.getNavigation().stop();
    }
}
