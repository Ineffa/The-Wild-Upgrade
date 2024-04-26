package com.ineffa.wondrouswilds.mixin;

import com.ineffa.wondrouswilds.entities.BycocketUser;
import com.ineffa.wondrouswilds.entities.CanTakeSharpshots;
import com.ineffa.wondrouswilds.entities.projectiles.CanSharpshot;
import com.ineffa.wondrouswilds.items.BycocketItem;
import com.ineffa.wondrouswilds.registry.WondrousWildsEnchantments;
import com.ineffa.wondrouswilds.registry.WondrousWildsTags;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * @author Ineffa
 * <p> Processes Bycocket abilities through living entities by controlling when they should trigger, and processing some effects of them when they do.
 */
@Mixin(LivingEntity.class)
public abstract class MixinBycocketAbilityProcessor extends Entity implements BycocketUser, CanTakeSharpshots {

    private MixinBycocketAbilityProcessor(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Override
    public boolean wondrouswilds$isAccurateWith(ProjectileEntity projectile) {
        return false;
    }

    @Override
    public boolean wondrouswilds$canSharpshotWith(ProjectileEntity projectile) {
        ItemStack headItem = this.getEquippedStack(EquipmentSlot.HEAD);

        if (!(headItem.getItem() instanceof BycocketItem bycocketItem && bycocketItem.flair == BycocketItem.Flair.WOODPECKER)) return false;

        return EnchantmentHelper.getLevel(WondrousWildsEnchantments.VERSATILITY, headItem) > 0 || projectile.getType().isIn(WondrousWildsTags.EntityTypeTags.DEFAULT_BYCOCKET_PROJECTILES);
    }

    @Override
    public double wondrouswilds$getMaxVerticalDistanceForSharpshot() {
        return this.getHeight() / 15.0D;
    }

    @Override
    public boolean wondrouswilds$isValidHeightForSharpshot(double y) {
        double centerY = MathHelper.lerp(0.5D, this.getBoundingBox().minY, this.getBoundingBox().maxY);
        double maxDistanceFromCenterY = this.wondrouswilds$getMaxVerticalDistanceForSharpshot();
        return y >= centerY - maxDistanceFromCenterY && y <= centerY + maxDistanceFromCenterY;
    }

    /**
     * Applies a multiplier to the damage taken from a projectile when it is landing a sharpshot.
     */
    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float applySharpshotDamageBonus(float damage, DamageSource source, float amount) {
        if (source.getSource() instanceof CanSharpshot sharpshotProjectile && sharpshotProjectile.wondrouswilds$hasRegisteredSharpshot())
            damage *= 1.5F;

        return damage;
    }
}
