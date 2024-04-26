package com.ineffa.wondrouswilds.enchantments;

public class DexterityEnchantment extends BycocketEnchantment {

    public DexterityEnchantment() {
        super(Rarity.COMMON);
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
