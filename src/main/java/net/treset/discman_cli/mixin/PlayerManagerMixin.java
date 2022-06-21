package net.treset.discman_cli.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.registry.RegistryKey;
import net.treset.discman_cli.DiscmanClientMod;
import net.treset.discman_cli.networking.DataSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.function.Function;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"))
    private void broadcast(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory, RegistryKey<MessageType> typeKey, CallbackInfo info) {
        if(!(message.getContent() instanceof TranslatableTextContent)) return;

        String key = ((TranslatableTextContent)message.getContent()).getKey();
        switch(key) {
            case "multiplayer.player.joined" -> DataSender.requestMessage(message.getString());
        }
    }
}
