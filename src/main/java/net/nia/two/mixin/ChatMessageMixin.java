package net.nia.witchinghour.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.nia.witchinghour.magic.spells.create.SpellClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatMessageMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "sendChatMessage", at = @At("HEAD"))
    private void onSendChatMessage(String message, CallbackInfo ci) {
        SpellClient.updateLastResult(message);
        SpellClient.sendPacket(null);
    }
}