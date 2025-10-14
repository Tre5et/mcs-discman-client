package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcMethodBuilder;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.network.ManagementConnectionId;
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

    public static int getTime(ManagementHandlerDispatcher dispatcher) {
        if(DiscmanExtrasMod.getServerInstance() == null || DiscmanExtrasMod.getServerInstance().getOverworld() == null) {
            return -1;
        }
        return (int)DiscmanExtrasMod.getServerInstance().getOverworld().getTimeOfDay();
    }

    public static Boolean setTime(ManagementHandlerDispatcher dispatcher, Integer time, ManagementConnectionId remote) {
        if(DiscmanExtrasMod.getServerInstance() == null || DiscmanExtrasMod.getServerInstance().getOverworld() == null) {
            return false;
        }
        DiscmanExtrasMod.getServerInstance().getOverworld().setTimeOfDay(time);
        return true;
    }
}
