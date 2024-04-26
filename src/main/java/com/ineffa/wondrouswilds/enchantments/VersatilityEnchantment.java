package com.ineffa.wondrouswilds.enchantments;

public class VersatilityEnchantment extends BycocketEnchantment {

    public VersatilityEnchantment() {
        super(Rarity.RARE);
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
