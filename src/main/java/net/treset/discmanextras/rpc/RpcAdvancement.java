package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcNotificationHandler;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@ServerManagementInitialized
public record RpcAdvancement(
        RpcPlayer player,
        RpcText message,
        String identifier,
        RpcText title,
        RpcText description,
        RpcText toast,
        Integer color
) {
    public static final ManagementSchema<RpcAdvancement> WRAPPER = ManagementSchema.<RpcAdvancement>builder("discman", "advancement")
            .property("player", ManagementSchema.PLAYER, RpcAdvancement::player)
            .property("message", RpcText.WRAPPER, RpcAdvancement::message)
            .property("identifier", ManagementSchema.STRING, RpcAdvancement::identifier)
            .property("title", RpcText.WRAPPER, RpcAdvancement::title)
            .property("description", RpcText.WRAPPER, RpcAdvancement::description)
            .property("toast", RpcText.WRAPPER, RpcAdvancement::toast)
            .property("color", ManagementSchema.INTEGER, RpcAdvancement::color)
            .build(RpcAdvancement::new);

    private static final RpcNotificationHandler<RpcAdvancement> HANDLER = RpcNotificationHandler.builder(WRAPPER)
            .description("Player got an advancement")
            .identifier("discman", "notification/players/advancement")
            .build();

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
