package com.ineffa.wondrouswilds.entities;

import com.ineffa.wondrouswilds.entities.ai.ChipmunkFollowOwnerGoal;
import com.ineffa.wondrouswilds.entities.ai.RelaxedBodyControl;
import com.ineffa.wondrouswilds.entities.ai.navigation.LeapNavigation;
import com.ineffa.wondrouswilds.entities.ai.navigation.WalkToLeapNavigation;
import com.ineffa.wondrouswilds.registry.WondrousWildsItems;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class ChipmunkEntity extends TameableEntity implements LeapingMob, IAnimatable {

    public static final String IS_FOLLOWING_KEY = "IsFollowing";

    private static final TrackedData<Boolean> IS_LEAPING = DataTracker.registerData(ChipmunkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private WalkToLeapNavigation walkNavigation;
    private final LeapNavigation leapNavigation;

    public static final double DEFAULT_MOVEMENT_SPEED = 0.35D;

    private FleeEntityGoal<PlayerEntity> fleePlayerGoal;

    private boolean following;

    /* Client fields */
    private double prevMovementTiltAngle, movementTiltAngle, prevMovementTiltAngleTailUpper, movementTiltAngleTailUpper, prevMovementTiltAngleTailLower, movementTiltAngleTailLower;

    public ChipmunkEntity(EntityType<? extends ChipmunkEntity> entityType, World world) {
        super(entityType, world);

        this.setPathfindingPenalty(PathNodeType.LEAVES, 0.0F);
        this.leapNavigation = new LeapNavigation(this, world);

        this.ignoreCameraFrustum = true;
    }

    public static DefaultAttributeContainer.Builder createChipmunkAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, DEFAULT_MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(IS_LEAPING, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean(IS_FOLLOWING_KEY, this.isFollowing());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.following = nbt.getBoolean(IS_FOLLOWING_KEY);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.0D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, FoxEntity.class, 24.0F, 1.0D, 1.0D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, WolfEntity.class, 24.0F, 1.0D, 1.0D, entity -> !((WolfEntity) entity).isTamed()));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, CatEntity.class, 24.0F, 1.0D, 1.0D, entity -> !((CatEntity) entity).isTamed()));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, OcelotEntity.class, 24.0F, 1.0D, 1.0D));
        this.goalSelector.add(3, new SitGoal(this));
        this.goalSelector.add(5, new ChipmunkFollowOwnerGoal(this, 1.0D, 6.0F, 3.0F, 12.0F, 24.0F));

        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D, 0.0F));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 16.0F));
        this.goalSelector.add(7, new LookAtEntityGoal(this, MobEntity.class, 16.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            double targetAngle = 45.0D * MathHelper.clamp((this.prevY - this.getY()) * 4.0D, -2.0D, 2.0D);
            this.prevMovementTiltAngle = this.movementTiltAngle;
            this.movementTiltAngle = WondrousWildsUtils.stepTowards(this.movementTiltAngle, targetAngle, Math.abs(this.movementTiltAngle - targetAngle) * 0.5D);
            this.prevMovementTiltAngleTailLower = this.movementTiltAngleTailLower;
            this.movementTiltAngleTailLower = this.movementTiltAngleTailUpper;
            this.prevMovementTiltAngleTailUpper = this.movementTiltAngleTailUpper;
            this.movementTiltAngleTailUpper = -(this.movementTiltAngle - this.prevMovementTiltAngle);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.isTamed()) {
            if (player.getStackInHand(hand).getItem() == WondrousWildsItems.LOVIFIER) {
                if (!this.getWorld().isClient()) {
                    this.setOwner(player);
                    this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                }
                return ActionResult.SUCCESS;
            }
        }
        else if (this.isOwner(player)) {
            if (!this.getWorld().isClient()) {
                String message;
                if (this.isSitting()) {
                    this.setSitting(false);
                    this.following = false;
                    message = "wandering";
                }
                else if (!this.isFollowing()) {
                    this.following = true;
                    message = "following";
                }
                else {
                    this.setSitting(true);
                    message = "sitting";
                }
                player.sendMessage(Text.translatable(this.getType().getTranslationKey() + ".start_" + message, this.getName()), true);
            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    protected void onTamedChanged() {
        if (this.fleePlayerGoal == null)
            this.fleePlayerGoal = new FleeEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.0D, 1.0D, entity -> !entity.isSneaky() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity));

        this.goalSelector.remove(this.fleePlayerGoal);

        if (!this.isTamed()) this.goalSelector.add(2, this.fleePlayerGoal);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.75F;
    }

    @Override
    protected BodyControl createBodyControl() {
        return new RelaxedBodyControl(this);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        this.walkNavigation = new WalkToLeapNavigation(this, world);
        return this.walkNavigation;
    }

    @Override
    protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
        if (movement.getY() <= 0.0D && type == MovementType.SELF && (this.isLeaping() || this.getNavigation().isIdle()) && (this.isOnGround() || this.fallDistance < this.stepHeight && !this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(0.0D, this.fallDistance - this.stepHeight, 0.0D)))) {
            double d = movement.x;
            double e = movement.z;
            double f = 0.05D;
            while (d != 0.0D && this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(d, -this.stepHeight, 0.0D))) {
                if (d < f && d >= -f) {
                    d = 0.0D;
                    continue;
                }
                if (d > 0.0D) {
                    d -= f;
                    continue;
                }
                d += f;
            }
            while (e != 0.0D && this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(0.0D, -this.stepHeight, e))) {
                if (e < f && e >= -f) {
                    e = 0.0D;
                    continue;
                }
                if (e > 0.0D) {
                    e -= f;
                    continue;
                }
                e += f;
            }
            while (d != 0.0D && e != 0.0D && this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(d, -this.stepHeight, e))) {
                d = d < f && d >= -f ? 0.0D : (d > 0.0D ? d - f : d + f);
                if (e < f && e >= -f) {
                    e = 0.0D;
                    continue;
                }
                if (e > 0.0D) {
                    e -= f;
                    continue;
                }
                e += f;
            }
            movement = new Vec3d(d, movement.y, e);
        }
        return movement;
    }

    @Override
    public boolean isLeaping() {
        return this.dataTracker.get(IS_LEAPING);
    }

    public void setIsLeaping(boolean leaping) {
        this.dataTracker.set(IS_LEAPING, leaping);
    }

    @Override
    public void setLeaping(boolean leaping, boolean retainPathTarget) {
        this.setIsLeaping(leaping);

        BlockPos previousPathTarget = !retainPathTarget || this.navigation.isIdle() || this.navigation.getCurrentPath().getLength() == 0 ? null : this.navigation.getCurrentPath().getTarget();
        if (!(this.navigation instanceof LeapNavigation)) this.navigation.stop();
        this.navigation = leaping ? this.leapNavigation : this.walkNavigation;
        if (previousPathTarget != null) this.navigation.startMovingTo(previousPathTarget.getX() + 0.5D, previousPathTarget.getY(), previousPathTarget.getZ() + 0.5D, 1.0D);
    }

    @Override
    public double getMaxLeapVelocity() {
        return 1.0D;
    }

    @Override
    public int getMaxLeapHeight() {
        return 5;
    }

    @Override
    public boolean hasNoDrag() {
        if (!this.isOnGround() && this.isLeaping()) return true;
        return super.hasNoDrag();
    }

    @Override
    public void onLanding() {
        super.onLanding();
        if (!this.getWorld().isClient() && this.getNavigation().isIdle() && this.isLeaping()) this.setLeaping(false, false);
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return super.computeFallDamage(fallDistance, damageMultiplier) - 20;
    }

    public boolean isFollowing() {
        return this.following;
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "movement_controller", 2, this::movementAnimationPredicate));
    }

    private static final AnimationBuilder STANDING_CONSTANT_ANIMATION = new AnimationBuilder().loop("StandingConstant");
    private static final AnimationBuilder OFF_GROUND_CONSTANT_ANIMATION = new AnimationBuilder().loop("OffGroundConstant");
    private static final AnimationBuilder SITTING_CONSTANT_ANIMATION = new AnimationBuilder().loop("SittingConstant");
    private static final AnimationBuilder HOPPING_ANIMATION = new AnimationBuilder().loop("Hopping");

    private <E extends IAnimatable> PlayState movementAnimationPredicate(AnimationEvent<E> event) {
        AnimationBuilder animation;
        double animationSpeed = 1.0D;

        if (this.hasVehicle() || this.isInSittingPose()) animation = SITTING_CONSTANT_ANIMATION;

        else if (!this.isOnGround() || this.isLeaping()) animation = OFF_GROUND_CONSTANT_ANIMATION;

        else if (event.isMoving()) {
            animation = HOPPING_ANIMATION;
            animationSpeed = this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / DEFAULT_MOVEMENT_SPEED;
        }

        else animation = STANDING_CONSTANT_ANIMATION;

        event.getController().setAnimation(animation);
        event.getController().setAnimationSpeed(animationSpeed);
        return PlayState.CONTINUE;
    }

    @Environment(value = EnvType.CLIENT)
    public double getMovementTiltAngle(float delta) {
        return MathHelper.lerp(delta, this.prevMovementTiltAngle, this.movementTiltAngle);
    }

    @Environment(value = EnvType.CLIENT)
    public double getMovementTiltAngleTailUpper(float delta) {
        return MathHelper.lerp(delta, this.prevMovementTiltAngleTailUpper, this.movementTiltAngleTailUpper);
    }

    @Environment(value = EnvType.CLIENT)
    public double getMovementTiltAngleTailLower(float delta) {
        return MathHelper.lerp(delta, this.prevMovementTiltAngleTailLower, this.movementTiltAngleTailLower);
    }
}
