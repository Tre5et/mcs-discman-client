package net.treset.discmanextras.wrapper;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.treset.discmanextras.DiscmanManagementServer;

/**
 * Allows sending an RPC notification.
 * @param <T> The type of object the notification sends.
 */
public class RpcNotificationHandler<T> {
    private final RegistryEntry.Reference<? extends OutgoingRpcMethod<T, ?>> method;

    public RpcNotificationHandler(RegistryEntry.Reference<? extends OutgoingRpcMethod<T, ?>> method) {
        this.method = method;
    }

    /**
     * Sends an RPC notification containing the data of the object in the configured format to all clients.
     * @param data The data object to send.
     */
    public void send(T data) {
        DiscmanManagementServer.notifyAll(
                method,
                data
        );
    }
}
