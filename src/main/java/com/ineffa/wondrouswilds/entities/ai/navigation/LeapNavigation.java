package com.ineffa.wondrouswilds.entities.ai.navigation;

import com.ineffa.wondrouswilds.entities.LeapingMob;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class LeapNavigation extends BetterMobNavigation {

    protected final LeapingMob leapingMob;

    @Nullable
    private Vec3d posToRetry;
    private boolean stopOnNextFail;

    public <T extends MobEntity & LeapingMob> LeapNavigation(T entity, World world) {
        super(entity, world);
        this.leapingMob = entity;
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new LeapPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(false);
        this.nodeMaker.setCanOpenDoors(false);
        this.nodeMaker.setCanSwim(false);
        return new LeapPathNodeNavigator((LeapPathNodeMaker) this.nodeMaker, range);
    }

    @Override
    public void tick() {
        ++this.tickCount;
        if (this.inRecalculationCooldown) {
            this.recalculatePath();
            if (!this.inRecalculationCooldown) {
                this.posToRetry = null;
                this.stopOnNextFail = false;
            }
        }

        if (this.isIdle()) return;

        if (this.posToRetry != null) {
            if (this.stopOnNextFail || !tryLeapingDirectlyToPosition(this.entity, this.posToRetry, this.leapingMob.getMaxLeapVelocity(), true, false)) this.stop();
            this.posToRetry = null;
            this.stopOnNextFail = true;
        }

        else if (this.isAtValidPosition()) this.continueFollowingPath();

        else if (!this.getCurrentPath().isFinished()) {
            Vec3d pos = this.getPos();
            Vec3d vec3d2 = this.getCurrentPath().getNodePosition(this.entity);
            if (pos.y > vec3d2.y && !this.entity.isOnGround() && MathHelper.floor(pos.x) == MathHelper.floor(vec3d2.x) && MathHelper.floor(pos.z) == MathHelper.floor(vec3d2.z))
                this.getCurrentPath().next();
        }

        DebugInfoSender.sendPathfindingData(this.world, this.entity, this.getCurrentPath(), this.nodeReachProximity);
    }

    @Override
    protected void continueFollowingPath() {
        super.continueFollowingPath();
        if (this.isIdle()) return;

        if (!this.tryLeapingToCurrentNode() && !this.getCurrentPath().isStart()) {
            Vec3d center = Vec3d.ofBottomCenter(this.getCurrentPath().getNodePos(this.getCurrentPath().getCurrentNodeIndex() - 1));
            this.posToRetry = center.add(center.relativize(this.entity.getPos()).multiply(-1.0D, 1.0D, -1.0D));
        }
        else this.stopOnNextFail = false;
    }

    protected boolean tryLeapingToCurrentNode() {
        return tryLeapingDirectlyToPosition(this.entity, Vec3d.ofBottomCenter(this.getCurrentPath().getCurrentNodePos()), this.leapingMob.getMaxLeapVelocity(), true, false);
    }

    public static boolean tryLeapingDirectlyToPosition(Entity leapingEntity, Vec3d targetPos, double maxVelocity, boolean requireSolidLanding, boolean prioritizeSteepestAngle) {
        Vec3d leapVec = tryCreatingLeapVelocityBetween(leapingEntity, leapingEntity.getPos(), targetPos, 40, 85, 1, maxVelocity, requireSolidLanding, prioritizeSteepestAngle, leapingEntity.getDimensions(EntityPose.LONG_JUMPING));
        if (leapVec != null) {
            leapingEntity.setVelocity(leapVec);
            WondrousWildsUtils.faceEntityYawTowards(leapingEntity, targetPos);
            return true;
        }
        return false;
    }

    @Nullable
    public static Vec3d tryCreatingLeapVelocityBetween(Entity entity, Vec3d from, Vec3d to, int lowerAngleBound, int upperAngleBound, int angleIncrement, double maxVelocity, boolean requireSolidLanding, boolean prioritizeSteepestAngle, EntityDimensions entityDimensions) {
        return tryCreatingLeapVelocityBetween(entity, from, to, lowerAngleBound, upperAngleBound, angleIncrement, maxVelocity, requireSolidLanding, prioritizeSteepestAngle, entityDimensions, null);
    }

    @Nullable
    public static Vec3d tryCreatingLeapVelocityBetween(Entity entity, Vec3d from, Vec3d to, int lowerAngleBound, int upperAngleBound, int angleIncrement, double maxVelocity, boolean requireSolidLanding, boolean prioritizeSteepestAngle, Set<Long> openPositionCache) {
        return tryCreatingLeapVelocityBetween(entity, from, to, lowerAngleBound, upperAngleBound, angleIncrement, maxVelocity, requireSolidLanding, prioritizeSteepestAngle, null, openPositionCache);
    }

    @Nullable
    private static Vec3d tryCreatingLeapVelocityBetween(Entity entity, Vec3d from, Vec3d to, int lowerAngleBound, int upperAngleBound, int angleIncrement, double maxVelocity, boolean requireSolidLanding, boolean prioritizeSteepestAngle, @Nullable EntityDimensions entityDimensions, @Nullable Set<Long> openPositionCache) {
        boolean checkWithHitbox = entityDimensions != null && openPositionCache == null;
        float density = checkWithHitbox ? 5.0F : 0.5F;
        int angleBound = prioritizeSteepestAngle ? lowerAngleBound : upperAngleBound;
        int currentAngle = prioritizeSteepestAngle ? upperAngleBound : lowerAngleBound;
        boolean reverse = prioritizeSteepestAngle;
        Vec3d leapVecTarget = to.subtract(from);
        double heightDifference = to.y - from.y;
        while (reverse ? currentAngle >= angleBound : currentAngle <= angleBound) {
            Vec3d leapVec = getLeapVector(leapVecTarget, currentAngle, maxVelocity);
            currentAngle += reverse ? -angleIncrement : angleIncrement;
            if (leapVec == null) continue;

            if (Double.isNaN(leapVec.y) || Double.isNaN(leapVec.x) || Double.isNaN(leapVec.z)) {
                if (reverse != prioritizeSteepestAngle) break;

                reverse = true;
                angleBound = lowerAngleBound;
                currentAngle = upperAngleBound;
                continue;
            }

            boolean validLeap = true;
            boolean reachedTarget = false;
            double e = 5.0D * (5.0D * leapVec.y + Math.sqrt(25.0D * leapVec.y * leapVec.y - 4.0D * heightDifference)) * density;
            Box currentBox;
            BlockPos currentBlockPos;
            long currentBlockPosLong = 0L;
            BlockState currentBlockState;
            for (double t = 0.0D; t < e; t += (1.0D / density)) {
                double x = t * leapVec.x;
                double y = t * leapVec.y - 0.04D * t * t;
                double z = t * leapVec.z;
                Vec3d vec3d = from.add(x, y, z);

                if (!reachedTarget && vec3d.distanceTo(to) <= 0.25D) reachedTarget = true;

                boolean foundNewOpenPosition = false;
                if (checkWithHitbox ? !entity.getWorld().containsFluid(currentBox = entityDimensions.getBoxAt(vec3d)) && entity.getWorld().isSpaceEmpty(entity, currentBox) : openPositionCache.contains(currentBlockPosLong = (currentBlockPos = new BlockPos(vec3d)).asLong()) || (foundNewOpenPosition = ((currentBlockState = entity.getWorld().getBlockState(currentBlockPos)).getCollisionShape(entity.getWorld(), currentBlockPos).isEmpty() && currentBlockState.getFluidState().isEmpty()))) {
                    if (foundNewOpenPosition) openPositionCache.add(currentBlockPosLong);

                    if (reachedTarget) {
                        if (!requireSolidLanding) break;

                        if (vec3d.distanceTo(to) > 1.0D) {
                            validLeap = false;
                            break;
                        }
                    }

                    continue;
                }

                if (reachedTarget) break;

                validLeap = false;
                break;
            }
            if (!validLeap) continue;

            return leapVec;
        }

        return null;
    }

    @Nullable
    public static Vec3d getLeapVector(Vec3d to, float angle, double maxVelocity) {
        double d = Math.sqrt(to.x * to.x + to.z * to.z);
        double tan = Math.tan(Math.toRadians(angle));
        double k = 5 * Math.sqrt(d * tan - to.y);
        double vy = (d * tan) / k;
        double vh = d / k;
        double theta = Math.atan2(to.z, to.x);
        double vx = Math.cos(theta) * vh;
        double vz = Math.sin(theta) * vh;

        Vec3d vec3d = new Vec3d(vx, vy, vz);
        double length = vec3d.length();
        if (length <= 0.0D || length > maxVelocity) return null;

        return vec3d;
    }

    @Override
    public void stop() {
        this.posToRetry = null;
        this.stopOnNextFail = false;
        super.stop();
        this.leapingMob.setLeaping(false, false);
    }
}
