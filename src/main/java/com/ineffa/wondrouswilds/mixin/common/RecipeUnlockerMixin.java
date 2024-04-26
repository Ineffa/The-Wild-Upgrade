package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.items.recipes.SecretRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeUnlocker.class)
public interface RecipeUnlockerMixin {

    @Inject(at = @At("HEAD"), method = "shouldCraftRecipe", cancellable = true)
    private void restrictCraftingSecrets(World world, ServerPlayerEntity player, Recipe<?> recipe, CallbackInfoReturnable<Boolean> callback) {
        if (recipe instanceof SecretRecipe && !player.getRecipeBook().contains(recipe)) callback.setReturnValue(false);
    }
}
