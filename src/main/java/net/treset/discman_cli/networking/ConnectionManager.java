package net.treset.discman_cli.networking;

import net.treset.discman_cli.DiscmanClientMod;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionManager {
    private static int port = 876;
    private static String sessionId;
    private static Socket s;
    private static DataOutputStream serverStream;

    public static int getPort() { return port; }
    public static boolean setPort(int newPort) { port = newPort; return true; }

    public static String getSessionId() { return sessionId; }
    public static boolean setSessionId(String id) { sessionId = id; return true; }

    public static DataOutputStream getServerStream() { return serverStream; }

    public static boolean establishConnection() {
        try {
            s = new Socket("localhost", port);
            serverStream = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            return false;
        }

        sessionId = String.valueOf(System.currentTimeMillis());

        try {
            serverStream.writeBytes("sid/" + sessionId + "\n");
        } catch (IOException e) {
            return false;
        }

        DiscmanClientMod.LOGGER.info("Conn est");
        return true;
    }

    public static boolean closeConnection() {
        try {
            serverStream.writeBytes("dc/" + sessionId);
            serverStream.close();
            s.close();
        } catch (IOException e) {
            return false;
        }

        serverStream = null;

        DiscmanClientMod.LOGGER.info("Conn cls");
        return true;
    }

}
