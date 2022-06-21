package net.treset.discman_cli.tools;

import net.treset.discman_cli.networking.ConnectionManager;

public class EventHandler {
    public static void onServerStopping() {
        ConnectionManager.closeConnection();
    }
}
