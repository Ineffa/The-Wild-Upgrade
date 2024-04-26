package com.ineffa.wondrouswilds.config;

import com.ineffa.wondrouswilds.WondrousWilds;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = WondrousWilds.MOD_ID)
public class WondrousWildsConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public ClientSettings clientSettings = new ClientSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public MobSettings mobSettings = new MobSettings();

    public static class ClientSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean showShadersWarning = true;
    }

    public static class MobSettings {
        // Firefly
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.Gui.RequiresRestart
        public boolean firefliesSpawnNaturally = true;

        @ConfigEntry.Gui.Tooltip
        public boolean firefliesLandOnMobs = true;

        // Woodpecker
        @ConfigEntry.Gui.Tooltip
        public boolean woodpeckersDrum = true;
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 60, max = 6000)
        public int woodpeckerDrumChance = 600;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean woodpeckersBuildNests = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean woodpeckersInteractWithBlocks = true;

        public int woodpeckerBondingTaskInterval = 24000;
    }
}
