package net.treset.discman_cli.networking;

import net.treset.discman_cli.DiscmanClientMod;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DataSender {

    public static boolean sendDummyData() {

        DataOutputStream dos = ConnectionManager.getServerStream();
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
        DataOutputStream dos = ConnectionManager.getServerStream();
        if(dos == null) return false;

        try {
            dos.writeBytes("txt/" + message + "\n");
        } catch (IOException e) {
            return false;
        }

        System.out.println("message sent: " + message);
        return true;
    }

}