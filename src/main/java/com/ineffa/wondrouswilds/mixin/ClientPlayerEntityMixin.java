package com.ineffa.wondrouswilds.mixin;

import com.ineffa.wondrouswilds.registry.WondrousWildsEnchantments;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;movementSideways:F", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void applyDexterityEnchantmentSideways(Input instance, float value) {
        instance.movementSideways *= 0.2F * (EnchantmentHelper.getLevel(WondrousWildsEnchantments.DEXTERITY, this.getEquippedStack(EquipmentSlot.HEAD)) + 1);
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;movementForward:F", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void applyDexterityEnchantmentForward(Input instance, float value) {
        instance.movementForward *= 0.2F * (EnchantmentHelper.getLevel(WondrousWildsEnchantments.DEXTERITY, this.getEquippedStack(EquipmentSlot.HEAD)) + 1);
    }
}
