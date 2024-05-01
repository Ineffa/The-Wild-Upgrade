package com.ineffa.wondrouswilds.enchantments;

import com.ineffa.wondrouswilds.entities.BycocketUser;
import com.ineffa.wondrouswilds.util.WondrousWildsUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;

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

    public static float getOverchargeProgress(LivingEntity user) {
        BycocketUser bycocketUser = ((BycocketUser) user);
        return Math.min(1.0F, WondrousWildsUtils.normalizeValue(user.getItemUseTime(), bycocketUser.wondrouswilds$getOverchargeStartDelay(), bycocketUser.wondrouswilds$getFullOverchargeThreshold()));
    }
}
