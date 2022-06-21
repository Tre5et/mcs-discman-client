package net.treset.discman_cli.networking;

import net.treset.discman_cli.DiscmanClientMod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConnectionManager {
    private static int port = 876;
    private static String sessionId;
    private static Socket s;
    private static DataOutputStream serverSender;
    private static BufferedReader serverReader;

    public static int getPort() { return port; }
    public static boolean setPort(int newPort) { port = newPort; return true; }

    public static String getSessionId() { return sessionId; }
    public static boolean setSessionId(String id) { sessionId = id; return true; }

    public static DataOutputStream getServerSender() { return serverSender; }
    public static BufferedReader getServerReader() { return serverReader; }

    public static boolean establishConnection() {
        try {
            s = new Socket("localhost", port);
            serverSender = new DataOutputStream(s.getOutputStream());
            serverReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            return false;
        }

        sessionId = String.valueOf(System.currentTimeMillis());

        try {
            serverSender.writeBytes("sid/" + sessionId + "\n");
        } catch (IOException e) {
            return false;
        }

        try {
            String sid = serverReader.readLine();
            if(!sid.substring(0,3).equals("sid") || !sid.substring(4).equals(sessionId)) {
                closeConnection();
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        DiscmanClientMod.LOGGER.info("Conn est");
        return true;
    }

    public static boolean closeConnection() {
        try {
            serverSender.writeBytes("dcn/" + sessionId);
            serverSender.close();
            s.close();
        } catch (IOException e) {
            return false;
        }

        serverSender = null;

        DiscmanClientMod.LOGGER.info("Conn cls");
        return true;
    }

}
