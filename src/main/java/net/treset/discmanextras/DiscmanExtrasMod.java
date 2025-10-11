package net.treset.discmanextras;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscmanExtrasMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("discman_cli");

    private static MinecraftServer SERVER_INSTANCE = null;

	@Override
	public void onInitialize() {}

    public static MinecraftServer getServerInstance() {
        return SERVER_INSTANCE;
    }

    public static void setServerInstance(MinecraftServer serverInstance) {
        SERVER_INSTANCE = serverInstance;
    }
}
