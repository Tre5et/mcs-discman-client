package net.treset.discman_cli.networking;

import net.treset.discman_cli.DiscmanClientMod;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DataSender {

    public static void init() throws IOException {
        // Create client socket
        Socket s = new Socket("localhost", 856);

        // to send data to the server
        DataOutputStream dos
                = new DataOutputStream(
                s.getOutputStream());

        dos.writeBytes("test_message \n");
        dos.writeBytes("test_message2 \n");
        dos.writeBytes("dc");
        dos.close();

        s.close();

        DiscmanClientMod.LOGGER.info("done SENDING DATA aaaaaaaaaaaaaaaaaaaaaa");
    }

}
