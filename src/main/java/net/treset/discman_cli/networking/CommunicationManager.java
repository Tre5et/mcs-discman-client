package net.treset.discman_cli.networking;

import net.treset.discman_cli.DiscmanClientMod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

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

    private static boolean closeReader = false;

    //continuous code, only run async
    public static boolean handleData() {
        BufferedReader br = ConnectionManager.getServerReader();
        if(br == null) return false;

        String msg;

        while(!closeReader) {
            try {
                msg = br.readLine();
            } catch (IOException e) {
                return false;
            }

            if(msg == null || msg.isEmpty()) continue;

            switch(msg.substring(0, 3)) {
                case "cls" -> ConnectionManager.respondToClosingConnection(msg.substring(4));
                case "txt" -> printText(msg.substring(4));
                case "acl" -> ConnectionManager.acceptClose();
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
            return false;
        }
        return true;
    }

}
