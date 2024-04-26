package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import net.minecraft.entity.ai.control.LookControl;

public class WoodpeckerLookControl extends LookControl {

    private final WoodpeckerEntity woodpecker;

    public WoodpeckerLookControl(WoodpeckerEntity entity) {
        super(entity);

        this.woodpecker = entity;
    }

    @Override
    protected boolean shouldStayHorizontal() {
        return !this.woodpecker.isPecking() && !this.woodpecker.isDrumming();
    }
}
