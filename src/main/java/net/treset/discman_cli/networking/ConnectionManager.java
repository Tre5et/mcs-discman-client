package net.treset.discman_cli.networking;

import net.minecraft.server.command.CommandManager;
import net.treset.discman_cli.DiscmanClientMod;
import net.treset.discman_cli.config.Config;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConnectionManager {
    private static int port = 876;
    private static boolean connected = false;
    private static String sessionId;
    private static Socket s;
    private static DataOutputStream serverSender;
    private static BufferedReader serverReader;

    public static int getPort() { return port; }
    public static boolean setPort(int newPort) { port = newPort; return true; }

    public static boolean isConnected() { return connected; }

    public static String getSessionId() { return sessionId; }
    public static boolean setSessionId(String id) { sessionId = id; return true; }

    public static DataOutputStream getServerSender() { return serverSender; }
    public static BufferedReader getServerReader() { return serverReader; }

    public static boolean establishConnection() {
        port = Config.port;
        try {
            s = new Socket("localhost", port);
            serverSender = new DataOutputStream(s.getOutputStream());
            serverReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            DiscmanClientMod.LOGGER.error("Error establishing connection to server. Unable to open socket. Stacktrace:");
            e.printStackTrace();
            return false;
        }

        sessionId = String.valueOf(System.currentTimeMillis());

        try {
            serverSender.writeBytes("sid/" + sessionId + "\n");
        } catch (IOException e) {
            DiscmanClientMod.LOGGER.error("Error initializing connection to server. Unable to send session id. Stacktrace:");
            e.printStackTrace();
            return false;
        }

        try {
            String sid = serverReader.readLine();
            if(!sid.startsWith("sid") || !sid.substring(4).equals(sessionId)) {
                DiscmanClientMod.LOGGER.error("Error initializing connection to server. Didn't receive correct session id from sever: \"" + sid + "\". Expecting \"" + sessionId + "\". Closing connection");
                closeConnection(true);
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        CommunicationManager.updateReader();

        connected = true;

        DiscmanClientMod.LOGGER.info("Connection established");
        return true;
    }

    private static boolean closeAccepted = false;
    public static void acceptClose() {
        closeAccepted = true;
    }
    public static boolean closeConnection(boolean force) {
        DiscmanClientMod.LOGGER.info("Closing connection with id " + sessionId);

        if(closeAccepted) {
            DiscmanClientMod.LOGGER.warn("Found illegal close accepted state while closing connection with id " + sessionId + ". Closing anyway");
        }

        if(CommunicationManager.sendToServer("cls/" + sessionId) || force) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(!closeAccepted) {
                if(!force) {
                    DiscmanClientMod.LOGGER.error("Error closing connection with id " + sessionId + ". Close not accepted by server");
                    return false;
                }
                DiscmanClientMod.LOGGER.warn("Forcefully closing connection with id " + sessionId + " even though it wasn't accepted.");
            }
            closeAccepted = false;

            try {
                serverSender.close();
                serverReader.close();
                s.close();
            } catch (IOException e) {
                DiscmanClientMod.LOGGER.error("Error closing connection with id " + sessionId + ". Unable to close socket. Stacktrace:");
                e.printStackTrace();
                if(!force) return false;
            }

            DiscmanClientMod.LOGGER.info("Closed connection with id " + sessionId);

            serverSender = null;
            serverReader = null;
            sessionId = null;

            CommunicationManager.updateReader();

            connected = false;

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

            CommunicationManager.updateReader();

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

            connected = !success;

            return success;
        }

        DiscmanClientMod.LOGGER.error("Error handling close request with id " + sessionId + ". Unable to send close accept to server.");

        return false;
    }

}
