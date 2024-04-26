package com.ineffa.wondrouswilds.entities.ai;

import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class DynamicNavigationFollowOwnerGoal extends Goal {

    protected final TameableEntity tameable;
    protected LivingEntity owner;
    protected final WorldView world;
    protected final double speed;
    protected int updateCountdownTicks;
    protected final float maxDistance, minDistance, startLeapingDistance, teleportDistance;
    protected float oldWaterPathfindingPenalty;
    protected final boolean leavesAllowed;

    public DynamicNavigationFollowOwnerGoal(TameableEntity tameableEntity, double speed, float minDistance, float maxDistance, float startLeapingDistance, float teleportDistance, boolean leavesAllowed) {
        this.tameable = tameableEntity;
        this.world = tameableEntity.getWorld();
        this.speed = speed;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.startLeapingDistance = startLeapingDistance;
        this.teleportDistance = teleportDistance;
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) return false;
        if (livingEntity.isSpectator()) return false;
        if (this.tameable.isSitting()) return false;
        if (this.tameable.squaredDistanceTo(livingEntity) < (double) (this.minDistance * this.minDistance)) return false;
        this.owner = livingEntity;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (this.tameable.isSitting()) return false;
        return !(this.tameable.squaredDistanceTo(this.owner) <= (double) (this.maxDistance * this.maxDistance));
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.tameable.getPathfindingPenalty(PathNodeType.WATER);
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.tameable.getNavigation().stop();
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        this.tameable.getLookControl().lookAt(this.owner, 10.0F, this.tameable.getMaxLookPitchChange());

        if (--this.updateCountdownTicks > 0) return;
        this.updateCountdownTicks = this.getTickCount(10);

        if (this.tameable.isLeashed() || this.tameable.hasVehicle()) return;

        if (this.tameable.distanceTo(this.owner) >= this.teleportDistance) this.tryTeleport();
        else this.tameable.getNavigation().startMovingTo(this.owner, this.speed);
    }

    protected void tryTeleport() {
        BlockPos blockPos = this.owner.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (!bl) continue;
            return;
        }
    }

    protected boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) return false;
        if (!this.canTeleportTo(new BlockPos(x, y, z))) return false;
        this.tameable.refreshPositionAndAngles((double) x + 0.5D, y, (double) z + 0.5D, this.tameable.getYaw(), this.tameable.getPitch());
        this.tameable.getNavigation().stop();
        return true;
    }

    protected boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathNodeType != PathNodeType.WALKABLE) return false;
        if (!this.leavesAllowed && this.world.getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;
        return this.world.isSpaceEmpty(this.tameable, this.tameable.getBoundingBox().offset(pos.subtract(this.tameable.getBlockPos())));
    }

    protected int getRandomInt(int min, int max) {
        return this.tameable.getRandom().nextInt(max - min + 1) + min;
    }
}
