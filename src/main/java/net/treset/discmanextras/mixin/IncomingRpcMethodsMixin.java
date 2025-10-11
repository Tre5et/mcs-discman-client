package net.treset.discmanextras.mixin;

import net.minecraft.server.dedicated.management.IncomingRpcMethod;
import net.minecraft.server.dedicated.management.IncomingRpcMethods;
import net.treset.discmanextras.rpc.RpcGetTime;
import net.treset.discmanextras.rpc.RpcSetTime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IncomingRpcMethods.class)
public abstract class IncomingRpcMethodsMixin {
    @Inject(method = "registerAndGetDefault(Lnet/minecraft/registry/Registry;)Lnet/minecraft/server/dedicated/management/IncomingRpcMethod;", at = @At("TAIL"))
    private static void registerIncomingRpc(CallbackInfoReturnable<IncomingRpcMethod> ci) {
        RpcGetTime.register();
        RpcSetTime.register();
    }
}
