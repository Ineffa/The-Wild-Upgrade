package com.ineffa.wondrouswilds.advancement.criteria;

import com.google.gson.JsonObject;
import com.ineffa.wondrouswilds.WondrousWilds;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class GaveWoodpeckerItemCriterion extends AbstractCriterion<GaveWoodpeckerItemCriterion.Conditions> {

    private static final Identifier ID = new Identifier(WondrousWilds.MOD_ID, "gave_woodpecker_item");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate, ItemPredicate.fromJson(obj.get("item")));
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, conditions -> conditions.matches(stack));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended player, ItemPredicate item) {
            super(ID, player);
            this.item = item;
        }

        public boolean matches(ItemStack stack) {
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}
