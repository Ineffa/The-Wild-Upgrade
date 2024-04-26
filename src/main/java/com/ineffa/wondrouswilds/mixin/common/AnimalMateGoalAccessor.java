package com.ineffa.wondrouswilds.mixin.common;

import net.minecraft.entity.ai.goal.AnimalMateGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimalMateGoal.class)
public interface AnimalMateGoalAccessor {

    @Accessor
    int getTimer();

    @Accessor("timer")
    void setTimer(int time);
}
