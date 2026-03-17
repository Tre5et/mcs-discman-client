package net.treset.discmanextras.mixin;

import net.minecraft.server.MinecraftServer;
import net.treset.discmanextras.DiscmanExtrasMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "spin(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;", at = @At("RETURN"))
    private static <S extends MinecraftServer> void spin(Function<Thread, S> serverFactory, CallbackInfoReturnable<S> info) {
        DiscmanExtrasMod.setServerInstance(info.getReturnValue());
    }
}
