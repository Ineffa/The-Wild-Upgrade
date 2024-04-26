package com.ineffa.wondrouswilds.advancement.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CompanionKilledEntityCriterion extends AbstractCriterion<CompanionKilledEntityCriterion.Conditions> {

    public static final String COMPANION_KEY = "companion";
    public static final String ENTITY_KILLED_KEY = "entity_killed";
    public static final String KILLING_BLOW_KEY = "killing_blow";

    private final Identifier id;

    public CompanionKilledEntityCriterion(Identifier id) {
        this.id = id;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new CompanionKilledEntityCriterion.Conditions(this.id, playerPredicate, EntityPredicate.Extended.getInJson(obj, COMPANION_KEY, predicateDeserializer), EntityPredicate.Extended.getInJson(obj, ENTITY_KILLED_KEY, predicateDeserializer), DamageSourcePredicate.fromJson(obj.get(KILLING_BLOW_KEY)));
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public void trigger(ServerPlayerEntity owner, Entity companion, Entity entityKilled, DamageSource killingBlow) {
        this.trigger(owner, conditions -> conditions.test(owner, EntityPredicate.createAdvancementEntityLootContext(owner, companion), EntityPredicate.createAdvancementEntityLootContext(owner, entityKilled), killingBlow));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final EntityPredicate.Extended companion, entityKilled;
        private final DamageSourcePredicate killingBlow;

        public Conditions(Identifier id, EntityPredicate.Extended owner, EntityPredicate.Extended companion, EntityPredicate.Extended entityKilled, DamageSourcePredicate killingBlow) {
            super(id, owner);
            this.companion = companion;
            this.entityKilled = entityKilled;
            this.killingBlow = killingBlow;
        }

        public boolean test(ServerPlayerEntity owner, LootContext companionContext, LootContext killedEntityContext, DamageSource killingBlow) {
            if (!this.killingBlow.test(owner, killingBlow)) return false;
            return this.companion.test(companionContext) && this.entityKilled.test(killedEntityContext);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add(COMPANION_KEY, this.companion.toJson(predicateSerializer));
            jsonObject.add(ENTITY_KILLED_KEY, this.entityKilled.toJson(predicateSerializer));
            jsonObject.add(KILLING_BLOW_KEY, this.killingBlow.toJson());
            return jsonObject;
        }
    }
}
