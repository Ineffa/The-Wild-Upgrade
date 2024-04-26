package com.ineffa.wondrouswilds.entities;

import com.google.common.base.MoreObjects;
import com.ineffa.wondrouswilds.blocks.InhabitableNestBlock;
import com.ineffa.wondrouswilds.blocks.TreeHollowBlock;
import com.ineffa.wondrouswilds.blocks.entity.InhabitableNestBlockEntity;
import com.ineffa.wondrouswilds.blocks.entity.NestBoxBlockEntity;
import com.ineffa.wondrouswilds.entities.ai.*;
import com.ineffa.wondrouswilds.entities.ai.navigation.HasCustomReachedDestinationDistance;
import com.ineffa.wondrouswilds.entities.eggs.LaysEggsInNests;
import com.ineffa.wondrouswilds.entities.eggs.NesterEgg;
import com.ineffa.wondrouswilds.networking.packets.s2c.BlockBreakingParticlesPacket;
import com.ineffa.wondrouswilds.networking.packets.s2c.NestTransitionStartPacket;
import com.ineffa.wondrouswilds.networking.packets.s2c.WoodpeckerInteractWithBlockPacket;
import com.ineffa.wondrouswilds.registry.*;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import com.ineffa.wondrouswilds.util.fakeplayer.WoodpeckerFakePlayer;
import com.ineffa.wondrouswilds.util.fakeplayer.WoodpeckerItemUsageContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
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

import java.util.*;
import java.util.function.Predicate;

import static com.ineffa.wondrouswilds.WondrousWilds.config;
import static com.ineffa.wondrouswilds.util.WondrousWildsUtils.HORIZONTAL_DIRECTIONS;
import static com.ineffa.wondrouswilds.util.WondrousWildsUtils.TREE_HOLLOW_MAP;

public class WoodpeckerEntity extends FlyingAndWalkingAnimalEntity implements BlockNester, LaysEggsInNests, Tameable, Angerable, HasCustomReachedDestinationDistance, IAnimatable {

    public static final String CLING_POS_KEY = "ClingPos";
    public static final String PLAY_SESSIONS_BEFORE_TAME_KEY = "PlaySessionsBeforeTame";
    public static final String TAME_KEY = "Tame";
    public static final String MATE_KEY = "Mate";
    public static final String HAS_EGGS_KEY = "HasEggs";
    public static final String BONDING_TASK_COOLDOWN_KEY = "BondingTaskCooldown";
    public static final String BONDING_TASK_MANAGER_KEY = "Bonding";
    public static final String OWNER_KEY = "Owner";
    public static final String IS_FOLLOWING_KEY = "IsFollowing";

    public static final int DEFAULT_PECK_INTERVAL = 10;
    public static final int PECKS_NEEDED_FOR_NEST = 200;
    public static final int WOODPECKER_BABY_AGE = -168000;

    public static final EntityDimensions BABY_SIZE = EntityDimensions.fixed(0.1875F, 0.28125F);

    private int ticksPeekingOutOfNest;

    private static final TrackedData<BlockPos> CLING_POS = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Integer> CLING_ANGLE = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PECK_CHAIN_LENGTH = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PECK_INTERVAL = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DRUMMING_TICKS = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANGER_TICKS = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> CHIRP_DELAY = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> TAME = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<UUID>> MATE = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Boolean> HAS_EGGS = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> FAILING_TO_FLY_TICKS = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<NbtCompound> BONDING_TASK_MANAGER = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Boolean> FOLLOWING = DataTracker.registerData(WoodpeckerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(10, 15);
    @Nullable
    private UUID angryAt;
    private int drumAttackCooldown;

    private int peckChainTicks;
    private int consecutivePecks;
    @Nullable
    private ItemEntity pickupTarget;

    private Optional<NestTransition> currentNestTransition = Optional.empty();

    private Direction clingSide;

    private int cannotEnterNestTicks;
    @Nullable
    private BlockPos nestPos;

    private boolean isPeekingOutOfNest;

    private int playSessionsBeforeTame;

    private int newBondingTaskCooldown;
    private BondingTaskManager bondingTaskManager = new BondingTaskManager();
    private int startBondingTaskCooldown;
    private static final Predicate<WoodpeckerEntity> START_BONDING_TASK_PREDICATE = woodpecker -> woodpecker.startBondingTaskCooldown <= 0 && !woodpecker.isFlying() && !woodpecker.isPecking() && woodpecker.canBond() && woodpecker.canWander();

    private byte chirpCount, nextChirpCount, nextChirpSpeed;
    private float nextChirpPitch;

    /* Client fields */
    public float flapSpeed, prevFlapAngle, flapAngle;

    private int blinkTimer;

    public WoodpeckerEntity(EntityType<? extends WoodpeckerEntity> entityType, World world) {
        super(entityType, world);

        this.lookControl = new WoodpeckerLookControl(this);

        this.ignoreCameraFrustum = true;
    }

    public static DefaultAttributeContainer.Builder createWoodpeckerAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(CLING_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(CLING_ANGLE, 0);
        this.dataTracker.startTracking(PECK_CHAIN_LENGTH, 0);
        this.dataTracker.startTracking(PECK_INTERVAL, DEFAULT_PECK_INTERVAL);
        this.dataTracker.startTracking(DRUMMING_TICKS, 0);
        this.dataTracker.startTracking(ANGER_TICKS, 0);
        this.dataTracker.startTracking(CHIRP_DELAY, (byte) 0);
        this.dataTracker.startTracking(TAME, false);
        this.dataTracker.startTracking(MATE, Optional.empty());
        this.dataTracker.startTracking(HAS_EGGS, false);
        this.dataTracker.startTracking(FAILING_TO_FLY_TICKS, 0);
        this.dataTracker.startTracking(BONDING_TASK_MANAGER, BondingTaskManager.getDefaultNbt());
        this.dataTracker.startTracking(OWNER, Optional.empty());
        this.dataTracker.startTracking(FOLLOWING, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.put(CLING_POS_KEY, NbtHelper.fromBlockPos(this.getClingPos()));

        if (this.hasNestPos()) nbt.put(NEST_POS_KEY, NbtHelper.fromBlockPos(Objects.requireNonNull(this.getNestPos())));

        nbt.putInt(TICKS_PEEKING_OUT_OF_NEST_KEY, this.getTicksPeekingOutOfNest());

        nbt.putInt(PLAY_SESSIONS_BEFORE_TAME_KEY, this.playSessionsBeforeTame);

        nbt.putBoolean(TAME_KEY, this.isTame());

        if (this.getMateUuid() != null) nbt.putUuid(MATE_KEY, this.getMateUuid());

        nbt.putBoolean(HAS_EGGS_KEY, this.hasEggs());

        nbt.putInt(BONDING_TASK_COOLDOWN_KEY, this.newBondingTaskCooldown);

        nbt.put(BONDING_TASK_MANAGER_KEY, this.bondingTaskManager.toNbt(true));

        if (this.getOwnerUuid() != null) nbt.putUuid(OWNER_KEY, this.getOwnerUuid());

        nbt.putBoolean(IS_FOLLOWING_KEY, this.isFollowing());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        BlockPos clingPos = NbtHelper.toBlockPos(nbt.getCompound(CLING_POS_KEY));
        if (!this.isClinging() && !WondrousWildsUtils.isPosAtWorldOrigin(clingPos)) this.tryClingingTo(clingPos);

        if (nbt.contains(NEST_POS_KEY)) this.setNestPos(NbtHelper.toBlockPos(nbt.getCompound(NEST_POS_KEY)));

        this.playSessionsBeforeTame = nbt.getInt(PLAY_SESSIONS_BEFORE_TAME_KEY);

        this.setTame(nbt.getBoolean(TAME_KEY));

        if (nbt.contains(MATE_KEY)) this.setMateUuid(nbt.getUuid(MATE_KEY));

        this.setHasEggs(nbt.getBoolean(HAS_EGGS_KEY));

        this.newBondingTaskCooldown = nbt.getInt(BONDING_TASK_COOLDOWN_KEY);

        this.updateBondingTaskManager(nbt.getCompound(BONDING_TASK_MANAGER_KEY), false);

        if (nbt.contains(OWNER_KEY)) this.setOwnerUuid(nbt.getUuid(OWNER_KEY));

        this.setIsFollowing(nbt.getBoolean(IS_FOLLOWING_KEY));
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

        if (BONDING_TASK_MANAGER.equals(data)) this.bondingTaskManager = new BondingTaskManager(this.getBondingTaskManagerNbt());
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (this.playSessionsBeforeTame <= 0 && !this.isTame()) this.playSessionsBeforeTame = this.getRandom().nextBetween(5, 15);

        this.initEquipment(world.getRandom(), difficulty);

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        if (random.nextInt(4) != 0) return;

        // ITEM CHANCES:
        // 1% Wooden Axe
        // 4% Music Disc
        // 8% Glass Bottle
        // 12% Honeycomb
        // 18% Bone Meal
        // 24% Flower
        // 33% Seeds
        Item itemToHold;
        int i = 1 + random.nextInt(100);
        if (i <= 1) itemToHold = Items.WOODEN_AXE;
        else if (i <= 5) itemToHold = WondrousWildsItems.MUSIC_DISC_AVIAN;
        else if (i <= 13) itemToHold = Items.GLASS_BOTTLE;
        else if (i <= 25) itemToHold = Items.HONEYCOMB;
        else if (i <= 43) itemToHold = Items.BONE_MEAL;
        else if (i <= 67) itemToHold = switch (random.nextInt(5)) {
            default -> Items.LILY_OF_THE_VALLEY;
            case 1 -> WondrousWildsItems.PURPLE_VIOLET;
            case 2 -> WondrousWildsItems.PINK_VIOLET;
            case 3 -> WondrousWildsItems.RED_VIOLET;
            case 4 -> WondrousWildsItems.WHITE_VIOLET;
        };
        else itemToHold = switch (random.nextInt(4)) {
            default -> Items.WHEAT_SEEDS;
            case 1 -> Items.BEETROOT_SEEDS;
            case 2 -> Items.PUMPKIN_SEEDS;
            case 3 -> Items.MELON_SEEDS;
        };

        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(itemToHold));
    }

    @Override
    protected void loot(ItemEntity itemEntity) {
        ItemStack heldItem = this.getStackInHand(Hand.MAIN_HAND);
        if (!heldItem.isEmpty()) this.dropStack(heldItem);

        ItemStack itemStack = itemEntity.getStack();
        this.setStackInHand(Hand.MAIN_HAND, itemStack.copy());
        this.triggerItemPickedUpByEntityCriteria(itemEntity);
        this.sendPickup(itemEntity, itemStack.getCount());
        itemEntity.discard();
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public void setFlying(boolean flying) {
        boolean clinging = this.isClinging();

        if (this.isAbleToFly()) super.setFlying(flying);
        else if (flying && !clinging) this.failToFly();

        if (flying) {
            if (clinging) this.stopClinging();
            else if (this.hasVehicle()) this.stopRiding();
        }
    }

    public BlockPos getClingPos() {
        return this.dataTracker.get(CLING_POS);
    }

    public void setClingPos(BlockPos pos) {
        this.dataTracker.set(CLING_POS, pos);
    }

    public boolean isClinging() {
        BlockPos clingPos = this.dataTracker.get(CLING_POS);
        return clingPos != null && !WondrousWildsUtils.isPosAtWorldOrigin(clingPos);
    }

    public boolean tryClingingTo(BlockPos clingPos) {
        Direction clingSide = Direction.fromHorizontal(this.getRandom().nextInt(4));
        double closestSideDistance = 100.0D;
        for (Direction side : HORIZONTAL_DIRECTIONS) {
            BlockPos offsetPos = clingPos.offset(side);
            if (!this.getWorld().isAir(offsetPos) || !this.getWorld().getBlockState(clingPos).isSideSolidFullSquare(this.getWorld(), clingPos, side)) continue;

            double distanceFromSide = this.getBlockPos().getSquaredDistance(offsetPos);
            if (distanceFromSide < closestSideDistance) {
                clingSide = side;
                closestSideDistance = distanceFromSide;
            }
        }
        if (closestSideDistance == 100.0D) return false;

        this.setClingPos(clingPos);
        this.clingSide = clingSide;

        BlockPos pos = clingPos.offset(clingSide);
        this.refreshPositionAndAngles(pos, this.getYaw(), this.getPitch());

        this.setFlying(false);

        for (WoodpeckerEntity woodpeckerInSpot : this.getWorld().getEntitiesByClass(WoodpeckerEntity.class, this.getBoundingBox().expand(0.5D), woodpecker -> woodpecker != this && woodpecker.isClinging()))
            woodpeckerInSpot.setFlying(true);

        return true;
    }

    public void stopClinging() {
        this.setClingPos(BlockPos.ORIGIN);
        this.clingSide = null;

        if (this.isPecking()) this.stopPecking(true);
        else this.resetConsecutivePecks();
    }

    public boolean canContinueClinging() {
        return this.canClingToPos(this.getClingPos(), false, this.clingSide) && this.getWorld().isAir(this.getBlockPos());
    }

    public boolean canClingToPos(BlockPos pos, boolean checkForSpace, @Nullable Direction... sidesToCheck) {
        Direction[] directionsToCheck = sidesToCheck != null ? sidesToCheck : HORIZONTAL_DIRECTIONS;

        if (checkForSpace) {
            boolean hasOpenSpace = false;
            for (Direction direction : directionsToCheck) {
                if (this.getWorld().isAir(pos.offset(direction))) {
                    hasOpenSpace = true;
                    break;
                }
            }
            if (!hasOpenSpace) return false;
        }

        BlockState state = this.getWorld().getBlockState(pos);

        boolean hasSolidSide = false;
        for (Direction direction : directionsToCheck) {
            if (state.isSideSolidFullSquare(this.getWorld(), pos, direction)) {
                hasSolidSide = true;
                break;
            }
        }
        if (!hasSolidSide) return false;

        return (state.getBlock() instanceof PillarBlock && state.isIn(BlockTags.OVERWORLD_NATURAL_LOGS) && state.get(PillarBlock.AXIS).isVertical()) || state.isIn(WondrousWildsTags.BlockTags.WOODPECKERS_INTERACT_WITH);
    }

    public boolean canMakeNests() {
        return config.mobSettings.woodpeckersBuildNests && !this.isFollowing() && !this.isBaby() && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    public boolean canMakeNestInPos(BlockPos pos) {
        Block block = this.getWorld().getBlockState(pos).getBlock();
        return TREE_HOLLOW_MAP.containsKey(block);
    }

    public boolean canInteractWithPos(BlockPos pos) {
        return this.getWorld().getBlockState(pos).isIn(WondrousWildsTags.BlockTags.WOODPECKERS_INTERACT_WITH);
    }

    public boolean isMakingNest() {
        return this.canMakeNests() && this.canMakeNestInPos(this.getClingPos()) && (this.getConsecutivePecks() > 0 || this.isPecking());
    }

    public int getClingAngle() {
        return this.dataTracker.get(CLING_ANGLE);
    }

    public void setClingAngle(int angle) {
        this.dataTracker.set(CLING_ANGLE, angle);
    }

    public boolean isPecking() {
        return this.getCurrentPeckChainLength() > 0;
    }

    public int getCurrentPeckChainLength() {
        return this.dataTracker.get(PECK_CHAIN_LENGTH);
    }

    public void setPeckChainLength(int length) {
        this.dataTracker.set(PECK_CHAIN_LENGTH, length);
    }

    public int getPeckInterval() {
        return this.dataTracker.get(PECK_INTERVAL);
    }

    public void setPeckInterval(int interval) {
        this.dataTracker.set(PECK_INTERVAL, interval);
    }

    public int calculateTicksForPeckChain(int length, int interval) {
        return interval + (interval * length);
    }

    public void startPeckChain(int length, int interval) {
        this.setPeckChainLength(length);
        this.setPeckInterval(interval);
        this.peckChainTicks = this.calculateTicksForPeckChain(length, interval);
    }

    public void startPeckChain() {
        this.startPeckChain(1 + this.getRandom().nextInt(4), this.getRandom().nextBetween(9, 11));
    }

    public void stopPecking(boolean resetConsecutive) {
        this.setPeckChainLength(0);
        this.setPeckInterval(DEFAULT_PECK_INTERVAL);
        this.peckChainTicks = 0;

        if (resetConsecutive) this.resetConsecutivePecks();
    }

    public int getConsecutivePecks() {
        return this.consecutivePecks;
    }

    public void setConsecutivePecks(int pecks) {
        this.consecutivePecks = pecks;
    }

    public void resetConsecutivePecks() {
        this.setConsecutivePecks(0);
    }

    @Nullable
    public ItemEntity getPickupTarget() {
        return this.pickupTarget;
    }

    public void setPickupTarget(@Nullable ItemEntity pickupTarget) {
        this.pickupTarget = pickupTarget;
    }

    public double getPeckReach() {
        return 1.3D;
    }

    public byte getChirpDelay() {
        return this.dataTracker.get(CHIRP_DELAY);
    }

    public void setChirpDelay(byte speed) {
        this.dataTracker.set(CHIRP_DELAY, speed);
    }

    public void startChirping(byte count, byte speed) {
        this.chirpCount = (byte) 0;
        this.nextChirpCount = count;

        this.nextChirpSpeed = speed;
        this.setChirpDelay(speed);

        this.nextChirpPitch = this.getSoundPitch();
    }

    public void stopChirping() {
        this.chirpCount = (byte) 0;
        this.nextChirpCount = (byte) 0;

        this.nextChirpSpeed = (byte) 0;
        this.setChirpDelay((byte) 0);
    }

    public void progressTame() {
        if (this.playSessionsBeforeTame <= 1) this.finishTame();
        else {
            --this.playSessionsBeforeTame;
            this.showTameParticles(false);
        }

        this.resetConsecutivePecks();
    }

    public void finishTame() {
        this.setTame(true);
        this.showTameParticles(true);
    }

    public boolean isTame() {
        return this.dataTracker.get(TAME);
    }

    public void setTame(boolean tame) {
        this.dataTracker.set(TAME, tame);
    }

    public boolean canBond() {
        return this.isBaby() && this.isTame() && this.getOwnerUuid() == null;
    }

    public void addNewBondingTask() {
        this.newBondingTaskCooldown = config.mobSettings.woodpeckerBondingTaskInterval;

        this.bondingTaskManager.tasks.add(new BondingTask(this.bondingTaskManager.nextBlockAmount));
        ++this.bondingTaskManager.nextBlockAmount;

        this.showTameParticles(true);
    }

    @Nullable
    public BondingTask getCurrentBondingTask() {
        try {
            return this.bondingTaskManager.tasks.get(0);
        }
        catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    public void fulfillCurrentBondingTask() {
        this.bondingTaskManager.tasks.remove(0);
        this.refreshBondingTasks();

        this.showTameParticles(true);
        this.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
    }

    public void resetBonding(boolean refresh) {
        this.bondingTaskManager.reset();
        if (refresh) this.refreshBondingTasks();
    }

    public void refreshBondingTasks() {
        this.updateBondingTaskManager(this.bondingTaskManager.toNbt(false), true);
    }

    public NbtCompound getBondingTaskManagerNbt() {
        return this.dataTracker.get(BONDING_TASK_MANAGER);
    }

    private void updateBondingTaskManager(NbtCompound compound, boolean clientOnly) {
        if (!clientOnly) this.bondingTaskManager = new BondingTaskManager(compound);
        this.dataTracker.set(BONDING_TASK_MANAGER, compound);
    }

    public void setComparingWithBondingTarget(boolean comparing) {
        if (this.getBondingTarget() instanceof CanBondWithWoodpecker bondingTarget)
            bondingTarget.setComparingWoodpecker(comparing ? this : null);
    }

    @Nullable
    public PlayerEntity getBondingTarget() {
        try {
            if (this.bondingTaskManager.targetUuid == null) return null;
            return this.getWorld().getPlayerByUuid(this.bondingTaskManager.targetUuid);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }


    @Nullable
    @Override
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER, Optional.ofNullable(uuid));
    }

    public void setOwner(ServerPlayerEntity player) {
        if (!this.isTame()) this.setTame(true);

        this.setOwnerUuid(player.getUuid());
        Criteria.TAME_ANIMAL.trigger(player, this);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        try {
            UUID uUID = this.getOwnerUuid();
            if (uUID == null) return null;
            return this.getWorld().getPlayerByUuid(uUID);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    public boolean isFollowing() {
        return this.dataTracker.get(FOLLOWING);
    }

    public void setIsFollowing(boolean following) {
        this.dataTracker.set(FOLLOWING, following);
    }

    public double getFollowingWanderRadiusAroundOwner() {
        return 32.0D;
    }

    public double getMaximumFollowingDistance() {
        return 200.0D;
    }

    @Nullable
    public UUID getMateUuid() {
        return this.dataTracker.get(MATE).orElse(null);
    }

    public void setMateUuid(@Nullable UUID uuid) {
        this.dataTracker.set(MATE, Optional.ofNullable(uuid));
    }

    public boolean hasMate() {
        return this.getMateUuid() != null;
    }

    public boolean isMate(UUID uuidToCheck) {
        return Objects.equals(this.getMateUuid(), uuidToCheck);
    }

    public boolean isMate(Entity entity) {
        return this.isMate(entity.getUuid());
    }

    public boolean hasEggs() {
        return this.dataTracker.get(HAS_EGGS);
    }

    public void setHasEggs(boolean hasEggs) {
        this.dataTracker.set(HAS_EGGS, hasEggs);
    }

    public int getDrummingTicks() {
        return this.dataTracker.get(DRUMMING_TICKS);
    }

    public void setDrummingTicks(int ticks) {
        this.dataTracker.set(DRUMMING_TICKS, ticks);
    }

    public boolean isDrumming() {
        return this.getDrummingTicks() > 0;
    }

    public void startDrumming(boolean attack) {
        this.setDrummingTicks(attack ? 50 : 55);
    }

    public boolean canStartDrumAttack() {
        return this.drumAttackCooldown <= 0 && !this.isDrumming() && !this.isPecking() && this.getMainHandStack().isEmpty();
    }

    public void applyDrumAttackCooldown() {
        this.drumAttackCooldown = 600;
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        if (this.isFlying()) this.setFlying(false);
        return super.startRiding(entity, force);
    }

    @Override
    public void stopRiding() {
        if (!this.isFlying() && this.isAbleToFly()) this.setFlying(true);
        if (this.isDrumming()) this.setDrummingTicks(0);

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
    public int getNestCapacityWeight() {
        return WondrousWildsEntities.getDefaultNestCapacityWeightFor((EntityType<? extends BlockNester>) this.getType(), this.isBaby());
    }

    @Nullable
    @Override
    public BlockPos getNestPos() {
        return this.nestPos;
    }

    @Override
    public void setNestPos(@Nullable BlockPos pos) {
        this.nestPos = pos;
    }

    @Override
    public Optional<NestTransition> getCurrentNestTransition() {
        return this.currentNestTransition;
    }

    @Override
    public int getDurationOfNestTransitionType(NestTransitionType type) {
        return switch (type) {
            case ENTER -> 0;
            case EXIT -> 0;
            case PEEK -> 0;
            case UNPEEK -> 0;
        };
    }

    @Override
    public void startNewNestTransition(NestTransitionType transitionType) {
        NestTransition transition = new NestTransition(transitionType, this, this.getWorld().isClient());
        this.currentNestTransition = Optional.of(transition);

        if (this.getWorld().isClient()) return;

        if (this.getNestPos() == null) return;
        BlockState nestState = this.getWorld().getBlockState(this.getNestPos());
        if (!(nestState.getBlock() instanceof InhabitableNestBlock)) return;

        this.setFlying(false);

        Direction nestDirection = nestState.get(InhabitableNestBlock.FACING);
        BlockPos pos = this.getNestPos().offset(nestDirection);
        this.refreshPositionAndAngles(pos, this.getYaw(), this.getPitch());

        int horizontal = nestDirection.getOpposite().getHorizontal();
        this.setYaw(horizontal * 90);
        this.setBodyYaw(this.getYaw());
        this.setHeadYaw(this.getYaw());

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(this.getId());
        buf.writeEnumConstant(transitionType);
        buf.writeByte(horizontal);
        for (ServerPlayerEntity receiver : PlayerLookup.tracking(this)) ServerPlayNetworking.send(receiver, NestTransitionStartPacket.ID, buf);
    }

    @Override
    public void finishCurrentNestTransition() {
        if (!this.getWorld().isClient()) {
            this.getCurrentNestTransition().ifPresent((nestTransition) -> {
                if (nestTransition.getType() == NestTransitionType.ENTER && this.getWorld().getBlockEntity(this.getNestPos()) instanceof InhabitableNestBlockEntity nestBlock) {
                    if (!nestBlock.tryAddingInhabitant(this)) {
                        this.setCannotInhabitNestTicks(this.getMinTicksOutOfNest());
                        this.clearNestPos();
                    }
                }
            });
        }

        BlockNester.super.finishCurrentNestTransition();
    }

    @Override
    public void clearCurrentNestTransition() {
        this.currentNestTransition = Optional.empty();
    }

    @Override
    public boolean isPeekingOutOfNest() {
        return false;
    }

    @Override
    public int getTicksPeekingOutOfNest() {
        return this.ticksPeekingOutOfNest;
    }

    @Override
    public int getMinTicksInNest() {
        return 200;
    }

    @Override
    public int getMinTicksOutOfNest() {
        return 600;
    }

    @Override
    public int getCannotInhabitNestTicks() {
        if (this.getWorld().isClient()) return 0;

        return this.cannotEnterNestTicks;
    }

    @Override
    public void setCannotInhabitNestTicks(int ticks) {
        if (this.getWorld().isClient()) return;

        this.cannotEnterNestTicks = ticks;
    }

    @Override
    public boolean shouldFindNest() {
        return BlockNester.super.shouldFindNest() && !this.hasAttackTarget() && this.getAttacker() == null && !this.isFollowing();
    }

    @Override
    public boolean shouldReturnToNest() {
        if (!this.hasNestPos() || this.getCannotInhabitNestTicks() > 0 || this.isFollowing()) return false;

        if (this.hasAttackTarget() || this.getAttacker() != null) return false;

        return this.getWorld().isNight() || this.getWorld().isRaining() || this.hasEggs();
    }

    @Override
    public boolean shouldDefendNestAgainstVisitor(LivingEntity visitor, InhabitableNestBlockEntity.InhabitantAlertScenario scenario) {
        if (this.isBaby()) return false;

        if (visitor instanceof PlayerEntity && this.isTame()) return false;

        return !(visitor instanceof WoodpeckerEntity woodpecker && (scenario == InhabitableNestBlockEntity.InhabitantAlertScenario.VISITOR || woodpecker.isBaby() || this.isMate(woodpecker)));
    }

    @Override
    public void beforeExitingNest(BlockPos nestPos) {
        if (!this.isFlying() && this.isAbleToFly()) this.setFlying(true);

        if (!(this.getWorld().getBlockEntity(nestPos) instanceof InhabitableNestBlockEntity nest)) return;

        if (this.hasEggs()) {
            boolean laidEggs = false;

            int eggsToLay = this.getRandom().nextBetween(1, 3);
            for (int i = 0; i < eggsToLay; ++i) {
                if (!nest.tryAddingEgg(this.createEggToLay())) break;
                laidEggs = true;
            }

            this.setHasEggs(!laidEggs);
        }

        if (!this.isTame()) return;

        if (this.getMainHandStack().isEmpty() && nest instanceof NestBoxBlockEntity nestBox && !nestBox.isEmpty()) {
            int slotToTakeFrom = 0;
            ItemStack nestBoxItem = nestBox.getStack(slotToTakeFrom);
            if (!nestBoxItem.isEmpty()) {
                this.setStackInHand(Hand.MAIN_HAND, nestBoxItem.copy());
                nestBox.removeStack(slotToTakeFrom);
            }
        }
    }

    @Override
    public void onBeginExitingNest(BlockPos nestPos, InhabitableNestBlockEntity.InhabitantReleaseReason reason) {
        if (reason == InhabitableNestBlockEntity.InhabitantReleaseReason.NATURAL && this.isTame() && this.getRandom().nextInt(10) == 0) this.dropItem(WondrousWildsItems.WOODPECKER_CREST_FEATHER);
    }

    @Override
    public int getWanderRadiusFromNest() {
        return 64;
    }

    @Override
    public int getMaxDistanceFromNest() {
        return 128;
    }

    @Override
    public float getReachedDestinationDistance() {
        return 0.3F;
    }

    @Nullable
    public BlockPos getPosToWanderTowards() {
        BlockPos moveTowardsPos = null;

        LivingEntity owner;
        if (this.isFollowing() && (owner = this.getOwner()) != null) {
            BlockPos ownerPos = owner.getBlockPos();
            if (!ownerPos.isWithinDistance(this.getPos(), this.getFollowingWanderRadiusAroundOwner()))
                moveTowardsPos = ownerPos;
        }

        else if (this.hasNestPos() && !Objects.requireNonNull(this.getNestPos()).isWithinDistance(this.getPos(), this.getWanderRadiusFromNest()))
            moveTowardsPos = this.getNestPos();

        return moveTowardsPos;
    }

    @Override
    public int getAngerTime() {
        return this.dataTracker.get(ANGER_TICKS);
    }

    @Override
    public void setAngerTime(int ticks) {
        this.dataTracker.set(ANGER_TICKS, ticks);
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.getRandom()));
    }

    public boolean canWander() {
        return !this.isClinging() && !this.hasAttackTarget() && this.getAttacker() == null && !this.isLeashed();
    }

    public boolean hasAttackTarget() {
        return this.getTarget() != null;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);

        if (target != null && this.isClinging()) this.setFlying(true);
    }

    public boolean shouldDefendOwnerFromTarget(@Nullable LivingEntity target) {
        if (!(target instanceof MobEntity) || target instanceof CreeperEntity) return false;

        LivingEntity owner = this.getOwner();
        if (owner == null) return false;

        LivingEntity mobTarget = ((MobEntity) target).getTarget();
        return mobTarget != null && ((mobTarget.equals(owner) && owner.hasStatusEffect(WondrousWildsStatusEffects.WOODPECKERS_WARCRY)) || (mobTarget instanceof WoodpeckerEntity woodpecker && woodpecker.getOwner() == owner));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WoodpeckerEscapeDangerGoal(this, 1.25D, 16, 8));
        this.goalSelector.add(1, new WoodpeckerFleeEntityGoal<>(this, FoxEntity.class, 24.0F, 1.0D, 1.25D));
        this.goalSelector.add(2, new WoodpeckerFleeEntityGoal<>(this, WoodpeckerEntity.class, 24.0F, 1.0D, 1.0D, entity -> ((WoodpeckerEntity) entity).getTarget() == this));
        this.goalSelector.add(3, new WoodpeckerFleeEntityGoal<>(this, PlayerEntity.class, 12.0F, 1.0D, 1.25D, entity -> !this.isTame()));
        this.goalSelector.add(4, new WoodpeckerAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new WoodpeckerMateGoal(this, 1.0D));
        this.goalSelector.add(5, new WoodpeckerBondingTaskGoal(this, 1.0D));
        this.goalSelector.add(6, new WoodpeckerCatchUpToOwnerGoal(this, 16.0F, 1.0D));
        this.goalSelector.add(7, new FindOrReturnToBlockNestGoal(this, 1.0D, 24, 24));
        if (config.mobSettings.woodpeckersInteractWithBlocks) this.goalSelector.add(8, new WoodpeckerPlayWithBlockGoal(this, 1.0D, 24, 24));
        this.goalSelector.add(9, new WoodpeckerClingToLogGoal(this, 1.0D, 24, 24));
        this.goalSelector.add(10, new WoodpeckerWanderLandGoal(this, 1.0D));
        this.goalSelector.add(10, new WoodpeckerWanderFlyingGoal(this));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 16.0F));
        this.goalSelector.add(11, new LookAtEntityGoal(this, MobEntity.class, 16.0F));
        this.goalSelector.add(12, new LookAroundGoal(this));

        this.targetSelector.add(0, new WoodpeckerRescueOwnerGoal(this));
        this.targetSelector.add(1, new WoodpeckerDefendOwnerGoal<>(this, LivingEntity.class, false, this::shouldDefendOwnerFromTarget));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PhantomEntity.class, true));
    }

    @Override
    public void breed(ServerWorld world, AnimalEntity other) {
        if (!(other instanceof WoodpeckerEntity otherWoodpecker)) return;

        this.setMateUuid(otherWoodpecker.getUuid());
        otherWoodpecker.setMateUuid(this.getUuid());

        this.setHasEggs(true);

        ServerPlayerEntity serverPlayerEntity = this.getLovingPlayer();
        if (serverPlayerEntity == null && other.getLovingPlayer() != null) serverPlayerEntity = other.getLovingPlayer();

        if (serverPlayerEntity != null) {
            serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
            Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this, other, null);
        }

        this.setBreedingAge(6000);
        other.setBreedingAge(6000);

        this.resetLoveTicks();
        other.resetLoveTicks();

        world.sendEntityStatus(this, EntityStatuses.ADD_BREEDING_PARTICLES);

        if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity otherParent) {
        if (!(otherParent instanceof WoodpeckerEntity otherWoodpecker)) return null;

        WoodpeckerEntity babyWoodpecker = WondrousWildsEntities.WOODPECKER.create(world);

        if (babyWoodpecker != null) {
            babyWoodpecker.playSessionsBeforeTame = this.getRandom().nextBetween(5, 15);

            if (this.isTame()) babyWoodpecker.setTame(true);

            List<WoodpeckerEntity> parentsWithNest = new ArrayList<>();
            if (this.hasNestPos()) parentsWithNest.add(this);
            if (otherWoodpecker != this && otherWoodpecker.hasNestPos()) parentsWithNest.add(otherWoodpecker);

            if (!parentsWithNest.isEmpty()) babyWoodpecker.setNestPos(parentsWithNest.get(this.getRandom().nextInt(parentsWithNest.size())).getNestPos());
        }

        return babyWoodpecker;
    }

    @Override
    public void setBaby(boolean baby) {
        this.setBreedingAge(baby ? WOODPECKER_BABY_AGE : 0);
    }

    @Override
    protected void onGrowUp() {
        super.onGrowUp();

        if (this.bondingTaskManager.tasks.isEmpty()) {
            if (this.getBondingTarget() instanceof ServerPlayerEntity newOwner)
                this.setOwner(newOwner);
        }
        this.resetBonding(true);

        this.showTameParticles(false);
    }

    public boolean isAdolescent() {
        return this.isBaby() && this.getBreedingAge() >= -72000;
    }

    @Override
    public boolean isAbleToFly() {
        return !this.isBaby() || this.isAdolescent();
    }

    public int getFailingToFlyTicks() {
        return this.dataTracker.get(FAILING_TO_FLY_TICKS);
    }

    public void setFailingToFlyTicks(int ticks) {
        this.dataTracker.set(FAILING_TO_FLY_TICKS, ticks);
    }

    public boolean isFailingToFly() {
        return this.getFailingToFlyTicks() > 0;
    }

    public void failToFly() {
        if (this.isFailingToFly()) return;

        this.setFailingToFlyTicks(55);
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (this.hasEggs() || !super.canBreedWith(other)) return false;

        WoodpeckerEntity otherWoodpecker = (WoodpeckerEntity) other;

        if (otherWoodpecker.hasEggs()) return false;

        if (this.hasMate()) return this.isMate(otherWoodpecker.getUuid());

        return !otherWoodpecker.hasMate();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.SWEET_BERRIES);
    }

    @Override
    public NesterEgg createEggToLay() {
        return new NesterEgg(
                WondrousWildsEntities.WOODPECKER,
                null,
                false,
                new Pair<>(48000, 72000),
                this.getRandom()
        );
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            this.flapSpeed = MathHelper.clamp(1.0F - (this.limbDistance * 0.5F), 0.0F, 1.0F);
            this.prevFlapAngle = this.flapAngle;
            this.flapAngle += this.flapSpeed;

            if (this.blinkTimer-- <= 0) this.blinkTimer = 20 + this.getRandom().nextInt(41);

            if (this.hasEggs() && this.getRandom().nextInt(3) == 0)
                this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0D), this.getRandomBodyY(), this.getParticleZ(1.0D), 0.0D, 0.0D, 0.0D);
        }
        else {
            if (this.age % 200 == 0)
                if (this.hasNestPos() && !this.getNestPos().isWithinDistance(this.getPos(), this.getMaxDistanceFromNest())) this.clearNestPos();

            if (this.isPecking()) {
                if (this.peckChainTicks <= 0) this.stopPecking(false);
                else {
                    int currentPeckInterval = this.getPeckInterval();
                    if (this.peckChainTicks % currentPeckInterval == 0 && this.peckChainTicks != this.calculateTicksForPeckChain(this.getCurrentPeckChainLength(), currentPeckInterval)) {
                        SoundEvent peckSound = null;

                        if (this.pickupTarget != null) {
                            if (this.distanceTo(this.pickupTarget) <= this.getPeckReach()) {
                                if (!this.pickupTarget.isRemoved() && !this.pickupTarget.getStack().isEmpty() && !this.pickupTarget.cannotPickup() && this.canGather(this.pickupTarget.getStack()))
                                    this.loot(this.pickupTarget);
                                this.pickupTarget = null;
                            }
                        }
                        else if (this.isAttacking() && this.hasAttackTarget()) {
                            LivingEntity attackTarget = this.getTarget();
                            if (this.distanceTo(attackTarget) <= this.getPeckReach() + 1.0D) this.tryAttack(attackTarget);
                        }
                        else if (this.isClinging() && this.canMakeNests() && this.canMakeNestInPos(this.getClingPos()) && this.canContinueClinging()) {
                            BlockState peckState = this.getWorld().getBlockState(this.getClingPos());

                            this.setConsecutivePecks(this.getConsecutivePecks() + 1);
                            if (this.getConsecutivePecks() >= PECKS_NEEDED_FOR_NEST) {
                                this.stopPecking(true);

                                Block clingBlock = peckState.getBlock();
                                this.getWorld().setBlockState(this.getClingPos(), TREE_HOLLOW_MAP.get(clingBlock).getDefaultState().with(TreeHollowBlock.FACING, this.clingSide));
                            }

                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeBlockPos(this.getClingPos());
                            buf.writeEnumConstant(this.clingSide);
                            for (ServerPlayerEntity receiver : PlayerLookup.tracking(this)) ServerPlayNetworking.send(receiver, BlockBreakingParticlesPacket.ID, buf);

                            peckSound = peckState.getSoundGroup().getHitSound();
                        }
                        else {
                            BlockHitResult hitResult = (BlockHitResult) this.raycast(this.getPeckReach(), 0.0F, false);
                            BlockState peckState = this.getWorld().getBlockState(hitResult.getBlockPos());

                            BondingTask currentBondingTask = this.canBond() ? this.getCurrentBondingTask() : null;
                            boolean updateBondingTask = currentBondingTask != null && currentBondingTask.getStatus() == BondingTask.BondingTaskStatus.PLAYING;
                            boolean progressedBonding = false;
                            if (peckState.isIn(WondrousWildsTags.BlockTags.WOODPECKERS_INTERACT_WITH)) {
                                ItemStack heldItemStack = this.getMainHandStack();
                                WoodpeckerFakePlayer fakePlayer = new WoodpeckerFakePlayer(this);

                                boolean successfulInteraction;
                                boolean usedItem = false;

                                if (!(successfulInteraction = (peckState.onUse(this.getWorld(), fakePlayer, Hand.MAIN_HAND, hitResult)).isAccepted()))
                                    if (!heldItemStack.isEmpty() && (successfulInteraction = heldItemStack.useOnBlock(new WoodpeckerItemUsageContext(this, fakePlayer, heldItemStack, hitResult)).isAccepted())) usedItem = true;

                                if (successfulInteraction) {
                                    PacketByteBuf buf = PacketByteBufs.create();
                                    buf.writeVarInt(this.getId());
                                    buf.writeBlockHitResult(hitResult);
                                    buf.writeBoolean(usedItem);
                                    for (ServerPlayerEntity receiver : PlayerLookup.tracking(this)) ServerPlayNetworking.send(receiver, WoodpeckerInteractWithBlockPacket.ID, buf);

                                    this.setConsecutivePecks(this.getConsecutivePecks() + 1);

                                    if (updateBondingTask && (progressedBonding = currentBondingTask.tryProgressingWith(peckState, hitResult.getBlockPos(), heldItemStack.getItem())))
                                        this.showTameParticles(true);
                                }
                            }
                            if (updateBondingTask) {
                                if (!progressedBonding) currentBondingTask.reset();
                                this.refreshBondingTasks();
                            }

                            peckSound = peckState.getSoundGroup().getHitSound();
                        }

                        if (peckSound != null) this.playSound(peckSound, 0.75F, 1.5F);
                    }

                    --this.peckChainTicks;
                }
            }

            if (this.isClinging()) {
                boolean shouldInteract = this.canInteractWithPos(this.getClingPos());
                boolean canContinueClinging = this.canContinueClinging();

                if (!this.isPecking()) {
                    if (!this.isDrumming()) {
                        boolean canMakeNest = this.canMakeNests() && this.canMakeNestInPos(this.getClingPos());
                        if (shouldInteract || (canMakeNest && this.shouldFindNest())) {
                            if (this.getRandom().nextInt(shouldInteract ? 40 : 20) == 0 && canContinueClinging) {
                                int randomLength = 1 + this.getRandom().nextInt(4);
                                this.startPeckChain(canMakeNest ? Math.min(randomLength, PECKS_NEEDED_FOR_NEST - this.getConsecutivePecks()) : randomLength, this.getRandom().nextBetween(9, 11));
                            }
                        }
                        else if (config.mobSettings.woodpeckersDrum && this.hasNestPos() && this.getRandom().nextInt(config.mobSettings.woodpeckerDrumChance) == 0) this.startDrumming(false);
                    }
                }

                boolean naturallyUncling = !this.isDrumming() && !this.isMakingNest() && (this.getRandom().nextInt(shouldInteract ? 200 : 800) == 0 || this.shouldReturnToNest());
                if (naturallyUncling || !canContinueClinging) {
                    if (shouldInteract && naturallyUncling && !this.isTame() && !this.isBaby() && this.getConsecutivePecks() > 0) this.progressTame();
                    this.setFlying(true);
                }
                else this.setClingAngle(this.clingSide.getOpposite().getHorizontal() * 90);
            }

            if (!this.isDrumming()) {
                if (!this.isPecking()) {
                    if (this.nextChirpSpeed == (byte) 0) {
                        if (this.getRandom().nextInt(180) == 0) this.startChirping((byte) (1 + this.getRandom().nextInt(12)), (byte) (2 + this.getRandom().nextInt(3)));
                    }
                    else {
                        if (this.getChirpDelay() > 0) {
                            if (this.getChirpDelay() == 2) {
                                ++this.chirpCount;
                                this.playSound(WondrousWildsSounds.WOODPECKER_CHIRP, this.getSoundVolume(), this.nextChirpPitch);
                            }

                            this.setChirpDelay((byte) (this.getChirpDelay() - 1));
                        }
                        else {
                            this.setChirpDelay(this.nextChirpSpeed);

                            if (this.chirpCount >= this.nextChirpCount) this.stopChirping();
                        }
                    }
                }
            }
            else {
                int drummingTicks = this.getDrummingTicks();
                int nextDrummingTicks = drummingTicks - 1;
                if (!this.hasVehicle()) {
                    if (drummingTicks == 45) this.playSound(WondrousWildsSounds.WOODPECKER_DRUM, 4.0F, 1.0F);
                }
                else {
                    if (drummingTicks <= 31 && drummingTicks >= 1 && nextDrummingTicks % 2 == 0) {
                        Entity entityToDamage = this.getVehicle();
                        if (!entityToDamage.isAlive() || !this.getMainHandStack().isEmpty()) this.stopRiding();
                        else {
                            entityToDamage.timeUntilRegen = 0;
                            entityToDamage.damage(DamageSource.mob(this), (float) (this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) / 6.0F));
                        }
                    }
                    if (nextDrummingTicks <= 0) this.stopRiding();
                }

                this.setDrummingTicks(nextDrummingTicks);
            }

            this.tickAngerLogic((ServerWorld) this.getWorld(), false);
            if (this.drumAttackCooldown > 0) --this.drumAttackCooldown;

            if (this.getCannotInhabitNestTicks() > 0) this.setCannotInhabitNestTicks(this.getCannotInhabitNestTicks() - 1);

            if (this.isFailingToFly()) this.setFailingToFlyTicks(this.getFailingToFlyTicks() - 1);

            if (this.newBondingTaskCooldown <= 0) {
                if (this.canBond()) {
                    this.addNewBondingTask();
                    this.refreshBondingTasks();
                }
            }
            else --this.newBondingTaskCooldown;
        }

        if (this.startBondingTaskCooldown > 0) --this.startBondingTaskCooldown;

        if (this.isClinging()) {
            this.setYaw(this.getClingAngle());
            this.setBodyYaw(this.getYaw());

            boolean straightenHead = this.isPecking() || this.isDrumming();
            this.setHeadYaw(straightenHead ? this.getYaw() : MathHelper.clampAngle(this.getHeadYaw(), this.getYaw(), this.getMaxHeadRotation()));
            if (straightenHead) this.setPitch(0.0F);
        }
        else if (this.getClingAngle() != 0) this.setClingAngle(0);

        this.getCurrentNestTransition().ifPresent(NestTransition::tick);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack playerHeldStack = player.getStackInHand(hand);

        if (playerHeldStack.getItem() == WondrousWildsItems.LOVIFIER) {
            if (!this.isTame()) {
                if (!this.getWorld().isClient()) this.finishTame();
                return ActionResult.SUCCESS;
            }
            else if (this.getOwnerUuid() == null) {
                if (!this.getWorld().isClient()) {
                    this.setOwner((ServerPlayerEntity) player);
                    this.resetBonding(true);
                }
                return ActionResult.SUCCESS;
            }
        }

        ItemStack woodpeckerHeldStack = this.getStackInHand(Hand.MAIN_HAND);

        BondingTask currentBondingTask = this.getCurrentBondingTask();
        if (currentBondingTask != null && currentBondingTask.getStatus() == BondingTask.BondingTaskStatus.INACTIVE) {
            boolean hasNoBondingTarget = this.bondingTaskManager.targetUuid == null;
            if ((hasNoBondingTarget || player.getUuid().equals(this.bondingTaskManager.targetUuid)) && START_BONDING_TASK_PREDICATE.test(this)) {
                if (!this.getWorld().isClient()) {
                    if (this.bondingTaskManager.tryPreparingBondingTaskInArea(currentBondingTask, this.getBoundingBox().expand(10.0D, 5.0D, 10.0D), this)) {
                        if (hasNoBondingTarget) this.bondingTaskManager.targetUuid = player.getUuid();
                        this.refreshBondingTasks();
                        this.showTameParticles(true);
                    }
                    else this.showTameParticles(false);
                }
                this.startBondingTaskCooldown = 100;
                return ActionResult.SUCCESS;
            }
        }

        if (this.isTame() && (currentBondingTask == null || currentBondingTask.getStatus() != BondingTask.BondingTaskStatus.PLAYING)) {
            if (hand == Hand.MAIN_HAND && playerHeldStack.isEmpty() && !woodpeckerHeldStack.isEmpty()) {
                ItemStack stackToTransfer = woodpeckerHeldStack.copy();

                if (player.isSneaking()) {
                    stackToTransfer.setCount(1);
                    woodpeckerHeldStack.decrement(1);
                }
                else this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

                if (!player.giveItemStack(stackToTransfer)) this.dropStack(stackToTransfer);

                return ActionResult.SUCCESS;
            }

            transferToWoodpecker: if (!playerHeldStack.isEmpty()) {
                int woodpeckerSpaceRemaining = woodpeckerHeldStack.getMaxCount() - woodpeckerHeldStack.getCount();
                if (woodpeckerSpaceRemaining <= 0) break transferToWoodpecker;

                boolean isNewItemType = playerHeldStack.getItem() != woodpeckerHeldStack.getItem();
                if (isNewItemType && !woodpeckerHeldStack.isEmpty()) break transferToWoodpecker;

                ItemStack priorPlayerHeldStack = playerHeldStack.copy();

                if (!player.isSneaking()) {
                    if (!isNewItemType) {
                        int amountToTransfer = Math.min(playerHeldStack.getCount(), woodpeckerSpaceRemaining);

                        if (!player.getAbilities().creativeMode) playerHeldStack.decrement(amountToTransfer);
                        woodpeckerHeldStack.increment(amountToTransfer);

                    }
                    else {
                        if (!player.getAbilities().creativeMode) player.setStackInHand(hand, ItemStack.EMPTY);
                        this.setStackInHand(Hand.MAIN_HAND, playerHeldStack.copy());
                    }

                    if (player instanceof ServerPlayerEntity serverPlayer) WondrousWildsAdvancementCriteria.GAVE_WOODPECKER_ITEM.trigger(serverPlayer, priorPlayerHeldStack);
                    return ActionResult.SUCCESS;
                }

                ItemStack playerHeldStackCopy = playerHeldStack.copy();

                if (!player.getAbilities().creativeMode) playerHeldStack.decrement(1);

                if (isNewItemType) {
                    playerHeldStackCopy.setCount(1);
                    this.setStackInHand(Hand.MAIN_HAND, playerHeldStackCopy);
                }
                else woodpeckerHeldStack.increment(1);

                if (player instanceof ServerPlayerEntity serverPlayer) WondrousWildsAdvancementCriteria.GAVE_WOODPECKER_ITEM.trigger(serverPlayer, priorPlayerHeldStack);
                return ActionResult.SUCCESS;
            }

            if (player == this.getOwner()) {
                if (!this.getWorld().isClient()) {
                    boolean previouslyFollowing = this.isFollowing();
                    this.setIsFollowing(!previouslyFollowing);
                    player.sendMessage(Text.translatable(this.getType().getTranslationKey() + ".following_" + (previouslyFollowing ? "disabled" : "enabled"), this.getName()), true);
                }
                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        super.equipStack(slot, stack);

        this.updateDropChances(slot);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof WoodpeckerEntity) amount = 0.0F;

        if (super.damage(source, amount)) {
            this.resetConsecutivePecks();

            if (!this.getWorld().isClient() && !(!this.hasVehicle() && this.isDrumming()) && !this.isFlying() && this.isAbleToFly()) this.setFlying(true);

            return true;
        }
        else return false;
    }

    @Override
    public void updateKilledAdvancementCriterion(Entity entityKilled, int score, DamageSource damageSource) {
        super.updateKilledAdvancementCriterion(entityKilled, score, damageSource);

        if (!this.getWorld().isClient() && this.isTame() && this.getOwner() instanceof ServerPlayerEntity serverPlayer)
            WondrousWildsAdvancementCriteria.COMPANION_KILLED_ENTITY.trigger(serverPlayer, this, entityKilled, damageSource);
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isClinging()) return;

        if (this.isFailingToFly()) movementInput = Vec3d.ZERO;

        if (!this.isFlying()) {
            super.travel(movementInput);
            return;
        }

        if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.8D));
            }
            else if (this.isInLava()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5D));
            }
            else {
                this.updateVelocity(this.getMovementSpeed(), movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.91D));
            }
        }

        this.updateLimbs(this, false);
    }

    @Override
    public Vec3d getVelocity() {
        return this.isClinging() ? Vec3d.ZERO : super.getVelocity();
    }

    @Override
    public void setVelocity(Vec3d velocity) {
        if (!this.isClinging()) super.setVelocity(velocity);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (!this.isClinging()) super.pushAwayFrom(entity);
    }

    @Override
    protected void onSwimmingStart() {
        super.onSwimmingStart();

        if (!this.getWorld().isClient() && !this.isFlying()) this.setFlying(true);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public int getDespawnCounter() {
        return 0;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return this.isTame() && super.canBeLeashedBy(player);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.95F;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.isBaby() ? BABY_SIZE : super.getDimensions(pose);
    }

    @Override
    protected BodyControl createBodyControl() {
        return new WoodpeckerBodyControl(this);
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

    @Environment(value = EnvType.CLIENT)
    public int getBlinkTimer() {
        return this.blinkTimer;
    }

    @Environment(value = EnvType.CLIENT)
    public boolean shouldCloseEyes() {
        return this.getBlinkTimer() <= 1 || (this.isDrumming() && this.getDrummingTicks() <= (this.hasVehicle() ? 31 : 45));
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        AnimationController<WoodpeckerEntity> constantController = new AnimationController<>(this, "constantController", 2, this::constantAnimationPredicate);
        AnimationController<WoodpeckerEntity> overlapController = new AnimationController<>(this, "overlapController", 0, this::overlapAnimationPredicate);
        AnimationController<WoodpeckerEntity> animationController = new AnimationController<>(this, "animationController", 2, this::animationPredicate);

        animationData.addAnimationController(constantController);
        animationData.addAnimationController(overlapController);
        animationData.addAnimationController(animationController);
    }

    private <E extends IAnimatable> PlayState constantAnimationPredicate(AnimationEvent<E> event) {
        if (this.isFlying())
            event.getController().setAnimation(new AnimationBuilder().loop("FlyingConstant"));

        else if (this.isClinging())
            event.getController().setAnimation(new AnimationBuilder().loop("ClingingConstant"));

        else
            event.getController().setAnimation(new AnimationBuilder().loop("GroundedConstant"));

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState overlapAnimationPredicate(AnimationEvent<E> event) {
        double speed = 1.0D;

        if (this.isPecking()) {
            event.getController().setAnimation(new AnimationBuilder().playOnce(this.getPeckAnimationToPlay()));
            speed += (this.getPeckInterval() - DEFAULT_PECK_INTERVAL) / (double) -DEFAULT_PECK_INTERVAL;
        }

        else if (this.hasVehicle() && this.isDrumming())
            event.getController().setAnimation(new AnimationBuilder().playOnce("DrumAttackOverlap"));

        else if (this.getChirpDelay() > 0 && this.getChirpDelay() <= 2)
            event.getController().setAnimation(new AnimationBuilder().loop("ChirpOverlap"));

        else
            event.getController().setAnimation(new AnimationBuilder().loop("Empty"));

        event.getController().setAnimationSpeed(speed);

        return PlayState.CONTINUE;
    }

    private String getPeckAnimationToPlay() {
        return switch (this.getCurrentPeckChainLength()) {
            case 1 -> "Peck1Overlap";
            case 2 -> "Peck2Overlap";
            case 3 -> "Peck3Overlap";
            case 4 -> "Peck4Overlap";
            default -> "Empty";
        };
    }

    private <E extends IAnimatable> PlayState animationPredicate(AnimationEvent<E> event) {
        if (!this.shouldPreventMovementAnimations() && this.isFlying() && this.limbDistance >= 0.9F)
            event.getController().setAnimation(new AnimationBuilder().loop("Flap"));

        else if (this.isDrumming() && this.isClinging())
            event.getController().setAnimation(new AnimationBuilder().playOnce("Drum"));

        else if (this.getCurrentNestTransition().isPresent())
            event.getController().setAnimation(new AnimationBuilder().playOnce(this.getCurrentNestTransition().get().getType().animationName));

        else if (this.isFailingToFly())
            event.getController().setAnimation(new AnimationBuilder().playOnce("FailToFly"));

        else
            event.getController().setAnimation(new AnimationBuilder().loop("Empty"));

        return PlayState.CONTINUE;
    }

    @Environment(value = EnvType.CLIENT)
    public boolean shouldPreventMovementAnimations() {
        return this.isClinging() || this.getCurrentNestTransition().isPresent() || this.hasVehicle() || this.isFailingToFly();
    }

    public void showTameParticles(boolean positive) {
        DefaultParticleType particleEffect = ParticleTypes.SMOKE;
        if (positive) particleEffect = ParticleTypes.HEART;

        if (!this.getWorld().isClient() && this.getWorld() instanceof ServerWorld serverWorld)
            serverWorld.spawnParticles(particleEffect, this.getX(), this.getEyeY(), this.getZ(), 10, 0.25D, 0.25D, 0.25D, 0.0D);

        else for (int i = 0; i < 7; ++i) {
            double d = this.getRandom().nextGaussian() * 0.02D;
            double e = this.getRandom().nextGaussian() * 0.02D;
            double f = this.getRandom().nextGaussian() * 0.02D;

            this.getWorld().addParticle(particleEffect, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), d, e, f);
        }
    }

    private static class BondingTaskManager {
        public static final String TARGET_KEY = "Target";
        public static final String NEXT_BLOCK_AMOUNT_KEY = "NextBlockAmount";
        public static final String TASKS_KEY = "Tasks";
        public static final int INITIAL_TASK_STEPS = 1;

        @Nullable
        private UUID targetUuid;
        private int nextBlockAmount;
        private final List<BondingTask> tasks = new ArrayList<>();
        public final Set<Predicate<BlockState>> blockFilters = new HashSet<>();

        private BondingTaskManager() {
            this(getDefaultNbt());
        }

        private BondingTaskManager(NbtCompound compound) {
            if (compound.containsUuid(TARGET_KEY)) this.targetUuid = compound.getUuid(TARGET_KEY);

            this.nextBlockAmount = compound.getInt(NEXT_BLOCK_AMOUNT_KEY);

            NbtList tasksNbt = compound.getList(TASKS_KEY, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < tasksNbt.size(); ++i) this.tasks.add(BondingTask.fromNbt(tasksNbt.getCompound(i)));
        }

        public static NbtCompound getDefaultNbt() {
            NbtCompound compound = new NbtCompound();
            compound.putInt(NEXT_BLOCK_AMOUNT_KEY, INITIAL_TASK_STEPS);
            return compound;
        }

        private NbtCompound toNbt(boolean basic) {
            NbtCompound compound = new NbtCompound();

            if (this.targetUuid != null) compound.putUuid(TARGET_KEY, this.targetUuid);

            compound.putInt(NEXT_BLOCK_AMOUNT_KEY, this.nextBlockAmount);

            NbtList tasks = new NbtList();
            this.tasks.forEach(task -> tasks.add(task.toNbt(basic)));
            compound.put(TASKS_KEY, tasks);

            return compound;
        }

        private void reset() {
            this.targetUuid = null;
            this.nextBlockAmount = INITIAL_TASK_STEPS;
            this.tasks.clear();
            this.blockFilters.clear();
        }

        public boolean acceptsBlockAt(BlockPos pos, World world) {
            BlockState blockState = world.getBlockState(pos);
            if (!blockState.isIn(WondrousWildsTags.BlockTags.WOODPECKER_BONDING_BLOCKS)) return false;

            for (Predicate<BlockState> filter : this.blockFilters) if (filter.test(blockState)) return false;
            return true;
        }

        private boolean tryPreparingBondingTaskInArea(BondingTask bondingTask, Box box, LivingEntity entity) {
            List<BlockPos> suitableTargetPositions = new ArrayList<>(BlockPos.stream(box).filter(pos -> this.acceptsBlockAt(pos, entity.getWorld()) && WondrousWildsUtils.canEntitySeeBlock(entity, pos, false)).map(BlockPos::toImmutable).toList());
            if (suitableTargetPositions.size() < bondingTask.blocksRequired) return false;
            Collections.shuffle(suitableTargetPositions);

            for (int i = 0; i < bondingTask.blocksRequired; ++i) bondingTask.targetPositions.add(suitableTargetPositions.get(i));

            bondingTask.setStatus(BondingTask.BondingTaskStatus.PLAYING);
            return true;
        }
    }

    public static final class BondingTask {
        public static final String BLOCKS_REQUIRED_KEY = "BlocksRequired";
        public static final String STATUS_KEY = "Status";
        public static final String TARGET_POSITIONS_KEY = "TargetPositions";
        public static final String COMPLETION_STEPS_KEY = "CompletionSteps";

        private final int blocksRequired;
        private BondingTaskStatus status;
        private final Set<BlockPos> targetPositions;
        private final List<BondingTaskCompletionStep> completionSteps = new ArrayList<>();

        private BondingTask(int blocksRequired) {
            this(blocksRequired, BondingTaskStatus.INACTIVE, null, null);
        }

        private BondingTask(int blocksRequired, BondingTaskStatus status, @Nullable Collection<BlockPos> targetPositions, @Nullable Collection<BondingTaskCompletionStep> completionSteps) {
            this.blocksRequired = blocksRequired;
            this.status = status;
            this.targetPositions = new HashSet<>(blocksRequired);
            if (targetPositions != null) this.targetPositions.addAll(targetPositions);
            if (completionSteps != null) this.completionSteps.addAll(completionSteps);
        }

        private NbtCompound toNbt(boolean basic) {
            NbtCompound compound = new NbtCompound();

            compound.putInt(BLOCKS_REQUIRED_KEY, this.blocksRequired);

            if (!basic) {
                compound.putInt(STATUS_KEY, this.status.ordinal());

                if (!this.targetPositions.isEmpty()) {
                    NbtList positions = new NbtList();
                    this.targetPositions.forEach(pos -> positions.add(NbtHelper.fromBlockPos(pos)));
                    compound.put(TARGET_POSITIONS_KEY, positions);
                }

                if (!this.completionSteps.isEmpty()) {
                    NbtList steps = new NbtList();
                    this.completionSteps.forEach(step -> steps.add(step.toNbt()));
                    compound.put(COMPLETION_STEPS_KEY, steps);
                }
            }

            return compound;
        }

        private static BondingTask fromNbt(NbtCompound nbt) {
            int blocksRequired = nbt.getInt(BLOCKS_REQUIRED_KEY);

            BondingTaskStatus status = BondingTaskStatus.fromId(nbt.getInt(STATUS_KEY));

            Set<BlockPos> targetPositions = new HashSet<>();
            NbtList targetPositionsNbt = nbt.getList(TARGET_POSITIONS_KEY, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < targetPositionsNbt.size(); ++i) targetPositions.add(NbtHelper.toBlockPos(targetPositionsNbt.getCompound(i)));

            List<BondingTaskCompletionStep> completionSteps = new ArrayList<>();
            NbtList completionStepsNbt = nbt.getList(COMPLETION_STEPS_KEY, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < completionStepsNbt.size(); ++i) completionSteps.add(BondingTaskCompletionStep.fromNbt(completionStepsNbt.getCompound(i)));

            return new BondingTask(blocksRequired, status, targetPositions, completionSteps);
        }

        public BondingTaskStatus getStatus() {
            return this.status;
        }

        public void setStatus(BondingTaskStatus status) {
            this.status = status;
        }

        public Set<BlockPos> getTargetPositions() {
            return this.targetPositions;
        }

        private boolean tryProgressingWith(BlockState blockState, BlockPos pos, Item itemUsed) {
            if (!this.targetPositions.contains(pos)) return false;

            this.addCompletionStep(new BondingTaskCompletionStep(blockState.getBlock(), pos, itemUsed));
            return true;
        }

        public void reset() {
            this.status = BondingTaskStatus.INACTIVE;
            this.targetPositions.clear();
            this.completionSteps.clear();
        }

        public void addCompletionStep(BondingTaskCompletionStep step) {
            this.completionSteps.add(step);
        }

        @Nullable
        public BondingTaskCompletionStep getCompletionStep(int index) {
            try {
                return this.completionSteps.get(index);
            }
            catch (IndexOutOfBoundsException exception) {
                return null;
            }
        }

        public int getCompletionStepAmount() {
            return this.completionSteps.size();
        }

        public enum BondingTaskStatus {
            INACTIVE,
            PLAYING,
            WAITING;

            public static final List<BondingTaskStatus> VALUES = Arrays.stream(values()).toList();

            public static WoodpeckerEntity.BondingTask.BondingTaskStatus fromId(int id) {
                return VALUES.stream().filter(bondingTaskStatus -> bondingTaskStatus.ordinal() == id).findAny().orElse(INACTIVE);
            }
        }

        public static final class BondingTaskCompletionStep {
            public static final String BLOCK_KEY = "Block";
            public static final String POS_KEY = "Pos";
            public static final String ITEM_KEY = "Item";

            public final Block requiredBlock;
            public final BlockPos requiredPos;
            public final Item requiredItem;

            public BondingTaskCompletionStep(Block block, BlockPos pos, Item item) {
                this.requiredBlock = block;
                this.requiredPos = pos;
                this.requiredItem = item;
            }

            public NbtCompound toNbt() {
                NbtCompound compound = new NbtCompound();

                compound.putString(BLOCK_KEY, Registry.BLOCK.getId(this.requiredBlock).toString());

                compound.put(POS_KEY, NbtHelper.fromBlockPos(this.requiredPos));

                compound.putString(ITEM_KEY, Registry.ITEM.getId(this.requiredItem).toString());

                return compound;
            }

            public static BondingTaskCompletionStep fromNbt(NbtCompound nbt) {
                Block block = Registry.BLOCK.get(new Identifier(nbt.getString(BLOCK_KEY)));
                BlockPos pos = NbtHelper.toBlockPos(nbt.getCompound(POS_KEY));
                Item item = Registry.ITEM.get(new Identifier(nbt.getString(ITEM_KEY)));
                return new BondingTaskCompletionStep(block, pos, item);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this).add(BLOCK_KEY, this.requiredBlock).add(POS_KEY, this.requiredPos).add(ITEM_KEY, this.requiredItem).toString();
            }

            public boolean matches(Block block, BlockPos pos, Item item) {
                return this.requiredBlock.equals(block) && this.requiredPos.equals(pos) && this.requiredItem.equals(item);
            }
        }
    }
}
