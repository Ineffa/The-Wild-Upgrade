package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class WoodpeckerWanderFlyingGoal extends Goal {

    private final WoodpeckerEntity woodpecker;

    public WoodpeckerWanderFlyingGoal(WoodpeckerEntity woodpeckerEntity) {
        this.woodpecker = woodpeckerEntity;

        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.woodpecker.isFlying() || !this.woodpecker.canWander()) return false;

        return this.woodpecker.getNavigation().isIdle() && this.woodpecker.getRandom().nextInt(10) == 0;
    }

    @Override
    public boolean shouldContinue() {
        return this.woodpecker.isFlying() && this.woodpecker.canWander() && this.woodpecker.getNavigation().isFollowingPath();
    }

    @Override
    public void start() {
        Vec3d vec3d = this.getRandomLocation();
        if (vec3d != null) this.woodpecker.getNavigation().startMovingAlong(this.woodpecker.getNavigation().findPathTo(new BlockPos(vec3d), 1), 1.0D);
    }

    @Override
    public void stop() {
        if (!this.woodpecker.wantsToLand()) {
            if (this.woodpecker.getRandom().nextInt(30) == 0) this.woodpecker.setWantsToLand(true);
        }
        else this.woodpecker.setFlying(false);
    }

    @Nullable
    private Vec3d getRandomLocation() {
        BlockPos moveTowardsPos = this.woodpecker.getPosToWanderTowards();
        boolean isMovingTowardsPos = moveTowardsPos != null;
        Vec3d direction = isMovingTowardsPos ? Vec3d.ofCenter(moveTowardsPos).subtract(this.woodpecker.getPos()).normalize() : this.woodpecker.getRotationVec(0.0F);

        int horizontalRange = isMovingTowardsPos ? 32 : 16;

        Vec3d groundLocation = AboveGroundTargeting.find(this.woodpecker, horizontalRange, 8, direction.x, direction.z, 1.5707964F, this.woodpecker.wantsToLand() ? 1 : 6, 1);
        if (groundLocation != null) return groundLocation;

        return NoPenaltySolidTargeting.find(this.woodpecker, horizontalRange, 8, -2, direction.x, direction.z, 1.5707963705062866D);
    }
}
