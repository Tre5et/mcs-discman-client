package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcNotificationHandler;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@ServerManagementInitialized
public record RpcDeath(RpcPlayer player, RpcText message) {
    public static final ManagementSchema<RpcDeath> WRAPPER = ManagementSchema.<RpcDeath>builder("discman", "death")
            .property("player", ManagementSchema.PLAYER, RpcDeath::player)
            .property("message", RpcText.WRAPPER, RpcDeath::message)
            .build(RpcDeath::new);

    private static final RpcNotificationHandler<RpcDeath> HANDLER = RpcNotificationHandler.builder(WRAPPER)
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
