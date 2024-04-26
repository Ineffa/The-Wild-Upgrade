package com.ineffa.wondrouswilds.items.recipes;

import com.google.gson.JsonObject;
import com.ineffa.wondrouswilds.mixin.common.ShapedRecipeInvoker;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;

public class ShapedSecretRecipe extends ShapedRecipe implements SecretRecipe {

    public ShapedSecretRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {
        super(id, group, width, height, input, output);
    }

    public static class Serializer implements RecipeSerializer<ShapedSecretRecipe> {

        @Override
        public ShapedSecretRecipe read(Identifier id, JsonObject json) {
            String string = JsonHelper.getString(json, "group", "");

            Map<String, Ingredient> map = ShapedRecipeInvoker.readSymbols(JsonHelper.getObject(json, "key"));

            String[] strings = ShapedRecipeInvoker.removePadding(ShapedRecipeInvoker.getPattern(JsonHelper.getArray(json, "pattern")));

            int i = strings[0].length();
            int j = strings.length;

            DefaultedList<Ingredient> defaultedList = ShapedRecipeInvoker.createPatternMatrix(strings, map, i, j);

            ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));

            return new ShapedSecretRecipe(id, string, i, j, defaultedList, itemStack);
        }

        @Override
        public ShapedSecretRecipe read(Identifier id, PacketByteBuf buf) {
            int i = buf.readVarInt();
            int j = buf.readVarInt();
            String string = buf.readString();

            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.EMPTY);
            for (int k = 0; k < defaultedList.size(); ++k) defaultedList.set(k, Ingredient.fromPacket(buf));

            ItemStack itemStack = buf.readItemStack();

            return new ShapedSecretRecipe(id, string, i, j, defaultedList, itemStack);
        }

        @Override
        public void write(PacketByteBuf buf, ShapedSecretRecipe recipe) {
            buf.writeVarInt(recipe.getWidth());
            buf.writeVarInt(recipe.getHeight());
            buf.writeString(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) ingredient.write(buf);

            buf.writeItemStack(recipe.getOutput());
        }
    }
}
