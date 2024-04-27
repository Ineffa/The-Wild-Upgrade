package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.CanBondWithWoodpecker;
import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.mixin.common.MoveToTargetPosGoalAccessor;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WoodpeckerBondingTaskGoal extends MoveToTargetPosGoal {

    private final WoodpeckerEntity woodpecker;
    private WoodpeckerEntity.BondingTask bondingTask;
    private PlayerEntity bondingTarget;
    private final List<Pair<BlockPos, Block>> blockTargets = new ArrayList<>();
    private int blockTargetIndex = -1;
    @Nullable
    private ItemEntity nextPickupTarget;
    private BlockPos lookPos;
    private boolean shouldDropItem;
    private boolean shouldStartWaiting;
    private boolean shouldWait;
    private BlockPos startPos;

    public WoodpeckerBondingTaskGoal(WoodpeckerEntity woodpecker, double speed) {
        super(woodpecker, speed, 0, 0);
        this.woodpecker = woodpecker;

        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.woodpecker.canBond() || !this.woodpecker.canWander()) return false;

        if ((this.bondingTask = this.woodpecker.getCurrentBondingTask()) == null) return false;

        return this.bondingTask.getStatus() == WoodpeckerEntity.BondingTask.BondingTaskStatus.PLAYING && this.findTargetPos();
    }

    @Override
    public void start() {
        super.start();

        if (this.woodpecker.isFlying()) this.woodpecker.setFlying(false);

        this.shouldDropItem = false;
        this.shouldStartWaiting = false;
        this.shouldWait = false;
        this.startPos = this.woodpecker.getBlockPos();
    }

    @Override
    public boolean shouldContinue() {
        return (this.bondingTask = this.woodpecker.getCurrentBondingTask()) != null &&
                this.woodpecker.canBond() &&
                this.woodpecker.canWander() &&
                super.shouldContinue() &&
                (this.bondingTarget = this.woodpecker.getBondingTarget()) != null &&
                this.checkStatus();
    }

    @Override
    public void stop() {
        this.woodpecker.setPickupTarget(null);

        if (this.bondingTask != null) {
            this.bondingTask.reset();
            this.woodpecker.setComparingWithBondingTarget(false);
            this.woodpecker.refreshBondingTasks();
        }

        this.blockTargetIndex = -1;
        this.blockTargets.clear();
    }

    @Override
    public void tick() {
        if (this.woodpecker.getPickupTarget() != null) this.woodpecker.getLookControl().lookAt(this.woodpecker.getPickupTarget());
        else if (this.lookPos != null) this.woodpecker.getLookControl().lookAt(WondrousWildsUtils.getCenterOfBlockShape(this.woodpecker.getWorld(), this.lookPos));

        if (this.woodpecker.isPecking()) return;

        this.woodpecker.setPickupTarget(this.nextPickupTarget);
        if (this.shouldDropItem) {
            this.shouldDropItem = false;
            this.dropHeldItem();
        }

        if (this.shouldStartWaiting) {
            this.shouldStartWaiting = false;

            ((MoveToTargetPosGoalAccessor) this).setTryingTime(0);
            ((MoveToTargetPosGoalAccessor) this).setSafeWaitingTime(1200);
            this.targetPos = this.startPos;
            this.lookPos = null;

            this.shouldWait = true;
            this.bondingTask.setStatus(WoodpeckerEntity.BondingTask.BondingTaskStatus.WAITING);
            this.woodpecker.setComparingWithBondingTarget(true);
            this.woodpecker.refreshBondingTasks();
        }

        super.tick();

        if (this.shouldWait) {
            this.woodpecker.getLookControl().lookAt(this.bondingTarget);
            return;
        }

        this.lookPos = this.targetPos;

        if (this.hasReached() && !this.woodpecker.getMoveControl().isMoving()) {
            if (this.nextPickupTarget == null) this.woodpecker.startPeckChain();
            else this.woodpecker.startPeckChain(1, WoodpeckerEntity.DEFAULT_PECK_INTERVAL);

            boolean startWaiting = !this.updateTargetPos();
            if (startWaiting) this.shouldDropItem = true;
            this.shouldStartWaiting = startWaiting;
        }

        ((ServerWorld) this.woodpecker.getWorld()).spawnParticles(ParticleTypes.FLAME, this.getTargetPos().getX() + 0.5D, this.getTargetPos().getY() + 1.01D, this.getTargetPos().getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    protected boolean updateTargetPos() {
        if (++this.blockTargetIndex >= this.blockTargets.size()) return false;

        Pair<BlockPos, Block> nextBlockTarget = this.blockTargets.get(this.blockTargetIndex);
        BlockPos nextPos = nextBlockTarget.getLeft();
        Block expectedBlock = nextBlockTarget.getRight();
        if (this.woodpecker.getWorld().getBlockState(nextPos).getBlock() != expectedBlock) return false;

        boolean dropItem = true;
        boolean previouslyPickedUpItem = this.nextPickupTarget != null;
        List<ItemEntity> nearbyItems;
        if (!previouslyPickedUpItem && !(nearbyItems = this.woodpecker.getWorld().getEntitiesByClass(ItemEntity.class, new Box(nextPos).expand(1.0D), item -> !item.cannotPickup() && item.isAlive() && item.isOnGround() && this.woodpecker.canSee(item))).isEmpty()) {
            nearbyItems.sort(Comparator.comparingDouble(item -> item.squaredDistanceTo(this.woodpecker)));
            ItemEntity pickupTarget = nearbyItems.get(0);
            this.nextPickupTarget = pickupTarget;
            this.targetPos = pickupTarget.getBlockPos();
            --this.blockTargetIndex;
        }
        else {
            if (previouslyPickedUpItem) dropItem = false;

            this.nextPickupTarget = null;
            this.targetPos = nextPos;
        }
        this.shouldDropItem = dropItem;
        return true;
    }

    @Override
    protected boolean findTargetPos() {
        if (this.bondingTask.getTargetPositions().isEmpty()) return false;

        this.blockTargets.clear();
        this.bondingTask.getTargetPositions().forEach(pos -> this.blockTargets.add(new Pair<>(pos, this.woodpecker.getWorld().getBlockState(pos).getBlock())));
        Collections.shuffle(this.blockTargets);
        return this.updateTargetPos();
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    protected BlockPos getTargetPos() {
        return this.targetPos;
    }

    @Override
    public double getDesiredDistanceToTarget() {
        return this.woodpecker.getPeckReach();
    }

    private void dropHeldItem() {
        ItemStack heldItem = this.woodpecker.getStackInHand(Hand.MAIN_HAND);
        if (!heldItem.isEmpty()) {
            this.woodpecker.dropStack(heldItem);
            this.woodpecker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    private boolean checkStatus() {
        WoodpeckerEntity.BondingTask.BondingTaskStatus status = this.bondingTask.getStatus();
        if (status == WoodpeckerEntity.BondingTask.BondingTaskStatus.WAITING) return true;
        return status == WoodpeckerEntity.BondingTask.BondingTaskStatus.PLAYING && ((CanBondWithWoodpecker) this.bondingTarget).wondrouswilds$getComparingWoodpecker().isEmpty();
    }
}
