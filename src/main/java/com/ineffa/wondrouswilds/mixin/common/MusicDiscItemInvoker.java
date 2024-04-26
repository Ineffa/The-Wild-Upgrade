package com.ineffa.wondrouswilds.mixin.common;

import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MusicDiscItem.class)
public interface MusicDiscItemInvoker {

    @Invoker("<init>")
    static MusicDiscItem createNewMusicDisc(int comparatorOutput, SoundEvent sound, Item.Settings settings, int lengthInSeconds) {
        throw new AssertionError();
    }
}
