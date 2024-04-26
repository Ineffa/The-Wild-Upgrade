package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.CanBondWithWoodpecker;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Inject(method = "interactBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/advancement/criterion/Criteria;ITEM_USED_ON_BLOCK:Lnet/minecraft/advancement/criterion/ItemCriterion;", opcode = Opcodes.GETSTATIC, shift = At.Shift.AFTER))
    private void triggerWoodpeckerBondingComparison(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> callback) {
        if (player instanceof CanBondWithWoodpecker bondingPlayer && bondingPlayer.getComparingWoodpecker().isPresent())
            bondingPlayer.tryWoodpeckerBondingWith(world.getBlockState(hitResult.getBlockPos()).getBlock(), hitResult.getBlockPos(), player.getStackInHand(hand).getItem());
    }
}
