package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcOutgoingHandler;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import net.minecraft.network.chat.Component;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.level.ServerPlayer;

@ServerManagementInitialized
public record RpcDeath(PlayerDto player, RpcText message) {
    public static final ManagementSchema<RpcDeath> WRAPPER = ManagementSchema.<RpcDeath>builder("discman", "death")
            .property("player", ManagementSchema.PLAYER, RpcDeath::player)
            .property("message", RpcText.WRAPPER, RpcDeath::message)
            .build(RpcDeath::new);

    private static final RpcOutgoingHandler.RpcResponselessOutgoingHandler<RpcDeath> HANDLER = RpcOutgoingHandler.builder(WRAPPER)
            .description("Player died")
            .identifier("discman", "notification/players/death")
            .build();

    public static RpcDeath of(ServerPlayer player, Component message) {
        return new RpcDeath(
                PlayerDto.from(player),
                RpcText.of(message)
        );
    }

    public static void handle(ServerPlayer player, Component message) {
        HANDLER.send(RpcDeath.of(player, message));
    }
}
