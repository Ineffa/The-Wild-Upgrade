package com.ineffa.wondrouswilds.enchantments;

import com.ineffa.wondrouswilds.items.BycocketItem;
import com.ineffa.wondrouswilds.registry.WondrousWildsTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class BycocketEnchantment extends Enchantment implements SimulatesCustomEnchantmentTarget {

    public BycocketEnchantment(Rarity rarity) {
        super(rarity, EnchantmentTarget.WEARABLE, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isIn(WondrousWildsTags.ItemTags.BYCOCKETS);
    }

    @Override
    public boolean isAcceptableItemInEnchantingTable(Item item) {
        return item instanceof BycocketItem;
    }
}
