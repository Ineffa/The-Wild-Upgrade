package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.registry.WondrousWildsSpawnGroups;
import net.minecraft.entity.SpawnGroup;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(SpawnGroup.class)
public abstract class SpawnGroupMixin {

    @Shadow
    @Final
    @Mutable
    private static SpawnGroup[] field_6301;

    @Invoker("<init>")
    private static SpawnGroup newSpawnGroup(String internalName, int internalId, String name, int spawnCap, boolean peaceful, boolean rare, int immediateDespawnRange) {
        throw new AssertionError();
    }


    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/entity/SpawnGroup;field_6301:[Lnet/minecraft/entity/SpawnGroup;", shift = At.Shift.AFTER))
    private static void addNewSpawnGroups(CallbackInfo callback) {
        var spawnGroups = new ArrayList<>(Arrays.asList(field_6301));
        var lastSpawnGroup = spawnGroups.get(spawnGroups.size() - 1);
        var internalPrefix = "WONDROUS_WILDS_";

        var fireflies = newSpawnGroup(internalPrefix + "FIREFLIES", lastSpawnGroup.ordinal() + 1, "fireflies", 15, true, false, 64);
        WondrousWildsSpawnGroups.FIREFLIES = fireflies;
        spawnGroups.add(fireflies);

        field_6301 = spawnGroups.toArray(new SpawnGroup[0]);
    }
}
