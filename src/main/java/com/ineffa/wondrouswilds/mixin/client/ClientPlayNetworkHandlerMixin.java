package com.ineffa.wondrouswilds.mixin.client;

import com.ineffa.wondrouswilds.WondrousWilds;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void sendIncompatibilityWarning(GameJoinS2CPacket packet, CallbackInfo callback) {
        if (WondrousWilds.config.clientSettings.showShadersWarning && this.client.player != null) {
            FabricLoader fabricInstance = FabricLoader.getInstance();
            if (fabricInstance.isModLoaded("iris") || fabricInstance.isModLoaded("optifabric")) {
                this.client.player.sendMessage(Text.translatable("mod." + WondrousWilds.MOD_ID + ".warning.shaders").formatted(Formatting.RED));
            }
        }
    }
}
