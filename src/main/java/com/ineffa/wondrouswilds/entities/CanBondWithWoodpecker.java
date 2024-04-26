package com.ineffa.wondrouswilds.entities;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface CanBondWithWoodpecker {

    Optional<WoodpeckerEntity> getComparingWoodpecker();

    void setComparingWoodpecker(@Nullable WoodpeckerEntity woodpecker);

    void tryWoodpeckerBondingWith(Block block, BlockPos pos, Item item);

    void stopComparingWithWoodpecker();
}
