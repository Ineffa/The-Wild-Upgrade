package com.ineffa.wondrouswilds.mixin;

import com.ineffa.wondrouswilds.enchantments.SimulatesCustomEnchantmentTarget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Unique
    private static Enchantment enchantmentBeingChecked;

    @Inject(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isTreasure()Z", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void getEnchantmentBeingChecked(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir, List list, Item item, boolean bl, Iterator var6, Enchantment enchantment) {
        enchantmentBeingChecked = enchantment;
    }

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean simulateCustomEnchantmentTargets(EnchantmentTarget instance, Item item) {
        return enchantmentBeingChecked instanceof SimulatesCustomEnchantmentTarget customEnchantment ? customEnchantment.isAcceptableItemInEnchantingTable(item) : instance.isAcceptableItem(item);
    }
}
