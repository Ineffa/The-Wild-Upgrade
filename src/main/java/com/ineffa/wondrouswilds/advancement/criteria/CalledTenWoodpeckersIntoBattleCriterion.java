package com.ineffa.wondrouswilds.advancement.criteria;

import com.google.gson.JsonObject;
import com.ineffa.wondrouswilds.WondrousWilds;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CalledTenWoodpeckersIntoBattleCriterion extends AbstractCriterion<CalledTenWoodpeckersIntoBattleCriterion.Conditions> {

    private static final Identifier ID = new Identifier(WondrousWilds.MOD_ID, "called_ten_woodpeckers_into_battle");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended predicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(predicate);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions(EntityPredicate.Extended predicate) {
            super(ID, predicate);
        }
    }
}
