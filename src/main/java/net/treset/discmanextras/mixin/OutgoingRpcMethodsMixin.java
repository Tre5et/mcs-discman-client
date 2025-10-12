package net.treset.discmanextras.mixin;

import net.minecraft.server.dedicated.management.OutgoingRpcMethods;
import net.treset.discmanextras.wrapper.RpcRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OutgoingRpcMethods.class)
public class OutgoingRpcMethodsMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void registerOutgoingRpc(CallbackInfo ci) {
        RpcRegistration.applyRegister();
    }
}
