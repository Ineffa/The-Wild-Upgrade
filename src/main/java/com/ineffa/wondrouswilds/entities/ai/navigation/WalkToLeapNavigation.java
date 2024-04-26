package com.ineffa.wondrouswilds.entities.ai.navigation;

import com.ineffa.wondrouswilds.entities.LeapingMob;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WalkToLeapNavigation extends BetterMobNavigation {

    protected final LeapingMob leapingMob;

    public <T extends MobEntity & LeapingMob> WalkToLeapNavigation(T entity, World world) {
        super(entity, world);
        this.leapingMob = entity;
    }

    @Override
    public boolean startMovingAlong(@Nullable Path path, double speed) {
        boolean b = super.startMovingAlong(path, speed);
        if (b && !path.reachesTarget()) this.leapingMob.setLeaping(true, true);
        return b;
    }
}
