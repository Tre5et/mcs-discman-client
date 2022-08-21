package net.treset.discman_cli;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.treset.discman_cli.commands.CommandHandler;
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

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandHandler.registerCommands(dispatcher, environment);
		});

		if(!ConnectionManager.establishConnection()) {
			if(Config.requireServer) {
				new Thread(MinecraftServerInstance::waitForCloseServer).start();
				return;
			}
		}
		new Thread(CommunicationManager::handleData).start();

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			EventHandler.onServerStopping();
		});
	}
}
