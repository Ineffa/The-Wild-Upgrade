package com.ineffa.wondrouswilds.entities;

import net.minecraft.entity.projectile.ProjectileEntity;

public interface BycocketUser {

    int wondrouswilds$getOverchargeStartDelay();

    int wondrouswilds$getFullOverchargeThreshold();

    boolean wondrouswilds$canOvercharge();

    boolean wondrouswilds$isOvercharging();

    boolean wondrouswilds$isFullyOvercharged();

    boolean wondrouswilds$isAccurateWith(ProjectileEntity projectile);

    boolean wondrouswilds$canSharpshotWith(ProjectileEntity projectile);
}
