package com.ineffa.wondrouswilds.enchantments;

import net.minecraft.enchantment.Enchantment;

public class OverchargeEnchantment extends BycocketEnchantment {

    public OverchargeEnchantment() {
        super(Rarity.VERY_RARE);
    }

    @Override
    public int getMinPower(int level) {
        return 25;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        if (other instanceof DexterityEnchantment || other instanceof VersatilityEnchantment) return false;
        return super.canAccept(other);
    }
}
