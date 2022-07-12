package net.treset.discman_cli.tools;

import net.minecraft.server.MinecraftServer;
import net.treset.discman_cli.DiscmanClientMod;
import net.treset.discman_cli.mixin.MinecraftServerMixin;

public class MinecraftServerInstance {
    private static MinecraftServer instance;

    public static MinecraftServer getInstance() { return instance; }
    public static void setInstance(MinecraftServer newInstance) {
        instance = newInstance;
        DiscmanClientMod.LOGGER.info("Updated server instance.");
    }

    public static void waitForCloseServer() {
        while(MinecraftServerInstance.getInstance() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        MinecraftServerInstance.getInstance().stop(true);
    }
}
