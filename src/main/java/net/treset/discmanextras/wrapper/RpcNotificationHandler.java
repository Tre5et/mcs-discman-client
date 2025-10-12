package net.treset.discmanextras.wrapper;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.treset.discmanextras.DiscmanManagementServer;

public class RpcNotificationHandler<T> {
    private final RegistryEntry.Reference<? extends OutgoingRpcMethod<T, ?>> method;

    public RpcNotificationHandler(RegistryEntry.Reference<? extends OutgoingRpcMethod<T, ?>> method) {
        this.method = method;
    }

    public void send(T data) {
        DiscmanManagementServer.notifyAll(
                method,
                data
        );
    }
}
