package net.treset.discmanextras.rpc;

import com.mojang.serialization.Codec;
import net.minecraft.server.dedicated.management.IncomingRpcMethod;
import net.minecraft.server.dedicated.management.RpcResponseResult;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.treset.discmanextras.DiscmanExtrasMod;
import net.treset.discmanextras.accessors.IncomingRpcMethodBuilderAccessor;

public class RpcGetTime {
    public static void register() {
        ((IncomingRpcMethodBuilderAccessor<IncomingRpcMethod.Parameterless<Integer>>)IncomingRpcMethod
                .createParameterlessBuilder(
                        RpcGetTime::getTime,
                        Codec.INT
                ).description("Get ingame time of day")
                .result(
                        new RpcResponseResult(
                                "time",
                                RpcSchema.INTEGER
                        )
                )
        ).buildAndRegisterDiscman("time");
    }

    public static int getTime(ManagementHandlerDispatcher dispatcher) {
        if(DiscmanExtrasMod.getServerInstance() == null || DiscmanExtrasMod.getServerInstance().getOverworld() == null) {
            return -1;
        }
        return (int)DiscmanExtrasMod.getServerInstance().getOverworld().getTimeOfDay();
    }
}
