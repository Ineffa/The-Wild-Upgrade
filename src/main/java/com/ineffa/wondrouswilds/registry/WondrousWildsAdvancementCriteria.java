package com.ineffa.wondrouswilds.registry;

import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.advancement.criteria.*;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.util.Identifier;

public class WondrousWildsAdvancementCriteria {

    public static final FireflyLandedOnHeadCriterion FIREFLY_LANDED_ON_HEAD = Criteria.register(new FireflyLandedOnHeadCriterion());
    public static final GaveWoodpeckerItemCriterion GAVE_WOODPECKER_ITEM = Criteria.register(new GaveWoodpeckerItemCriterion());
    public static final CalledTenWoodpeckersIntoBattleCriterion CALLED_TEN_WOODPECKERS_INTO_BATTLE = Criteria.register(new CalledTenWoodpeckersIntoBattleCriterion());
    public static final CompanionKilledEntityCriterion COMPANION_KILLED_ENTITY = Criteria.register(new CompanionKilledEntityCriterion(createId("companion_killed_entity")));
    public static final OnKilledCriterion KILLED_WITH_SHARPSHOT = Criteria.register(new OnKilledCriterion(createId("killed_with_sharpshot")));

    public static void initialize() {}

    private static Identifier createId(String name) {
        return new Identifier(WondrousWilds.MOD_ID, name);
    }
}
