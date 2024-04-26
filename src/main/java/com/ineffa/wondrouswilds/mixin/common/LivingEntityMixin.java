package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.entities.projectiles.BodkinArrowEntity;
import com.ineffa.wondrouswilds.items.BycocketItem;
import com.ineffa.wondrouswilds.registry.WondrousWildsEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract int getArmor();
    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);

    @Unique
    private boolean hitByWoodpeckerDrumming;

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void detectWoodpeckerDrummingDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, float f, boolean bl, float g, boolean bl2, Entity entity2, double d, double e) {
        if (entity2 instanceof WoodpeckerEntity && this.hasPassenger(entity2)) {
            this.hitByWoodpeckerDrumming = true;
            ((LivingEntity) (Object) this).knockbackVelocity = 0.0F;
        }
    }

    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"), index = 0)
    private double nullifyWoodpeckerDrummingKnockback(double strength) {
        if (this.hitByWoodpeckerDrumming) {
            this.hitByWoodpeckerDrumming = false;
            strength = 0.0D;
        }
        return strength;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V", ordinal = 0, shift = At.Shift.AFTER))
    private void bodkinArrowDisableShield(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player && source.getSource() instanceof BodkinArrowEntity bodkinArrow && bodkinArrow.getVelocity().length() >= bodkinArrow.getStrongVelocityThreshold()) {
            player.getItemCooldownManager().set(Items.SHIELD, 40);
            player.clearActiveItem();
            player.getWorld().sendEntityStatus(this, EntityStatuses.BREAK_SHIELD);
        }
    }

    @Redirect(method = "applyArmorToDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageArmor(Lnet/minecraft/entity/damage/DamageSource;F)V", ordinal = 0))
    private void bodkinArrowHeavilyDamageArmor(LivingEntity instance, DamageSource source, float amount) {
        float newAmount = amount;
        if (source.getSource() != null && source.getSource().getType() == WondrousWildsEntities.BODKIN_ARROW) newAmount *= 3.0F;
        instance.damageArmor(source, newAmount);
    }

    @Inject(method = "applyArmorToDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(FFF)F", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private void bodkinArrowBypassArmor(DamageSource source, float amount, CallbackInfoReturnable<Float> callback) {
        if (source.getSource() != null && source.getSource().getType() == WondrousWildsEntities.BODKIN_ARROW)
            callback.setReturnValue(DamageUtil.getDamageLeft(amount, this.getArmor() * 0.75F, (float) this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)));
    }

    @Inject(method = "getPreferredEquipmentSlot", at = @At("HEAD"), cancellable = true)
    private static void restrictBycocketEquipSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> callback) {
        if (stack.getItem() instanceof BycocketItem) callback.setReturnValue(EquipmentSlot.HEAD);
    }
}
