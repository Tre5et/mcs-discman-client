package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcOutgoingHandler;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.level.ServerPlayer;

@ServerManagementInitialized
public record RpcAdvancement(
        PlayerDto player,
        RpcText message,
        String identifier,
        RpcText title,
        RpcText description,
        RpcText toast,
        String color
) {
    public static final ManagementSchema<RpcAdvancement> WRAPPER = ManagementSchema.<RpcAdvancement>builder("discman", "advancement")
            .property("player", ManagementSchema.PLAYER, RpcAdvancement::player)
            .property("message", RpcText.WRAPPER, RpcAdvancement::message)
            .property("identifier", ManagementSchema.STRING, RpcAdvancement::identifier)
            .property("title", RpcText.WRAPPER, RpcAdvancement::title)
            .property("description", RpcText.WRAPPER, RpcAdvancement::description)
            .property("toast", RpcText.WRAPPER, RpcAdvancement::toast)
            .property("color", ManagementSchema.STRING, RpcAdvancement::color)
            .build(RpcAdvancement::new);

    private static final RpcOutgoingHandler.RpcResponselessOutgoingHandler<RpcAdvancement> HANDLER = RpcOutgoingHandler.builder(WRAPPER)
            .description("Player got an advancement")
            .identifier("discman", "notification/players/advancement")
            .build();

    public static RpcAdvancement of(ServerPlayer player, AdvancementHolder advancement, Component message) {
        if(advancement.value().display().isEmpty()) {
            throw new IllegalStateException("Called advancement handler without display");
        }
        DisplayInfo display = advancement.value().display().get();
        return new RpcAdvancement(
                PlayerDto.from(player),
                RpcText.of(message),
                advancement.id().toString(),
                RpcText.of(display.getTitle()),
                RpcText.of(display.getDescription()),
                RpcText.of(display.getType().getDisplayName()),
                display.getType().getChatColor().toString()
        );
    }

    public static void handle(ServerPlayer player, AdvancementHolder advancement, Component message) {
        HANDLER.send(RpcAdvancement.of(player, advancement, message));
    }
}
