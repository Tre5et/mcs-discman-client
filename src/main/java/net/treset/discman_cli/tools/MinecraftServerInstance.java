package net.treset.discman_cli.tools;

import net.minecraft.server.MinecraftServer;
import net.treset.discman_cli.mixin.MinecraftServerMixin;

public class MinecraftServerInstance {
    private static MinecraftServer instance;

    public static MinecraftServer getInstance() { return instance; }
    public static void setInstance(MinecraftServer newInstance) { instance = newInstance; }
}
