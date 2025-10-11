package net.treset.discmanextras.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.minecraft.util.Identifier;
import net.treset.discmanextras.accessors.OutgoingRpcMethodBuilderAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OutgoingRpcMethod.Builder.class)
public abstract class OutgoingRpcMethodBuilderMixin<T extends OutgoingRpcMethod<?, ?>> implements OutgoingRpcMethodBuilderAccessor<T> {
    @Shadow
    @Override
    public abstract RegistryEntry.Reference<T> buildAndRegister(Identifier id);

    @Override
    public RegistryEntry.Reference<T> buildAndRegisterDiscman(String path) {
        return buildAndRegister(Identifier.of("discman", path));
    }
}
