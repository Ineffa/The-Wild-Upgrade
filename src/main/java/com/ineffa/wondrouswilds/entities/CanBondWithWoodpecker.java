package com.ineffa.wondrouswilds.entities;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface CanBondWithWoodpecker {

    Optional<WoodpeckerEntity> wondrouswilds$getComparingWoodpecker();

    void wondrouswilds$setComparingWoodpecker(@Nullable WoodpeckerEntity woodpecker);

    void wondrouswilds$tryWoodpeckerBondingWith(Block block, BlockPos pos, Item item);

    void wondrouswilds$stopComparingWithWoodpecker();
}
