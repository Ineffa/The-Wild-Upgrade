package com.ineffa.wondrouswilds.registry;

import com.ineffa.wondrouswilds.WondrousWilds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WondrousWildsSounds {

    public static final SoundEvent WOODPECKER_CHIRP = createSoundEvent("entity.woodpecker.chirp");
    public static final SoundEvent WOODPECKER_DRUM = createSoundEvent("entity.woodpecker.drum");

    public static final SoundEvent MUSIC_OVERWORLD_BIRCH_FOREST = createSoundEvent("music.overworld.birch_forest");
    public static final SoundEvent MUSIC_DISC_AVIAN = createSoundEvent("music_disc.avian");

    private static SoundEvent createSoundEvent(String name) {
        Identifier id = new Identifier(WondrousWilds.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void initialize() {}
}
