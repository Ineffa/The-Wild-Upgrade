package com.ineffa.wondrouswilds.entities.ai;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.mixin.MeleeAttackGoalAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class WoodpeckerAttackGoal extends MeleeAttackGoal {

    private final WoodpeckerEntity woodpecker;

    public WoodpeckerAttackGoal(WoodpeckerEntity woodpecker, double speed, boolean pauseWhenMobIdle) {
        super(woodpecker, speed, pauseWhenMobIdle);

        this.woodpecker = woodpecker;
    }

    @Override
    public void start() {
        super.start();

        if (!this.woodpecker.isFlying()) this.woodpecker.setFlying(true);
    }

    @Override
    public void stop() {
        super.stop();

        if (this.woodpecker.isPecking()) this.woodpecker.stopPecking(true);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        if (this.isCooledDown() && squaredDistance <= this.getSquaredMaxAttackDistance(target)) {
            if (!(target instanceof WoodpeckerEntity) && !target.hasPassengers() && this.woodpecker.canStartDrumAttack()) {
                if (this.woodpecker.startRiding(target)) {
                    if (this.woodpecker.getWorld() instanceof ServerWorld serverWorld && target instanceof ServerPlayerEntity serverPlayer)
                        for (ServerPlayerEntity player : serverWorld.getPlayers()) player.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(serverPlayer));

                    this.woodpecker.startDrumming(true);
                    this.woodpecker.applyDrumAttackCooldown();

                    this.resetCooldown();
                    return;
                }
            }

            if (!this.woodpecker.isPecking()) {
                this.woodpecker.startPeckChain(1, 10);
                if (!this.woodpecker.getMainHandStack().isEmpty())
                    ((MeleeAttackGoalAccessor) this).setCooldown(this.getTickCount(20 + Math.round(20.0F / (float) this.woodpecker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED))));
            }
        }

        if (this.woodpecker.isPecking() && squaredDistance <= super.getSquaredMaxAttackDistance(target)) this.woodpecker.setVelocity(this.woodpecker.getVelocity().multiply(0.9D));
    }

    @Override
    protected void resetCooldown() {
        ((MeleeAttackGoalAccessor) this).setCooldown(this.getTickCount(60));
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return super.getSquaredMaxAttackDistance(entity) * 3.0D;
    }
}
