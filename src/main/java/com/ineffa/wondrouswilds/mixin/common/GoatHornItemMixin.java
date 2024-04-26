package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.registry.WondrousWildsAdvancementCriteria;
import com.ineffa.wondrouswilds.registry.WondrousWildsStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GoatHornItem.class)
public class GoatHornItemMixin {

    @Inject(method = "playSound", at = @At("HEAD"))
    private static void triggerWoodpeckersWarcry(World world, PlayerEntity player, Instrument instrument, CallbackInfo callback) {
        if (!world.isClient()) {
            List<WoodpeckerEntity> followingWoodpeckers = world.getEntitiesByClass(WoodpeckerEntity.class, player.getBoundingBox().expand(32.0D), woodpecker -> woodpecker.isFollowing() && woodpecker.getOwner() == player);
            if (!followingWoodpeckers.isEmpty()) {
                StatusEffectInstance existingPlayerEffect = player.getStatusEffect(WondrousWildsStatusEffects.WOODPECKERS_WARCRY);
                int duration = existingPlayerEffect != null ? existingPlayerEffect.getDuration() : 6000;

                if (existingPlayerEffect == null) player.addStatusEffect(new StatusEffectInstance(WondrousWildsStatusEffects.WOODPECKERS_WARCRY, duration, 0, false, false, true));

                for (WoodpeckerEntity woodpecker : followingWoodpeckers) {
                    final boolean alreadyHasHealthBoost = woodpecker.hasStatusEffect(StatusEffects.HEALTH_BOOST);
                    woodpecker.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, duration, 1, true, true));
                    if (!alreadyHasHealthBoost) woodpecker.setHealth(woodpecker.getMaxHealth());

                    woodpecker.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, duration, 1, true, true));
                }

                if (followingWoodpeckers.size() >= 10 && player instanceof ServerPlayerEntity serverPlayer) WondrousWildsAdvancementCriteria.CALLED_TEN_WOODPECKERS_INTO_BATTLE.trigger(serverPlayer);
            }
        }
    }
}
