package com.ineffa.wondrouswilds.enchantments;

public class BedazzlementEnchantment extends BycocketEnchantment {

    public BedazzlementEnchantment() {
        super(Rarity.UNCOMMON);
    }

    @Override
    public int getMinPower(int level) {
        return 12 + (level - 1) * 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
