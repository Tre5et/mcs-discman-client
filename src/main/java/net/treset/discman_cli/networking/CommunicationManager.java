package net.treset.discman_cli.networking;

import net.minecraft.server.command.CommandManager;
import net.treset.discman_cli.DiscmanClientMod;
import net.treset.discman_cli.tools.RequestHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;

public class CommunicationManager {

    public static boolean sendDummyData() {

        DataOutputStream dos = ConnectionManager.getServerSender();
        if(dos == null) return false;

        CommunicationManager.sendToServer("testStr1");
        CommunicationManager.sendToServer("testStr2");

        return true;
    }

    public static boolean requestMessage(String message) {
        return sendToServer("txt/" + message);
    }

    public static boolean requestJoin(String player) { return sendToServer("joi/" + player);}
    public static boolean requestLeave(String player) { return sendToServer("lev/" + player); }
    public static boolean requestDeath(String message) { return sendToServer("dth/" + message); }
    public static boolean requestAdvancement(String message) { return sendToServer("adv/" + message); }

    private static BufferedReader br;
    public static void updateReader() {
        br = ConnectionManager.getServerReader();
    }

    private static boolean closeReader = false;

    //continuous code, only run async
    public static boolean handleData() {
        updateReader();

        DiscmanClientMod.LOGGER.info("Opened reader.");

        String msg;

        while(!closeReader) {
            if(br == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            try {
                msg = br.readLine();
            } catch (IOException e) {
                DiscmanClientMod.LOGGER.warn("Error reading line from server. Stacktrace:");
                e.printStackTrace();
                if(Objects.equals(e.getMessage(), "Connection reset")) {
                    DiscmanClientMod.LOGGER.warn("Closing connection with id " + ConnectionManager.getSessionId() + " because server closed unexpectedly.");
                    ConnectionManager.closeConnection(true);
                }
                continue;
            }

            if(msg == null || msg.isEmpty()) continue;

            switch(msg.substring(0, 3)) {
                case "cls" -> ConnectionManager.respondToClosingConnection(msg.substring(4));
                case "txt" -> printText(msg.substring(4));
                case "acl" -> ConnectionManager.acceptClose();
                case "tim" -> RequestHandler.handleTimeGetter(msg.substring(4));
                case "ply" -> RequestHandler.handlePlayerGetter(msg.substring(4));
                default -> System.out.println(msg);
            }


        }

        closeReader = false;

        return true;
    }

    public static boolean requestCloseReader() {
        closeReader = true;
        return true;
    }

    public static void printText(String text) {
        DiscmanClientMod.LOGGER.info(text);
    }

    public static boolean sendToServer(String message) {
        DataOutputStream dos = ConnectionManager.getServerSender();
        if(dos == null) return false;

        try {
            dos.writeBytes(message + "\n");
        } catch (IOException e) {
            DiscmanClientMod.LOGGER.error("Unable to send message \"" + message + "\" to server. Stacktrace:");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
