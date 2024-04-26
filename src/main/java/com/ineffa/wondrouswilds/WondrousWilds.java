package com.ineffa.wondrouswilds;

import com.ineffa.wondrouswilds.client.rendering.WondrousWildsColorProviders;
import com.ineffa.wondrouswilds.config.WondrousWildsConfig;
import com.ineffa.wondrouswilds.entities.ChipmunkEntity;
import com.ineffa.wondrouswilds.mixin.common.MobEntityAccessor;
import com.ineffa.wondrouswilds.registry.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

import java.util.function.Predicate;

public class WondrousWilds implements ModInitializer {

	public static final String MOD_ID = "wondrouswilds";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static WondrousWildsConfig config;

	@Override
	public void onInitialize() {
		LOGGER.info("Wondrous Wilds initializing!");

		// Initialize GeckoLib
		GeckoLibMod.DISABLE_IN_DEV = true;
		GeckoLib.initialize();

		// Initialize config
		LOGGER.info("Initializing Wondrous Wilds config");

		AutoConfig.register(WondrousWildsConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(WondrousWildsConfig.class).getConfig();

		// Initialize mod
		LOGGER.info("Initializing Wondrous Wilds content");

		WondrousWildsSounds.initialize();
		WondrousWildsParticles.initialize();

		WondrousWildsEntities.initialize();
		WondrousWildsStatusEffects.initialize();
		WondrousWildsBlocks.initialize();
		WondrousWildsItems.initialize();
		WondrousWildsEnchantments.initialize();

		WondrousWildsFeatures.initialize();

		WondrousWildsScreenHandlers.initialize();

		WondrousWildsAdvancementCriteria.initialize();

		upgradeBirchForests();
		upgradeForest();

		ServerEntityEvents.ENTITY_LOAD.register(WondrousWilds::hookEntityCreation);

		LOGGER.info("Wondrous Wilds initialized and ready!");
	}

	private static void upgradeBirchForests() {
		BiomeModification birchForestModifier = BiomeModifications.create(new Identifier(MOD_ID, "birch_forest_modifier"));

		final Predicate<BiomeSelectionContext> BIRCH_FOREST = BiomeSelectors.includeByKey(BiomeKeys.BIRCH_FOREST);
		final Predicate<BiomeSelectionContext> OLD_GROWTH_BIRCH_FOREST = BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
		final Predicate<BiomeSelectionContext> ALL_BIRCH_FORESTS = BiomeSelectors.includeByKey(BiomeKeys.BIRCH_FOREST, BiomeKeys.OLD_GROWTH_BIRCH_FOREST);

		// All Birch Forests
		birchForestModifier.add(ModificationPhase.REPLACEMENTS, ALL_BIRCH_FORESTS, context -> {
			context.getGenerationSettings().removeBuiltInFeature(VegetationPlacedFeatures.PATCH_GRASS_FOREST.value());
			context.getGenerationSettings().removeBuiltInFeature(VegetationPlacedFeatures.FOREST_FLOWERS.value());

			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.BIRCH_FOREST_GRASS_PLACED.getKey().orElseThrow());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.BIRCH_FOREST_TALL_FLOWERS_PLACED.getKey().orElseThrow());

			context.getEffects().setMusic(MusicType.createIngameMusic(WondrousWildsSounds.MUSIC_OVERWORLD_BIRCH_FOREST));
		});

		birchForestModifier.add(ModificationPhase.ADDITIONS, ALL_BIRCH_FORESTS, context -> {
			context.getGenerationSettings().addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, WondrousWildsFeatures.BIRCH_FOREST_BOULDER_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, WondrousWildsFeatures.BIRCH_FOREST_MEDIUM_COARSE_DIRT_SPLOTCH_PLACED.getKey().orElseThrow());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, WondrousWildsFeatures.BIRCH_FOREST_LARGE_COARSE_DIRT_SPLOTCH_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.BIRCH_FOREST_FALLEN_LOG_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.BIRCH_FOREST_TALL_GRASS_PLACED.getKey().orElseThrow());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.BIRCH_FOREST_BUSHES_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.PURPLE_VIOLETS_PLACED.getKey().orElseThrow());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.PINK_VIOLETS_PLACED.getKey().orElseThrow());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.RED_VIOLETS_PLACED.getKey().orElseThrow());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.WHITE_VIOLETS_PLACED.getKey().orElseThrow());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.LILY_OF_THE_VALLEY_PATCH_PLACED.getKey().orElseThrow());

			context.getSpawnSettings().addSpawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 6, 2, 4));
		});

		// Birch Forest
		birchForestModifier.add(ModificationPhase.REPLACEMENTS, BIRCH_FOREST, context -> {
			context.getGenerationSettings().removeBuiltInFeature(VegetationPlacedFeatures.TREES_BIRCH.value());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.Trees.BIRCH_FOREST_TREES_PLACED.getKey().orElseThrow());
		});

		// Old Growth Birch Forest
		birchForestModifier.add(ModificationPhase.REPLACEMENTS, OLD_GROWTH_BIRCH_FOREST, context -> {
			context.getGenerationSettings().removeBuiltInFeature(VegetationPlacedFeatures.BIRCH_TALL.value());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.Trees.OLD_GROWTH_BIRCH_FOREST_TREES_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().removeBuiltInFeature(VegetationPlacedFeatures.PATCH_PUMPKIN.value());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.AUTUMN_PUMPKIN_PATCH.getKey().orElseThrow());

			context.getWeather().setTemperature(0.3F);
			context.getEffects().setGrassColor(WondrousWildsColorProviders.getOldGrowthBirchForestGrassColor());
		});

		birchForestModifier.add(ModificationPhase.ADDITIONS, OLD_GROWTH_BIRCH_FOREST, context -> {
			context.getGenerationSettings().addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, WondrousWildsFeatures.COVER_SURFACE_WITH_FALLEN_BIRCH_LEAVES_PLACED.getKey().orElseThrow());
		});
	}

	private static void upgradeForest() {
		BiomeModification forestModifier = BiomeModifications.create(new Identifier(MOD_ID, "forest_modifier"));
		final Predicate<BiomeSelectionContext> FOREST = BiomeSelectors.includeByKey(BiomeKeys.FOREST);

		forestModifier.add(ModificationPhase.REPLACEMENTS, FOREST, context -> {
			context.getGenerationSettings().removeBuiltInFeature(VegetationPlacedFeatures.TREES_BIRCH_AND_OAK.value());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.Trees.FOREST_TREES_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().removeBuiltInFeature(VegetationPlacedFeatures.PATCH_GRASS_FOREST.value());
			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.FOREST_GRASS_PLACED.getKey().orElseThrow());
		});

		forestModifier.add(ModificationPhase.ADDITIONS, FOREST, context -> {
			context.getGenerationSettings().addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, WondrousWildsFeatures.FOREST_BOULDER_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, WondrousWildsFeatures.FOREST_COARSE_DIRT_SPLOTCH_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.FOREST_FALLEN_LOG_PLACED.getKey().orElseThrow());

			context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, WondrousWildsFeatures.FOREST_BUSHES_PLACED.getKey().orElseThrow());
		});
	}

	private static void hookEntityCreation(Entity entityBeingCreated, ServerWorld world) {
		if (entityBeingCreated instanceof WolfEntity wolfEntity) {
			((MobEntityAccessor) wolfEntity).getTargetSelector().add(5, new UntamedActiveTargetGoal<>(wolfEntity, ChipmunkEntity.class, false, null));
		}
		else if (entityBeingCreated instanceof CatEntity catEntity) {
			((MobEntityAccessor) catEntity).getTargetSelector().add(1, new UntamedActiveTargetGoal<>(catEntity, ChipmunkEntity.class, false, null));
		}
		else if (entityBeingCreated instanceof OcelotEntity ocelotEntity) {
			((MobEntityAccessor) ocelotEntity).getTargetSelector().add(1, new ActiveTargetGoal<>(ocelotEntity, ChipmunkEntity.class, false));
		}
	}
}
