package com.ineffa.wondrouswilds.registry;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.entities.effects.WondrousWildsStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WondrousWildsStatusEffects {

    public static final StatusEffect RUSH = registerStatusEffect("rush", new WondrousWildsStatusEffect(StatusEffectCategory.BENEFICIAL, 16741749));
    public static final StatusEffect WOODPECKERS_WARCRY = registerStatusEffect("woodpeckers_warcry", new WondrousWildsStatusEffect(StatusEffectCategory.BENEFICIAL, 15744060));

    private static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.register(Registry.STATUS_EFFECT, new Identifier(WondrousWilds.MOD_ID, name), statusEffect);
    }

    public static void initialize() {}
}
