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
        DiscmanClientMod.LOGGER.info("Closing connection with id " + sessionId);

        if(closeAccepted) {
            DiscmanClientMod.LOGGER.warn("Found illegal close accepted state while closing connection with id " + sessionId + ". Closing anyway");
        }

        if(CommunicationManager.sendToServer("cls/" + sessionId)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(!closeAccepted) {
                DiscmanClientMod.LOGGER.error("Error closing connection with id " + sessionId + ". Close not accepted by server");
                return false;
            }
            closeAccepted = false;

            try {
                CommunicationManager.requestCloseReader();
                serverSender.close();
                serverReader.close();
                s.close();
            } catch (IOException e) {
                DiscmanClientMod.LOGGER.error("Error closing connection with id " + sessionId + ". Unable to close socket. Stacktrace:");
                e.printStackTrace();
                return false;
            }

            DiscmanClientMod.LOGGER.info("Closed connection with id " + sessionId);

            serverSender = null;
            serverReader = null;
            sessionId = null;

            return true;
        }

        DiscmanClientMod.LOGGER.error("Error closing connection with id " + sessionId + ". Unable to send close request to server");
        return false;
    }

    public static boolean respondToClosingConnection(String sid) {
        DiscmanClientMod.LOGGER.info("Received connection close request with id " + sid);

        if(!sid.equals(sessionId)) {
            DiscmanClientMod.LOGGER.info("Rejected close request with id " + sid + ". Server session doesn't match the current session " + sessionId);
            CommunicationManager.sendToServer("dcl/" + sessionId);
            return false;
        }

        boolean success = true;
        if(CommunicationManager.sendToServer("acl/" + sessionId)) {
            DiscmanClientMod.LOGGER.info("Accepted connection close request with id " + sessionId);
            try {
                serverReader.close();
                serverSender.close();
            } catch (IOException e) {
                DiscmanClientMod.LOGGER.error("Error handling close request with id " + sessionId + ". Unable to close io. Stacktrace:");
                e.printStackTrace();
                success = false;
            }

            serverReader = null;
            serverSender = null;

            try {
                s.close();
            } catch (IOException e) {
                DiscmanClientMod.LOGGER.error("Error handling close request with id " + sessionId + ". Unable to close socket. Stacktrace:");
                e.printStackTrace();
                success = false;
            }

            if(success) {
                DiscmanClientMod.LOGGER.info("Closed connection after request with id " + sessionId);
            }

            sessionId = null;

            return success;
        }

        DiscmanClientMod.LOGGER.error("Error handling close request with id " + sessionId + ". Unable to send close accept to server.");

        return false;
    }

}
