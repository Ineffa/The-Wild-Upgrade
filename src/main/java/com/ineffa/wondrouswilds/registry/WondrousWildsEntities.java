package com.ineffa.wondrouswilds.registry;

import com.google.common.collect.ImmutableMap;
import com.ineffa.wondrouswilds.WondrousWilds;
import com.ineffa.wondrouswilds.entities.BlockNester;
import com.ineffa.wondrouswilds.entities.ChipmunkEntity;
import com.ineffa.wondrouswilds.entities.FireflyEntity;
import com.ineffa.wondrouswilds.entities.WoodpeckerEntity;
import com.ineffa.wondrouswilds.entities.projectiles.BodkinArrowEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;

import java.util.Map;

public class WondrousWildsEntities {

    public static final EntityType<FireflyEntity> FIREFLY = Registry.register(Registry.ENTITY_TYPE, new Identifier(WondrousWilds.MOD_ID, "firefly"), FabricEntityTypeBuilder.createMob()
            .entityFactory(FireflyEntity::new)
            .defaultAttributes(FireflyEntity::createFireflyAttributes)
            .dimensions(EntityDimensions.fixed(0.1875F, 0.25F))
            .spawnGroup(WondrousWildsSpawnGroups.FIREFLIES)
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, FireflyEntity::canFireflySpawn)
            .build()
    );

    public static final EntityType<WoodpeckerEntity> WOODPECKER = Registry.register(Registry.ENTITY_TYPE, new Identifier(WondrousWilds.MOD_ID, "woodpecker"), FabricEntityTypeBuilder.createMob()
            .entityFactory(WoodpeckerEntity::new)
            .defaultAttributes(WoodpeckerEntity::createWoodpeckerAttributes)
            .dimensions(EntityDimensions.fixed(0.3125F, 0.5F))
            .spawnGroup(SpawnGroup.CREATURE)
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, AnimalEntity::isValidNaturalSpawn)
            .build()
    );

    public static final EntityType<ChipmunkEntity> CHIPMUNK = Registry.register(Registry.ENTITY_TYPE, new Identifier(WondrousWilds.MOD_ID, "chipmunk"), FabricEntityTypeBuilder.createMob()
            .entityFactory(ChipmunkEntity::new)
            .defaultAttributes(ChipmunkEntity::createChipmunkAttributes)
            .dimensions(EntityDimensions.fixed(0.25F, 0.34375F))
            .spawnGroup(SpawnGroup.CREATURE)
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, AnimalEntity::isValidNaturalSpawn)
            .build()
    );

    public static final EntityType<BodkinArrowEntity> BODKIN_ARROW = Registry.register(Registry.ENTITY_TYPE, new Identifier(WondrousWilds.MOD_ID, "bodkin_arrow"), FabricEntityTypeBuilder.<BodkinArrowEntity>create()
            .entityFactory(BodkinArrowEntity::new)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .trackRangeBlocks(4)
            .trackedUpdateRate(20)
            .build()
    );

    public static void initialize() {
        if (WondrousWilds.config.mobSettings.firefliesSpawnNaturally) BiomeModifications.addSpawn(context -> context.hasTag(ConventionalBiomeTags.IN_OVERWORLD), WondrousWildsSpawnGroups.FIREFLIES, FIREFLY, 100, 2, 3);
    }

    public static final Map<EntityType<? extends BlockNester>, Pair<Integer, Integer>> DEFAULT_NESTER_CAPACITY_WEIGHTS = new ImmutableMap.Builder<EntityType<? extends BlockNester>, Pair<Integer, Integer>>()
            .put(WOODPECKER, new Pair<>(55, 15))
            .build();

    public static int getDefaultNestCapacityWeightFor(EntityType<? extends BlockNester> entityType, boolean baby) {
        return baby ? DEFAULT_NESTER_CAPACITY_WEIGHTS.get(entityType).getRight() : DEFAULT_NESTER_CAPACITY_WEIGHTS.get(entityType).getLeft();
    }
}
