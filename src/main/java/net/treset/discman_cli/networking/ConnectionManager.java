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
            if(!sid.startsWith("sid") || !sid.substring(4).equals(sessionId)) {
                closeConnection();
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        DiscmanClientMod.LOGGER.info("Conn est");
        return true;
    }

    private static boolean closeAccepted = false;
    public static void acceptClose() { closeAccepted = true; }
    public static boolean closeConnection() {
        boolean success = true;

        if(CommunicationManager.sendToServer("cls/" + sessionId)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(!closeAccepted) return false;
            closeAccepted = false;

            try {
                CommunicationManager.requestCloseReader();
                serverSender.close();
                serverReader.close();
                s.close();
            } catch (IOException e) {
                return false;
            }

            serverSender = null;
            serverReader = null;
            sessionId = null;
        }
        else success = false;

        DiscmanClientMod.LOGGER.info(success ? "Connection close" : "Connection close unsuccessfull");
        return true;
    }

    public static boolean respondToClosingConnection(String sid) {
        if(!sid.equals(sessionId)) {
            CommunicationManager.sendToServer("dcl/" + sessionId);
            return false;
        }

        boolean success = true;
        if(CommunicationManager.sendToServer("acl/" + sessionId)) {
            DiscmanClientMod.LOGGER.info("Connection close accepted");
            try {
                serverReader.close();
                serverSender.close();
            } catch (IOException e) {
                success = false;
            }

            serverReader = null;
            serverSender = null;

            try {
                s.close();
            } catch (IOException e) {
                success = false;
            }

            sessionId = null;
        } else success = false;

        DiscmanClientMod.LOGGER.info(success ? "Connection closed" : "Connection closed unsuccessfully");

        return success;
    }

}
