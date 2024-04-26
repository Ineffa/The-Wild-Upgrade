package com.ineffa.wondrouswilds.entities;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.entities.ai.FireflyHideGoal;
import com.ineffa.wondrouswilds.entities.ai.FireflyLandOnEntityGoal;
import com.ineffa.wondrouswilds.entities.ai.FireflyWanderFlyingGoal;
import com.ineffa.wondrouswilds.entities.ai.FireflyWanderLandGoal;
import com.ineffa.wondrouswilds.registry.WondrousWildsTags;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class FireflyEntity extends FlyingAndWalkingAnimalEntity implements IAnimatable {

    private static final TrackedData<Integer> LAND_ON_ENTITY_COOLDOWN = DataTracker.registerData(FireflyEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public FireflyEntity(EntityType<? extends FireflyEntity> entityType, World world) {
        super(entityType, world);

        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0f);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0f);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0f);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0f);

        this.ignoreCameraFrustum = true;
    }

    public static boolean canFireflySpawn(EntityType<FireflyEntity> entityType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos spawnAttemptPos, Random random) {
        if (!world.getBlockState(spawnAttemptPos.down()).isIn(WondrousWildsTags.BlockTags.FIREFLIES_SPAWNABLE_ON) || !FireflyEntity.canMobSpawn(entityType, world, spawnReason, spawnAttemptPos, random)) return false;

        RegistryEntry<Biome> biome = world.getBiome(spawnAttemptPos);
        int skylightLevel = world.getLightLevel(LightType.SKY, spawnAttemptPos);

        // Spawn immediately if the spawn position is underground and the biome allows underground spawning
        if (skylightLevel <= 0 && biome.isIn(WondrousWildsTags.BiomeTags.SPAWNS_FIREFLIES_UNDERGROUND)) return true;

        ServerWorld serverWorld = world.toServerWorld();

        // Otherwise, cancel if it is not raining and the biome requires it
        if (biome.isIn(WondrousWildsTags.BiomeTags.SPAWNS_FIREFLIES_ON_SURFACE_ONLY_IN_RAIN)) {
            if (!serverWorld.isRaining()) return false;
        }
        // Otherwise, cancel if the biome does not allow surface spawning at all
        else if (!biome.isIn(WondrousWildsTags.BiomeTags.SPAWNS_FIREFLIES_ON_SURFACE)) return false;

        // Finally, spawn if basic surface spawning conditions are met
        return serverWorld.isNight() && skylightLevel >= 6 && world.getLightLevel(LightType.BLOCK, spawnAttemptPos) <= 0;
    }

    // Removes the light level restriction set by AnimalEntity
    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0F;
    }

    public static DefaultAttributeContainer.Builder createFireflyAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(LAND_ON_ENTITY_COOLDOWN, 0);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.setLandOnEntityCooldown(nbt.getInt("LandOnEntityCooldown"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putInt("LandOnEntityCooldown", this.getLandOnEntityCooldown());
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (this.getRandom().nextBoolean() && !this.isFlying()) this.setFlying(true);

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public int getLandOnEntityCooldown() {
        return this.dataTracker.get(LAND_ON_ENTITY_COOLDOWN);
    }

    public void setLandOnEntityCooldown(int ticks) {
        this.dataTracker.set(LAND_ON_ENTITY_COOLDOWN, ticks);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0D));
        this.goalSelector.add(2, new FireflyHideGoal(this, 1.0D, 16, 16));
        if (WondrousWilds.config.mobSettings.firefliesLandOnMobs) this.goalSelector.add(3, new FireflyLandOnEntityGoal(this, LivingEntity.class));
        this.goalSelector.add(4, new FireflyWanderLandGoal(this, 1.0D));
        this.goalSelector.add(4, new FireflyWanderFlyingGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.hasVehicle()) {
            if (!this.getWorld().isClient() && (this.getRandom().nextInt(600) == 0 || this.shouldHide())) this.stopRiding();

            if (this.getVehicle() != null) this.setHeadYaw(this.getVehicle().getHeadYaw());
        }
        else if (this.getLandOnEntityCooldown() > 0) this.setLandOnEntityCooldown(this.getLandOnEntityCooldown() - 1);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (super.damage(source, amount)) {
            if (this.hasVehicle()) this.stopRiding();

            else if (!this.isFlying()) this.setFlying(true);

            return true;
        }
        else return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.7F;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return true;
    }

    public boolean canWander() {
        return !this.hasVehicle();
    }

    public boolean canSearchForEntityToLandOn() {
        return this.getLandOnEntityCooldown() <= 0 && !this.hasVehicle() && !this.shouldHide();
    }

    public boolean shouldHide() {
        return this.getWorld().isDay() && this.getWorld().getLightLevel(LightType.SKY, this.getBlockPos()) >= 6;
    }

    @Override
    public void stopRiding() {
        if (!this.isFlying()) this.setFlying(true);

        Entity vehicle = this.getVehicle();

        super.stopRiding();

        if (this.getWorld() instanceof ServerWorld serverWorld && vehicle instanceof PlayerEntity)
            for (ServerPlayerEntity player : serverWorld.getPlayers()) player.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(vehicle));
    }

    @Override
    public double getHeightOffset() {
        Entity vehicle = this.getVehicle();
        return vehicle == null ? 0.0D : WondrousWildsUtils.getMountedOffsetForStandingOn(vehicle);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        AnimationController<FireflyEntity> molangAnimationController = new AnimationController<>(this, "molangAnimationController", 2, this::molangAnimationPredicate);

        AnimationController<FireflyEntity> antennaMolangController = new AnimationController<>(this, "antennaMolangController", 2, this::antennaMolangPredicate);

        animationData.addAnimationController(molangAnimationController);
        animationData.addAnimationController(antennaMolangController);
    }

    private <E extends IAnimatable> PlayState molangAnimationPredicate(AnimationEvent<E> event) {
        if (this.isFlying()) event.getController().setAnimation(new AnimationBuilder().loop("flyingMolang"));

        else event.getController().setAnimation(new AnimationBuilder().loop("groundedMolang"));

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState antennaMolangPredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().loop("antennaeMolang"));

        return PlayState.CONTINUE;
    }
}
