package com.ineffa.wondrouswilds.mixin;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.items.BycocketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isSectionVisible(ILnet/minecraft/item/ItemStack$TooltipSection;)Z", ordinal = 3, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addBycocketTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> callback, List<Text> list) {
        if (this.getItem() instanceof BycocketItem bycocketItem) {
            list.add(ScreenTexts.EMPTY);
            list.add(Text.translatable("bycocket.flair").formatted(Formatting.GRAY));
            list.add(Text.translatable("bycocket.flair." + WondrousWilds.MOD_ID + "." + bycocketItem.flair.name + ".title").formatted(Formatting.GOLD));
        }
    }
}
