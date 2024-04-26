package com.ineffa.wondrouswilds.client.rendering;

import com.ineffa.wondrouswilds.registry.WondrousWildsBlocks;
import com.ineffa.wondrouswilds.registry.WondrousWildsItems;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.util.math.random.Random;

public final class WondrousWildsColorProviders {

    public static void register() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D), WondrousWildsBlocks.BUSH);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor(), WondrousWildsBlocks.IVY);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) return getYellowBirchLeavesColor();
            Random random = Random.create(state.getRenderingSeed(pos));
            random.skip(tintIndex);
            byte i = (byte) random.nextInt(19);
            if (i <= 1) return getRedBirchLeavesColor();
            else if (i <= 8) return getYellowBirchLeavesColor();
            else return getOrangeBirchLeavesColor();
        }, WondrousWildsBlocks.FALLEN_BIRCH_LEAVES);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> getYellowBirchLeavesColor(), WondrousWildsBlocks.YELLOW_BIRCH_LEAVES);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> getOrangeBirchLeavesColor(), WondrousWildsBlocks.ORANGE_BIRCH_LEAVES);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> getRedBirchLeavesColor(), WondrousWildsBlocks.RED_BIRCH_LEAVES);

        ColorProviderRegistry.ITEM.register((state, tintIndex) -> GrassColors.getColor(0.5D, 1.0D), WondrousWildsItems.BUSH);
        ColorProviderRegistry.ITEM.register((state, tintIndex) -> FoliageColors.getDefaultColor(), WondrousWildsItems.IVY);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> switch (tintIndex) {
            default -> getOrangeBirchLeavesColor();
            case 1 -> getYellowBirchLeavesColor();
            case 2 -> getRedBirchLeavesColor();
        }, WondrousWildsItems.FALLEN_BIRCH_LEAVES);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> getYellowBirchLeavesColor(), WondrousWildsItems.YELLOW_BIRCH_LEAVES);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> getOrangeBirchLeavesColor(), WondrousWildsItems.ORANGE_BIRCH_LEAVES);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> getRedBirchLeavesColor(), WondrousWildsItems.RED_BIRCH_LEAVES);
    }

    public static int getOldGrowthBirchForestGrassColor() {
        return 7709005;
    }

    public static int getYellowBirchLeavesColor() {
        return 15380780;
    }

    public static int getOrangeBirchLeavesColor() {
        return 15827498;
    }

    public static int getRedBirchLeavesColor() {
        return 16204080;
    }
}
