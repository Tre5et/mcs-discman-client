package net.treset.discmanextras.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.treset.discmanextras.rpc.RpcDeath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"))
    private void die(DamageSource damageSource, CallbackInfo info) {
        ServerPlayer thisE = (ServerPlayer)(Object)this;
        RpcDeath.handle(thisE, thisE.getCombatTracker().getDeathMessage());
    }
}
