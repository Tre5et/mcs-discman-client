package net.treset.discmanextras.rpc;

import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.treset.discmanextras.wrapper.RpcNotificationBuilder;
import net.treset.discmanextras.wrapper.RpcNotificationHandler;
import net.treset.discmanextras.wrapper.RpcRegisterable;
import net.treset.discmanextras.wrapper.SchemaWrapper;

public record RpcDeath(RpcPlayer player, RpcText message) {
    public static SchemaWrapper<RpcDeath> WRAPPER;
    public static RpcNotificationHandler<RpcDeath> HANDLER;

    @RpcRegisterable
    public static void register() {
        WRAPPER = SchemaWrapper.<RpcDeath>builder("death")
                .property("player", SchemaWrapper.PLAYER, RpcDeath::player)
                .property("message", RpcText.WRAPPER, RpcDeath::message)
                .build(RpcDeath::new);

        HANDLER = RpcNotificationBuilder.of(WRAPPER)
                .identifier("discman", "notification/player/death")
                .description("Player died")
                .build();
    }

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
