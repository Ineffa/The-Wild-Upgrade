package com.ineffa.wondrouswilds.util.blockdamage;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface BlockDamageHolder {

    Map<Long, BlockDamageInstance> wondrouswilds$getBlockDamageInstanceMap();

    @Nullable
    default BlockDamageInstance getDamageAtPos(BlockPos pos) {
        long posLong = pos.asLong();
        if (!this.wondrouswilds$getBlockDamageInstanceMap().containsKey(posLong)) return null;
        return this.wondrouswilds$getBlockDamageInstanceMap().get(posLong);
    }

    default void createOrOverwriteDamage(BlockDamageInstance damage) {
        this.wondrouswilds$getBlockDamageInstanceMap().put(damage.getPos().asLong(), damage);
    }

    default void removeDamageAtPos(BlockPos pos) {
        this.wondrouswilds$getBlockDamageInstanceMap().remove(pos.asLong());
    }
}
