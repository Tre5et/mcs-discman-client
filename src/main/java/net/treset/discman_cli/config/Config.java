package net.treset.discman_cli.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.treset.discman_cli.tools.FileTools;

import java.io.File;

public class Config {
    public static boolean requireServer = false;
    public static int port = 876;

    public static void init() {
        JsonObject config = FileTools.readJsonFile(new File("./config/discman-client.json"));
        if(config == null) {
            config = new JsonObject();
            config.add("requireServer", new JsonPrimitive(false));
            config.add("port", new JsonPrimitive(876));
            FileTools.writeJsonToFile(config, new File("./config/discman-client.json"));
            return;
        }
        requireServer = config.getAsJsonPrimitive("requireServer").getAsBoolean();
        port = config.getAsJsonPrimitive("port").getAsInt();
    }
}
