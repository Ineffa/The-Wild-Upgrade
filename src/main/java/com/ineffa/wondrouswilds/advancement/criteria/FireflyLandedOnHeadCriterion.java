package com.ineffa.wondrouswilds.advancement.criteria;

import com.google.gson.JsonObject;
import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.entities.FireflyEntity;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FireflyLandedOnHeadCriterion extends AbstractCriterion<FireflyLandedOnHeadCriterion.Conditions> {

    private static final Identifier ID = new Identifier(WondrousWilds.MOD_ID, "firefly_landed_on_head");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        EntityPredicate.Extended fireflyPredicate = EntityPredicate.Extended.getInJson(obj, "firefly", predicateDeserializer);
        return new Conditions(playerPredicate, fireflyPredicate);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, FireflyEntity firefly) {
        this.trigger(player, conditions -> conditions.matches(EntityPredicate.createAdvancementEntityLootContext(player, firefly)));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final EntityPredicate.Extended fireflyPredicate;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended fireflyPredicate) {
            super(ID, player);
            this.fireflyPredicate = fireflyPredicate;
        }

        public boolean matches(LootContext context) {
            return this.fireflyPredicate.test(context);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("firefly", this.fireflyPredicate.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}
