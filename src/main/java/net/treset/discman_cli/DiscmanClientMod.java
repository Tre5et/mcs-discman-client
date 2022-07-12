package net.treset.discman_cli;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.treset.discman_cli.config.Config;
import net.treset.discman_cli.networking.CommunicationManager;
import net.treset.discman_cli.networking.ConnectionManager;
import net.treset.discman_cli.tools.EventHandler;
import net.treset.discman_cli.tools.MinecraftServerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscmanClientMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("discman_cli");

	@Override
	public void onInitialize() {
		Config.init();

		if(!ConnectionManager.establishConnection()) {
			if(Config.requireServer)
				new Thread(MinecraftServerInstance::waitForCloseServer).start();
			return;
		}
		new Thread(CommunicationManager::handleData).start();

		CommunicationManager.sendDummyData();

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			EventHandler.onServerStopping();
		});
	}
}
