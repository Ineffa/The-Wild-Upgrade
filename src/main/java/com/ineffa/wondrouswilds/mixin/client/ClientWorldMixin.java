package com.ineffa.wondrouswilds.mixin.client;

import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageHolder;
import com.ineffa.wondrouswilds.util.blockdamage.BlockDamageInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements BlockDamageHolder {

    private ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Unique
    private final Map<Long, BlockDamageInstance> clientBlockDamageInstances = new HashMap<>();

    @Override
    public Map<Long, BlockDamageInstance> getBlockDamageInstanceMap() {
        return this.clientBlockDamageInstances;
    }

    @Inject(method = "setBlockBreakingInfo", at = @At("HEAD"))
    private void setBlockBreakingInfo(int entityId, BlockPos pos, int progress, CallbackInfo callback) {
        this.removeDamageAtPos(pos);
    }
}
