package net.treset.discman_cli;

import net.fabricmc.api.ModInitializer;
import net.treset.discman_cli.networking.ConnectionManager;
import net.treset.discman_cli.networking.DataSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DiscmanClientMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("discman_cli");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		if(!ConnectionManager.establishConnection()) return;
		DataSender.sendDummyData();
	}
}
