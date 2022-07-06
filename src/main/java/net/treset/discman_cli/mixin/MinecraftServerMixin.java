package net.treset.discman_cli.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import net.treset.discman_cli.tools.MinecraftServerInstance;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;
import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "startServer(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;", at = @At("RETURN"))
    private static <S extends MinecraftServer> void startServer(Function<Thread, S> serverFactory, CallbackInfoReturnable<S> info) {
        MinecraftServerInstance.setInstance(info.getReturnValue());
        System.out.println("got server");
    }
}
