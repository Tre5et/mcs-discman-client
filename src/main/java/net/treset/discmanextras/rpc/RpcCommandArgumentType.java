package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;

@ServerManagementInitialized
public enum RpcCommandArgumentType {
    LITERAL,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    STRING,
    GREEDY_STRING,
    WORD;

    public static final ManagementSchema<RpcCommandArgumentType> WRAPPER = ManagementSchema.ofEnum(RpcCommandArgumentType.class);
}
