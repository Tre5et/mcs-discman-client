package net.treset.discman_cli.tools;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.treset.discman_cli.mixin.MinecraftServerMixin;
import net.treset.discman_cli.networking.CommunicationManager;

public class RequestHandler {
    public static void handleTimeG(String arg) {
        String out = "";

        switch(arg) {
            case "ig_now" -> out = getIngameTime();
            default -> out = "-1";
        }

        CommunicationManager.sendToServer("tim/" + out);
    }

    private static String getIngameTime() {
        return String.valueOf(MinecraftServerInstance.getInstance().getOverworld().getTimeOfDay());
    }
}
