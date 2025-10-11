package net.treset.discmanextras;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.dedicated.management.ManagementServer;
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.treset.discmanextras.accessors.ManagementServerAccessor;

public class DiscmanManagementServer {
    private static ManagementServer managementServer;

    public static void init(ManagementServer server) {
        managementServer = server;
        if(managementServer == null) {
            DiscmanClientMod.LOGGER.error("Discman failed to get Management Server");
        } else {
            DiscmanClientMod.LOGGER.info("Discman Management Server initialized");
        }
    }

    public static boolean isInitialized() {
        return managementServer == null;
    }

    public static <T> void notifyAll(
            RegistryEntry.Reference<? extends OutgoingRpcMethod<T, ?>> method,
            T payload
    ) {
        if (isInitialized()) return;
        ((ManagementServerAccessor)managementServer)
                .forEachConnection(connection ->
                        connection.sendNotification(method, payload)
                );
    }
}
