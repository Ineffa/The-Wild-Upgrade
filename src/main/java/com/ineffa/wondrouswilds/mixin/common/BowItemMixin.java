package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.enchantments.OverchargeEnchantment;
import com.ineffa.wondrouswilds.entities.BycocketUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public class BowItemMixin {

    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", shift = At.Shift.AFTER))
    private void applyBowCooldownFromOvercharge(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo callback) {
        BycocketUser bycocketUser = (BycocketUser) user;
        if (bycocketUser.wondrouswilds$isOvercharging())
            ((PlayerEntity) user).getItemCooldownManager().set(stack.getItem(), (int) (120 * OverchargeEnchantment.getOverchargeProgress(user)));
    }
}
