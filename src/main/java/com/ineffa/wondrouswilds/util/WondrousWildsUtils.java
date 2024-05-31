package com.ineffa.wondrouswilds.util;

import com.google.common.collect.ImmutableMap;
import com.ineffa.wondrouswilds.blocks.TreeHollowBlock;
import com.ineffa.wondrouswilds.registry.WondrousWildsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.*;

public class WondrousWildsUtils {

    public static final Direction[] HORIZONTAL_DIRECTIONS = Arrays.stream(Direction.values()).filter((direction) -> direction.getAxis().isHorizontal()).toArray(Direction[]::new);
    public static final Direction[] VERTICAL_DIRECTIONS = Arrays.stream(Direction.values()).filter((direction) -> direction.getAxis().isVertical()).toArray(Direction[]::new);

    public static final Map<Block, TreeHollowBlock> TREE_HOLLOW_MAP = new ImmutableMap.Builder<Block, TreeHollowBlock>()
            .put(Blocks.OAK_LOG, WondrousWildsBlocks.OAK_TREE_HOLLOW)
            .put(Blocks.SPRUCE_LOG, WondrousWildsBlocks.SPRUCE_TREE_HOLLOW)
            .put(Blocks.BIRCH_LOG, WondrousWildsBlocks.BIRCH_TREE_HOLLOW)
            .put(Blocks.JUNGLE_LOG, WondrousWildsBlocks.JUNGLE_TREE_HOLLOW)
            .put(Blocks.ACACIA_LOG, WondrousWildsBlocks.ACACIA_TREE_HOLLOW)
            .put(Blocks.DARK_OAK_LOG, WondrousWildsBlocks.DARK_OAK_TREE_HOLLOW)
            .put(Blocks.MANGROVE_LOG, WondrousWildsBlocks.MANGROVE_TREE_HOLLOW)
            .build();

    public static List<BlockPos> getCenteredCuboid(BlockPos center, int horizontalRadius) {
        return getCenteredCuboid(center, horizontalRadius, 0);
    }

    public static List<BlockPos> getCenteredCuboid(BlockPos center, int horizontalRadius, int verticalRadius) {
        List<BlockPos> positions = new ArrayList<>();

        for (int y = -verticalRadius; y <= verticalRadius; ++y) {
            for (int x = -horizontalRadius; x <= horizontalRadius; ++x) {
                for (int z = -horizontalRadius; z <= horizontalRadius; ++z) {
                    BlockPos pos = center.add(x, y, z);
                    positions.add(pos);
                }
            }
        }

        return positions;
    }

    public static List<BlockPos> getEdges(BlockPos center, int edgeDistance, int edgeRadius) {
        List<BlockPos> positions = new ArrayList<>();

        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos offsetPos = center.offset(direction, edgeDistance);

            positions.add(offsetPos);

            if (edgeRadius < 1) continue;

            for (int distance = 1; distance <= edgeRadius; ++distance) {
                positions.add(offsetPos.offset(direction.rotateYClockwise(), distance));
                positions.add(offsetPos.offset(direction.rotateYCounterclockwise(), distance));
            }
        }

        return positions;
    }

    public static boolean isPosAdjacentToAnyOfPositions(BlockPos pos, Collection<BlockPos> positions) {
        for (Direction direction : Direction.values())
            if (positions.stream().anyMatch(pos1 -> pos1.equals(pos.offset(direction)))) return true;

        return false;
    }

    public static boolean isPosAtWorldOrigin(BlockPos pos) {
        BlockPos origin = BlockPos.ORIGIN;
        return pos.getX() == origin.getX() && pos.getY() == origin.getY() && pos.getZ() == origin.getZ();
    }

    public static boolean canEntitySeeBlock(LivingEntity entity, BlockPos posToCheck, boolean ignoreFluids) {
        World world = entity.getWorld();

        VoxelShape shape = world.getBlockState(posToCheck).getOutlineShape(world, posToCheck);
        if (shape == null || shape.isEmpty()) return false;

        Vec3d raycastStart = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
        Vec3d raycastEnd = shape.getBoundingBox().getCenter().add(posToCheck.getX(), posToCheck.getY(), posToCheck.getZ());
        return world.raycast(new RaycastContext(raycastStart, raycastEnd, RaycastContext.ShapeType.COLLIDER, ignoreFluids ? RaycastContext.FluidHandling.NONE : RaycastContext.FluidHandling.ANY, entity)).getBlockPos().equals(posToCheck);
    }

    public static double stepTowards(double from, double to, double step) {
        return (from < to) ? MathHelper.clamp(from + step, from, to) : MathHelper.clamp(from - step, to, from);
    }

    public static void faceEntityYawTowards(Entity entity, Vec3d pos) {
        double deltaX = pos.getX() - entity.getX();
        double deltaZ = pos.getZ() - entity.getZ();
        double yaw = Math.atan2(deltaZ, deltaX) * (180.0D / Math.PI) - 90.0D;
        entity.setYaw((float) yaw);
    }

    public static Vec3d getCenterOfBlockShape(World world, BlockPos pos) {
        VoxelShape blockShape = world.getBlockState(pos).getOutlineShape(world, pos);
        if (!blockShape.isEmpty()) return blockShape.getBoundingBox().getCenter().add(pos.getX(), pos.getY(), pos.getZ());

        return Vec3d.ofCenter(pos);
    }

    public static double getMountedOffsetForStandingOn(Entity vehicle) {
        double offset;

        EntityType<?> vehicleType = vehicle.getType();
        if (vehicleType == EntityType.PLAYER || vehicleType == EntityType.ZOMBIE || vehicleType == EntityType.DROWNED || vehicleType == EntityType.SKELETON || vehicleType == EntityType.STRAY || vehicleType == EntityType.VILLAGER || vehicleType == EntityType.WANDERING_TRADER || vehicleType == EntityType.PILLAGER || vehicleType == EntityType.VINDICATOR || vehicleType == EntityType.EVOKER || vehicleType == EntityType.ILLUSIONER) offset = 0.5D;
        else if (vehicleType == EntityType.CREEPER || vehicleType == EntityType.SPIDER || vehicleType == EntityType.COW || vehicleType == EntityType.CHICKEN) offset = 0.3D;
        else if (vehicleType == EntityType.ZOMBIE_VILLAGER || vehicleType == EntityType.HUSK || vehicleType == EntityType.ENDERMAN) offset = 0.6D;
        else if (vehicleType == EntityType.SHEEP || vehicleType == EntityType.PIG) offset = 0.2D;
        else if (vehicleType == EntityType.WITCH) offset = 1.0D;
        else if (vehicleType == EntityType.IRON_GOLEM) offset = 0.65D;
        else if (vehicleType == EntityType.SNOW_GOLEM) {
            offset = 0.45D;

            if (!((SnowGolemEntity) vehicle).hasPumpkin()) offset -= 0.175D;
        }
        else if (vehicleType == EntityType.ARMOR_STAND) {
            offset = 0.4D;

            if (!((ArmorStandEntity) vehicle).getEquippedStack(EquipmentSlot.HEAD).isEmpty()) offset += 0.1D;
        }
        else if (vehicleType == EntityType.ALLAY) offset = 0.1D;
        else offset = vehicle.getHeight() - vehicle.getMountedHeightOffset();
        if (vehicle.isSneaking()) offset -= 0.15D;

        return offset;
    }

    public static Direction getRandomHorizontalDirection(Random random) {
        return HORIZONTAL_DIRECTIONS[random.nextInt(HORIZONTAL_DIRECTIONS.length)];
    }

    public static float normalizeValue(float value, float min, float max) {
        return (value - min) / (max - min);
    }
}
