package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.networking.packets.s2c.BlockDamagePacket;
import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageHolder;
import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageInstance;
import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageManager;
import com.ineffa.wondrouswilds.util.blockdamage.ServerBlockDamageInstance;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements BlockDamageHolder, BlockDamageManager {

    private ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Unique
    private final Map<Long, BlockDamageInstance> serverBlockDamageInstances = new HashMap<>();

    @Override
    public Map<Long, BlockDamageInstance> wondrouswilds$getBlockDamageInstanceMap() {
        return this.serverBlockDamageInstances;
    }

    @Override
    public boolean applyDamageToBlock(BlockPos pos, byte amount, @Nullable Entity damagingEntity) {
        if (amount == 0) return false;

        boolean blockBroken = false;

        byte updatedDamageStage = amount;
        ServerBlockDamageInstance existingDamage = (ServerBlockDamageInstance) this.getDamageAtPos(pos);
        boolean hasExistingDamage = existingDamage != null;
        if (hasExistingDamage) updatedDamageStage += existingDamage.getStage();

        if (updatedDamageStage < BlockDamageInstance.MINIMUM_STAGE || updatedDamageStage >= BlockDamageInstance.MAXIMUM_STAGE) {
            if (hasExistingDamage) this.removeDamageAtPos(pos);

            if (updatedDamageStage >= BlockDamageInstance.MAXIMUM_STAGE) {
                this.breakBlock(pos, true, damagingEntity);
                blockBroken = true;
            }
        }
        else {
            if (hasExistingDamage) existingDamage.setStage(updatedDamageStage);

            else this.createOrOverwriteDamage(new ServerBlockDamageInstance(pos, updatedDamageStage));
        }

        this.sendBlockDamageToClient(pos, updatedDamageStage);

        if (!blockBroken && amount > 0) this.playSound(null, pos, this.getBlockState(pos).getSoundGroup().getHitSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

        return blockBroken;
    }

    @Override
    public void sendBlockDamageToClient(BlockPos pos, byte damageStage) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeLong(pos.asLong());
        buf.writeByte(damageStage);
        for (ServerPlayerEntity receiver : PlayerLookup.tracking((ServerWorld) (Object) this, pos)) ServerPlayNetworking.send(receiver, BlockDamagePacket.ID, buf);
    }

    @Inject(method = "setBlockBreakingInfo", at = @At("HEAD"))
    private void setBlockBreakingInfo(int entityId, BlockPos pos, int progress, CallbackInfo callback) {
        this.removeDamageAtPos(pos);
    }

    // This additional injection is only required because ServerWorld lacks a super call for this method
    @Inject(method = "onBlockChanged", at = @At("HEAD"))
    private void onBlockChangeClearDamage(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo callback) {
        if (oldBlock.getBlock() != newBlock.getBlock()) this.removeDamageAtPos(pos);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V", shift = At.Shift.AFTER))
    private void tickBlockDamages(BooleanSupplier shouldKeepTicking, CallbackInfo callback) {
        List<BlockPos> positionsToTick = new ArrayList<>();
        this.wondrouswilds$getBlockDamageInstanceMap().values().forEach(blockDamageInstance -> positionsToTick.add(new BlockPos(blockDamageInstance.getPos())));

        positionsToTick.forEach(pos -> ((ServerBlockDamageInstance) this.wondrouswilds$getBlockDamageInstanceMap().get(pos.asLong())).tick(this));
    }
}
