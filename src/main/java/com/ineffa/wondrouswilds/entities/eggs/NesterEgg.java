package com.ineffa.wondrouswilds.entities.eggs;

import com.ineffa.wondrouswilds.entities.BlockNester;
import com.ineffa.wondrouswilds.registry.WondrousWildsEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NesterEgg {

    /**
     * <p> The amount of space the egg will take up inside its nest.
     * <p> Works identically to inhabitants, and should always match its baby equivalent's value.
     */
    public final int capacityWeight;

    public static final int CRACKS_NEEDED_TO_HATCH = 3;

    /**
     * What type of entity is inside the egg.
     */
    private final EntityType<? extends BlockNester> entityTypeToHatch;
    /**
     * Optional NBT data to be applied to the baby when it hatches.
     */
    @Nullable
    private final NbtCompound dataToInherit;

    /**
     * Controls whether the egg will crack and hatch during nighttime.
     */
    private final boolean nocturnal;

    /**
     * Contains the minimum and maximum value, respectively, that {@code crackCooldown} will be randomized between on each reroll.
     */
    public final Pair<Integer, Integer> crackCooldownRange;
    /**
     * Cooldown before the egg can crack again in ticks.
     */
    private int crackCooldown;
    private int cracksUntilHatch;

    /**
     * Creates a brand-new egg, like when a nester lays one in its nest.
     */
    public NesterEgg(EntityType<? extends BlockNester> entityTypeToHatch, @Nullable NbtCompound dataToInherit, boolean nocturnal, Pair<Integer, Integer> crackCooldownRange, Random random) {
        this(WondrousWildsEntities.getDefaultNestCapacityWeightFor(entityTypeToHatch, true), entityTypeToHatch, dataToInherit, nocturnal, crackCooldownRange, 0, CRACKS_NEEDED_TO_HATCH);

        this.rerollCrackCooldown(random);
    }

    /**
     * <p> Constructs an egg with fully specified data.
     * <p> Normally used to load an existing one from NBT.
     */
    public NesterEgg(int capacityWeight, EntityType<? extends BlockNester> entityTypeToHatch, @Nullable NbtCompound dataToInherit, boolean nocturnal, Pair<Integer, Integer> crackCooldownRange, int crackCooldown, int cracksUntilHatch) {
        this.capacityWeight = capacityWeight;

        this.entityTypeToHatch = entityTypeToHatch;
        this.dataToInherit = dataToInherit;
        this.nocturnal = nocturnal;

        this.crackCooldownRange = crackCooldownRange;
        this.crackCooldown = crackCooldown;
        this.cracksUntilHatch = cracksUntilHatch;
    }

    public EntityType<? extends BlockNester> getEntityTypeToHatch() {
        return this.entityTypeToHatch;
    }

    @Nullable
    public NbtCompound getDataToInherit() {
        return this.dataToInherit;
    }

    public boolean isNocturnal() {
        return this.nocturnal;
    }

    private boolean isDesiredTime(World world) {
        return this.isNocturnal() ? world.isNight() : world.isDay();
    }

    @Nullable
    public EggCrackResult tryCracking(World world) {
        if (this.isOnCooldown()) {
            --this.crackCooldown;
            return null;
        }

        return this.isDesiredTime(world) && world.getRandom().nextInt(4000) == 0 ? this.crack(world.getRandom()) : null;
    }

    public EggCrackResult crack(Random random) {
        this.rerollCrackCooldown(random);

        --this.cracksUntilHatch;
        return this.cracksUntilHatch <= 0 ? EggCrackResult.HATCH : EggCrackResult.CRACK;
    }

    public boolean isOnCooldown() {
        return this.crackCooldown > 0;
    }

    public void rerollCrackCooldown(Random random) {
        this.crackCooldown = random.nextBetween(this.crackCooldownRange.getLeft(), this.crackCooldownRange.getRight());
    }

    public int getCrackCooldown() {
        return this.crackCooldown;
    }

    public int getCracksUntilHatch() {
        return this.cracksUntilHatch;
    }

    public enum EggCrackResult {
        CRACK,
        HATCH
    }
}
