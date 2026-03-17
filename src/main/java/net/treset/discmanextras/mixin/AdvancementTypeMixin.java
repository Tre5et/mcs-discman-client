package net.treset.discmanextras.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.treset.discmanextras.rpc.RpcAdvancement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementType.class)
public class AdvancementTypeMixin {
    @Inject(
            method = "createAnnouncement(Lnet/minecraft/advancements/AdvancementHolder;Lnet/minecraft/server/level/ServerPlayer;)Lnet/minecraft/network/chat/MutableComponent;",
            at = @At("RETURN")
    )
    private void createAnnouncement(AdvancementHolder advancementHolder, ServerPlayer player, CallbackInfoReturnable<MutableComponent> ci) {
        RpcAdvancement.handle(player, advancementHolder, ci.getReturnValue());
    }
}
