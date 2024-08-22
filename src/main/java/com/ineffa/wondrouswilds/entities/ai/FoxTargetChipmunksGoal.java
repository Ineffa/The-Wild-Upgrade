package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.ChipmunkEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.FoxEntity;

public class FoxTargetChipmunksGoal extends ActiveTargetGoal<ChipmunkEntity> {

    public FoxTargetChipmunksGoal(FoxEntity foxEntity, boolean checkVisibility, boolean checkCanNavigate) {
        super(foxEntity, ChipmunkEntity.class, checkVisibility, checkCanNavigate);
    }

    @Override
    protected double getFollowRange() {
        return super.getFollowRange() * 0.5D;
    }
}
