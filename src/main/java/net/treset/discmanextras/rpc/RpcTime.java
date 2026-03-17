package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcMethodBuilder;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.treset.discmanextras.DiscmanExtrasMod;

@ServerManagementInitialized
public class RpcTime {
    static {
        RpcMethodBuilder.of(ManagementSchema.INTEGER)
                .responsePropertyName("time")
                .description("Get the in-game time of day")
                .identifier("discman", "server/time")
                .build(RpcTime::getTime);

        RpcMethodBuilder.of(ManagementSchema.BOOLEAN)
                .responsePropertyName("success")
                .parameter(ManagementSchema.INTEGER)
                .parameterName("time")
                .description("Set the in-game time of day")
                .identifier("discman", "server/time/set")
                .build(RpcTime::setTime);
    }

    public static int getTime(MinecraftApi dispatcher) {
        if(DiscmanExtrasMod.getServerInstance() == null) {
            return -1;
        }
        return (int)DiscmanExtrasMod.getServerInstance().overworld().getDefaultClockTime();
    }

    public static Boolean setTime(MinecraftApi dispatcher, Integer time, ClientInfo remote) {
        if(DiscmanExtrasMod.getServerInstance() == null || DiscmanExtrasMod.getServerInstance().overworld().dimensionType().defaultClock().isEmpty()) {
            return false;
        }
        DiscmanExtrasMod.getServerInstance().overworld().clockManager().setTotalTicks(
                DiscmanExtrasMod.getServerInstance().overworld().dimensionType().defaultClock().get(),
                time
        );
        return true;
    }
}
