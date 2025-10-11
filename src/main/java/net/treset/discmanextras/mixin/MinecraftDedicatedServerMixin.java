package net.treset.discmanextras.mixin;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.management.ManagementServer;
import net.treset.discmanextras.DiscmanManagementServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MinecraftDedicatedServerMixin {
    @Accessor("managementServer")
    public abstract ManagementServer getManagementServer();

    @Inject(method = "setupServer()Z", at = @At("RETURN"))
    private void setupServer(CallbackInfoReturnable<Boolean> info) {
        DiscmanManagementServer.init(getManagementServer());
    }
}
