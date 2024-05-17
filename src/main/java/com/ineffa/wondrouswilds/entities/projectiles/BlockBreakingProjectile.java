package com.ineffa.wondrouswilds.entities.projectiles;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface BlockBreakingProjectile {

    double getStrongVelocityThreshold();

    @Nullable
    ProjectileBlockDamageType testBlockDamageCapability(World world, BlockPos pos);
}
