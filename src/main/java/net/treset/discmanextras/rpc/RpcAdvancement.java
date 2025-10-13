package net.treset.discmanextras.rpc;

import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.treset.discmanextras.wrapper.RpcNotificationBuilder;
import net.treset.discmanextras.wrapper.RpcNotificationHandler;
import net.treset.discmanextras.wrapper.RpcRegisterable;
import net.treset.discmanextras.wrapper.SchemaWrapper;

public record RpcAdvancement(
        RpcPlayer player,
        RpcText message,
        String identifier,
        RpcText title,
        RpcText description,
        RpcText toast,
        Integer color
) {
    public static SchemaWrapper<RpcAdvancement> WRAPPER;
    private static RpcNotificationHandler<RpcAdvancement> HANDLER;

    @RpcRegisterable
    public static void register() {
        WRAPPER = SchemaWrapper.<RpcAdvancement>builder("discman", "advancement")
                .property("player", SchemaWrapper.PLAYER, RpcAdvancement::player)
                .property("message", RpcText.WRAPPER, RpcAdvancement::message)
                .property("identifier", SchemaWrapper.STRING, RpcAdvancement::identifier)
                .property("title", RpcText.WRAPPER, RpcAdvancement::title)
                .property("description", RpcText.WRAPPER, RpcAdvancement::description)
                .property("toast", RpcText.WRAPPER, RpcAdvancement::toast)
                .property("color", SchemaWrapper.INTEGER, RpcAdvancement::color)
                .build(RpcAdvancement::new);

        HANDLER = RpcNotificationBuilder.of(WRAPPER)
                .description("Player got an advancement")
                .identifier("discman", "notification/players/advancement")
                .build();
    }

    public static RpcAdvancement of(ServerPlayerEntity player, AdvancementEntry advancement, Text message) {
        if(advancement.value().display().isEmpty()) {
            throw new IllegalStateException("Called advancement handler without display");
        }
        AdvancementDisplay display = advancement.value().display().get();
        return new RpcAdvancement(
                RpcPlayer.of(player),
                RpcText.of(message),
                advancement.id().toString(),
                RpcText.of(display.getTitle()),
                RpcText.of(display.getDescription()),
                RpcText.of(display.getFrame().getToastText()),
                display.getFrame().getTitleFormat().getColorValue()
        );
    }

    public static void handle(ServerPlayerEntity player, AdvancementEntry advancement, Text message) {
        HANDLER.send(RpcAdvancement.of(player, advancement, message));
    }
}
