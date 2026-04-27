package net.treset.discmanextras;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscmanExtrasMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("discman_cli");

    private static MinecraftServer SERVER_INSTANCE = null;

    public static CommandDispatcher<CommandSourceStack> dispatcher;

	@Override
	public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> DiscmanExtrasMod.dispatcher = dispatcher);

    }

    public static MinecraftServer getServerInstance() {
        return SERVER_INSTANCE;
    }

    public static void setServerInstance(MinecraftServer serverInstance) {
        SERVER_INSTANCE = serverInstance;
    }
}
