package net.treset.discmanextras.rpc;

import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.treset.discmanextras.wrapper.RpcNotificationBuilder;
import net.treset.discmanextras.wrapper.RpcNotificationHandler;
import net.treset.discmanextras.wrapper.ServerManagementInitialized;
import net.treset.discmanextras.wrapper.SchemaWrapper;

@ServerManagementInitialized
public record RpcDeath(RpcPlayer player, RpcText message) {
    public static final SchemaWrapper<RpcDeath> WRAPPER = SchemaWrapper.<RpcDeath>builder("discman", "death")
            .property("player", SchemaWrapper.PLAYER, RpcDeath::player)
            .property("message", RpcText.WRAPPER, RpcDeath::message)
            .build(RpcDeath::new);

    private static final RpcNotificationHandler<RpcDeath> HANDLER = RpcNotificationBuilder.of(WRAPPER)
            .description("Player died")
            .identifier("discman", "notification/players/death")
            .build();

    public static RpcDeath of(ServerPlayerEntity player, Text message) {
        return new RpcDeath(
                RpcPlayer.of(player),
                RpcText.of(message)
        );
    }

    public static void handle(ServerPlayerEntity player, Text message) {
        HANDLER.send(RpcDeath.of(player, message));
    }
}
