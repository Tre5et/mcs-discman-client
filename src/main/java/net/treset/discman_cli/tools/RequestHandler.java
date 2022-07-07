package net.treset.discman_cli.tools;

import net.treset.discman_cli.DiscmanClientMod;
import net.treset.discman_cli.networking.CommunicationManager;

public class RequestHandler {
    public static void handleTimeGetter(String arg) {
        String out = "";

        switch(arg) {
            case "ig_now" -> out = "tim/" + getIngameTime();
            default -> out = "-1";
        }

        CommunicationManager.sendToServer(out);
    }

    private static String getIngameTime() {
        DiscmanClientMod.LOGGER.info("Got ingame time.");
        return String.valueOf(MinecraftServerInstance.getInstance().getOverworld().getTimeOfDay());
    }

    public static void handlePlayerGetter(String arg) {
        String out = "";

        switch(arg) {
            case "curr" -> out = "ply/" + getPlayers();
            default -> out = "-1";
        }

        CommunicationManager.sendToServer(out);
    }

    private static String getPlayers() {
        DiscmanClientMod.LOGGER.info("Got players.");

        String[] players = MinecraftServerInstance.getInstance().getPlayerNames();

        return String.join(";", players);
    }
}
