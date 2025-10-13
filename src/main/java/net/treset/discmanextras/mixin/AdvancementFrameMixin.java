package net.treset.discmanextras.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.treset.discmanextras.rpc.RpcAdvancement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementFrame.class)
public class AdvancementFrameMixin {
    @Inject(
            method = "getChatAnnouncementText(Lnet/minecraft/advancement/AdvancementEntry;Lnet/minecraft/server/network/ServerPlayerEntity;)Lnet/minecraft/text/MutableText;",
            at = @At("RETURN")
    )
    private void getChatAnnouncementText(AdvancementEntry advancementEntry, ServerPlayerEntity player, CallbackInfoReturnable<MutableText> ci) {
        RpcAdvancement.handle(player, advancementEntry, ci.getReturnValue());
    }
}
