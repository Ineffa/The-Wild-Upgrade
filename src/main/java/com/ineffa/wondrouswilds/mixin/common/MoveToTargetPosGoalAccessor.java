package com.ineffa.wondrouswilds.mixin.common;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MoveToTargetPosGoal.class)
public interface MoveToTargetPosGoalAccessor {

    @Accessor("tryingTime")
    void setTryingTime(int time);

    @Accessor("safeWaitingTime")
    void setSafeWaitingTime(int time);
}
