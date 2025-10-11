package net.treset.discmanextras.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.discmanextras.rpc.RpcDeath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo info) {
        ServerPlayerEntity thisE = (ServerPlayerEntity)(Object)this;
        RpcDeath.handle(thisE, thisE.getDamageTracker().getDeathMessage());
    }
}
