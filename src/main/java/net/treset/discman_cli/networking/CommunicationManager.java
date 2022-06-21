package net.treset.discman_cli.networking;

import net.treset.discman_cli.DiscmanClientMod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

public class CommunicationManager {

    public static boolean sendDummyData() {

        DataOutputStream dos = ConnectionManager.getServerSender();
        if(dos == null) return false;

        try {
            dos.writeBytes("test_message \n");
            dos.writeBytes("test_message2 \n");
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static boolean requestMessage(String message) {
        DataOutputStream dos = ConnectionManager.getServerSender();
        if(dos == null) return false;

        try {
            dos.writeBytes("txt/" + message + "\n");
        } catch (IOException e) {
            return false;
        }

        System.out.println("message sent: " + message);
        return true;
    }

    //continuous code, only run async
    public static boolean handleData() {
        BufferedReader br = ConnectionManager.getServerReader();
        if(br == null) return false;

        String msg;

        boolean cancel = false;
        while(!cancel) {
            try {
                msg = br.readLine();
            } catch (IOException e) {
                return false;
            }

            if(msg == null) continue;

            switch(msg.substring(0, 3)) {
                case "dcn" -> {
                    if(msg.substring(4).equals(ConnectionManager.getSessionId())) {
                        cancel = true;
                    }
                }
                case "txt" -> printText(msg.substring(4));
                default -> System.out.println(msg);
            }


        }

        return ConnectionManager.closeConnection();
    }

    public static void printText(String text) {
        DiscmanClientMod.LOGGER.info(text);
    }

}
