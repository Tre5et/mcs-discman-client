package net.treset.discmanextras.rpc;

import com.mojang.serialization.Codec;
import net.minecraft.server.dedicated.management.IncomingRpcMethod;
import net.minecraft.server.dedicated.management.RpcRequestParameter;
import net.minecraft.server.dedicated.management.RpcResponseResult;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.network.ManagementConnectionId;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.treset.discmanextras.DiscmanExtrasMod;
import net.treset.discmanextras.accessors.IncomingRpcMethodBuilderAccessor;

public class RpcSetTime {
    public static void register() {
        ((IncomingRpcMethodBuilderAccessor<IncomingRpcMethod.Parameterless<Integer>>)IncomingRpcMethod
                .createParameterizedBuilder(
                        RpcSetTime::setTime,
                        Codec.INT,
                        Codec.BOOL
                ).description("Set ingame time of day")
                .parameter(
                        new RpcRequestParameter(
                                "time",
                                RpcSchema.INTEGER
                        )
                )
                .result(
                        new RpcResponseResult(
                                "success",
                                RpcSchema.BOOLEAN
                        )
                )
        ).buildAndRegisterDiscman("time/set");
    }

    public static Boolean setTime(ManagementHandlerDispatcher dispatcher, Integer time, ManagementConnectionId remote) {
        if(DiscmanExtrasMod.getServerInstance() == null || DiscmanExtrasMod.getServerInstance().getOverworld() == null) {
            return false;
        }
        DiscmanExtrasMod.getServerInstance().getOverworld().setTimeOfDay(time);
        return true;
    }
}
