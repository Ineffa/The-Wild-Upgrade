package com.ineffa.wondrouswilds.registry;

import com.ineffa.wondrouswilds.WondrousWilds;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WondrousWildsParticles {

    public static final DefaultParticleType BIRCH_LEAF = registerSimpleParticleType("birch_leaf", false);
    public static final DefaultParticleType SHARPSHOT_HIT = registerSimpleParticleType("sharpshot_hit", false);

    private static DefaultParticleType registerSimpleParticleType(String name, boolean alwaysShow) {
        return (DefaultParticleType) registerParticleType(name, FabricParticleTypes.simple(alwaysShow));
    }

    private static <T extends ParticleEffect> ParticleType<T> registerParticleType(String name, ParticleType<T> type) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(WondrousWilds.MOD_ID, name), type);
    }

    public static void initialize() {}
}
