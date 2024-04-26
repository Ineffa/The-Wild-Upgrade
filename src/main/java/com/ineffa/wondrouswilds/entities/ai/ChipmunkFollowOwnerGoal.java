package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.ChipmunkEntity;

public class ChipmunkFollowOwnerGoal extends DynamicNavigationFollowOwnerGoal {

    private final ChipmunkEntity chipmunk;

    private int tryLeapingCooldown;

    public ChipmunkFollowOwnerGoal(ChipmunkEntity chipmunkEntity, double speed, float minDistance, float maxDistance, float startLeapingDistance, float teleportDistance) {
        super(chipmunkEntity, speed, minDistance, maxDistance, startLeapingDistance, teleportDistance, true);
        this.chipmunk = chipmunkEntity;
    }

    @Override
    public boolean canStart() {
        return this.chipmunk.isFollowing() && this.chipmunk.isOnGround() && super.canStart();
    }

    @Override
    public void tick() {
        this.chipmunk.getLookControl().lookAt(this.owner, 10.0F, this.chipmunk.getMaxLookPitchChange());

        boolean canTryLeaping = this.tryLeapingCooldown > 0;
        if (!canTryLeaping) --this.tryLeapingCooldown;

        if (--this.updateCountdownTicks > 0) return;
        this.updateCountdownTicks = this.getTickCount(10);

        if (this.chipmunk.isLeashed() || this.chipmunk.hasVehicle()) return;

        float distanceFromOwner = this.chipmunk.distanceTo(this.owner);
        if (distanceFromOwner >= this.teleportDistance) this.tryTeleport();
        else if (!this.chipmunk.isLeaping()) {
            if (canTryLeaping && this.owner.isOnGround() && distanceFromOwner >= this.startLeapingDistance) {
                this.tryLeapingCooldown = this.getTickCount(this.getRandomInt(60, 80));
                this.chipmunk.setLeaping(true, false);
            }
            this.chipmunk.getNavigation().startMovingTo(this.owner, this.speed);
        }
    }
}
