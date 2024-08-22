package com.ineffa.wondrouswilds.registry;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.enchantments.BedazzlementEnchantment;
import com.ineffa.wondrouswilds.enchantments.DexterityEnchantment;
import com.ineffa.wondrouswilds.enchantments.OverchargeEnchantment;
import com.ineffa.wondrouswilds.enchantments.VersatilityEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WondrousWildsEnchantments {

    public static final Enchantment DEXTERITY = registerEnchantment("dexterity", new DexterityEnchantment());
    public static final Enchantment BEDAZZLEMENT = registerEnchantment("bedazzlement", new BedazzlementEnchantment());
    public static final Enchantment VERSATILITY = registerEnchantment("versatility", new VersatilityEnchantment());
    public static final Enchantment OVERCHARGE = registerEnchantment("overcharge", new OverchargeEnchantment());

    private static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(Registry.ENCHANTMENT, new Identifier(WondrousWilds.MOD_ID, name), enchantment);
    }

    public static void initialize() {}
}
