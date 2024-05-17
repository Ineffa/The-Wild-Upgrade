package com.ineffa.wondrouswilds.entities.projectiles;

import com.ineffa.wondrouswilds.networking.packets.s2c.BlockBreakingParticlesPacket;
import com.ineffa.wondrouswilds.registry.WondrousWildsEntities;
import com.ineffa.wondrouswilds.registry.WondrousWildsItems;
import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BodkinArrowEntity extends PersistentProjectileEntity implements BlockBreakingProjectile, HasCustomGravity {

    public static final double BASE_DAMAGE = 1.75D;

    public BodkinArrowEntity(EntityType<? extends BodkinArrowEntity> entityType, World world) {
        super(entityType, world);
        this.setDamage(BASE_DAMAGE);
    }

    public BodkinArrowEntity(World world, LivingEntity owner) {
        super(WondrousWildsEntities.BODKIN_ARROW, owner, world);
        this.setDamage(BASE_DAMAGE);
    }

    public BodkinArrowEntity(World world, double x, double y, double z) {
        super(WondrousWildsEntities.BODKIN_ARROW, x, y, z, world);
        this.setDamage(BASE_DAMAGE);
    }

    @Override
    public double getStrongVelocityThreshold() {
        return 1.0D;
    }

    @Nullable
    @Override
    public ProjectileBlockDamageType testBlockDamageCapability(World world, BlockPos pos) {
        if (!this.canModifyAt(world, pos)) return null;

        ProjectileBlockDamageType damageType = ProjectileBlockDamageType.getTypeForHardness(world.getBlockState(pos).getBlock().getHardness());
        if (damageType == null || damageType == ProjectileBlockDamageType.PIERCE) return damageType;

        return this.getVelocity().length() >= this.getStrongVelocityThreshold() ? damageType : null;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockPos hitPos = blockHitResult.getBlockPos();
        ProjectileBlockDamageType damageType = null;
        if (!this.getWorld().isClient()) damageType = this.testBlockDamageCapability(this.getWorld(), hitPos);

        super.onBlockHit(blockHitResult);

        if (damageType != null && this.getWorld() instanceof BlockDamageManager blockDamageManager) {
            blockDamageManager.applyDamageToBlock(hitPos, damageType.getDamage(), this.getOwner());

            if (damageType != ProjectileBlockDamageType.PIERCE) {
                this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.25F, 1.4F + (this.random.nextFloat() * 0.4F));

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(hitPos);
                buf.writeEnumConstant(blockHitResult.getSide());
                for (ServerPlayerEntity receiver : PlayerLookup.tracking(this)) ServerPlayNetworking.send(receiver, BlockBreakingParticlesPacket.ID, buf);

                this.discard();
            }
        }
    }

    @Override
    public double getGravity() {
        return 0.075D;
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(WondrousWildsItems.BODKIN_ARROW);
    }

    public static ProjectileDispenserBehavior getDispenserBehavior() {
        return new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                BodkinArrowEntity bodkinArrow = new BodkinArrowEntity(world, position.getX(), position.getY(), position.getZ());
                bodkinArrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                return bodkinArrow;
            }
        };
    }
}
