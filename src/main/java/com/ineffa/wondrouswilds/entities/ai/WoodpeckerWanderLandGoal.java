package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WoodpeckerWanderLandGoal extends WanderAroundFarGoal {

    private final WoodpeckerEntity woodpecker;

    public WoodpeckerWanderLandGoal(WoodpeckerEntity woodpeckerEntity, double speed) {
        super(woodpeckerEntity, speed);

        this.woodpecker = woodpeckerEntity;
    }

    @Override
    public boolean canStart() {
        if (this.woodpecker.isFlying() || !this.woodpecker.canWander()) return false;

        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return !this.woodpecker.isFlying() && this.woodpecker.canWander() && super.shouldContinue();
    }

    @Override
    public void stop() {
        super.stop();

        if (this.woodpecker.getRandom().nextBoolean() && this.woodpecker.isAbleToFly()) this.woodpecker.setFlying(true);
    }

    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        BlockPos moveTowardsPos = this.woodpecker.getPosToWanderTowards();
        if (moveTowardsPos != null) {
            Vec3d direction = Vec3d.ofCenter(moveTowardsPos).subtract(this.woodpecker.getPos()).normalize();
            return NoPenaltySolidTargeting.find(this.woodpecker, 20, 7, -2, direction.x, direction.z, 1.5707963705062866D);
        }

        return super.getWanderTarget();
    }
}
