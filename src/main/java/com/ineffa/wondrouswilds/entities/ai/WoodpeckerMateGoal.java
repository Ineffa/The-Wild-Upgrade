package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.mixin.common.AnimalMateGoalAccessor;
import net.minecraft.entity.ai.goal.AnimalMateGoal;

public class WoodpeckerMateGoal extends AnimalMateGoal {

    private final WoodpeckerEntity woodpecker;

    public WoodpeckerMateGoal(WoodpeckerEntity woodpecker, double chance) {
        super(woodpecker, chance);

        this.woodpecker = woodpecker;
    }

    @Override
    public void start() {
        super.start();

        if (this.woodpecker.isFlying()) this.woodpecker.setFlying(false);
    }

    @Override
    public boolean shouldContinue() {
        return this.mate.isAlive() && this.mate.isInLove();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.woodpecker.distanceTo(this.mate) < 2.0D) {
            if (this.woodpecker.isFlying()) this.woodpecker.setFlying(false);
        }
        else if (!this.woodpecker.isFlying() && ((AnimalMateGoalAccessor) this).getTimer() >= this.getTickCount(120)) {
            ((AnimalMateGoalAccessor) this).setTimer(0);
            this.woodpecker.setFlying(true);
        }
    }
}
