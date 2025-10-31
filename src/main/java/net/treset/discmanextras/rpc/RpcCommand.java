package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcMethodBuilder;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.network.ManagementConnectionId;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.treset.discmanextras.DiscmanExtrasMod;
import org.jetbrains.annotations.Nullable;

@ServerManagementInitialized
public record RpcCommand(
        RpcText message,
        Boolean success
) {
    public static ManagementSchema<RpcCommand> SCHEMA = ManagementSchema.<RpcCommand>builder("discman", "command")
            .property("message", RpcText.WRAPPER, RpcCommand::message)
            .property("success", ManagementSchema.BOOLEAN, RpcCommand::success)
            .build(RpcCommand::new);

    static {
        RpcMethodBuilder.of(RpcCommand.SCHEMA)
                .parameter(ManagementSchema.STRING)
                .parameterName("command")
                .description("Runs a command on the server")
                .identifier("discman", "server/command/run")
                .build(RpcCommand::runCommand);
    }

    public static RpcCommand runCommand(ManagementHandlerDispatcher dispatcher, String command, ManagementConnectionId remote) {
        DiscmanCommandOutput output = new DiscmanCommandOutput();
        DiscmanExtrasMod.getServerInstance().getCommandManager().executeWithPrefix(
                new DiscmanCommandSource(
                        output,
                        DiscmanExtrasMod.getServerInstance().getSpawnWorld() == null ? Vec3d.ZERO : Vec3d.of(DiscmanExtrasMod.getServerInstance().getSpawnPoint().getPos()),
                        Vec2f.ZERO,
                        DiscmanExtrasMod.getServerInstance().getSpawnWorld(),
                        4,
                        "Discman",
                        Text.literal("Discman"),
                        DiscmanExtrasMod.getServerInstance(),
                        null
                ),
                command
        );
        synchronized (output.getResponseLock()) {
            if(output.getResponse() == null) {
                try {
                    output.getResponseLock().wait(2000);
                } catch (InterruptedException e) {
                    DiscmanExtrasMod.LOGGER.warn("Waiting for command response failed", e);
                }
            }
        }
        if(output.getResponse() == null) {
            return RpcCommand.failureResponse("Failed to get command response.");
        }
        return RpcCommand.response(output.getResponse(), !output.isError());
    }

    public static RpcCommand response(Text message, boolean success) {
        return new RpcCommand(RpcText.of(message), success);
    }

    public static RpcCommand failureResponse(String message) {
        return new RpcCommand(RpcText.ofString(message), false);
    }

    private static class DiscmanCommandOutput implements CommandOutput {
        private final Object responseLock = new Object();
        private boolean error = false;
        private Text response = null;

        public Object getResponseLock() {
            return responseLock;
        }

        public Text getResponse() {
            return response;
        }

        public boolean isError() {
            return error;
        }

        @Override
        public void sendMessage(Text message) {
            synchronized (responseLock) {
                this.response = message;
                responseLock.notify();
            }
            DiscmanExtrasMod.getServerInstance().sendMessage(message);
        }

        @Override
        public boolean shouldReceiveFeedback() {
            return DiscmanExtrasMod.getServerInstance().shouldReceiveFeedback();
        }

        @Override
        public boolean shouldTrackOutput() {
            return true;
        }

        @Override
        public boolean shouldBroadcastConsoleToOps() {
            return DiscmanExtrasMod.getServerInstance().shouldBroadcastConsoleToOps();
        }

        public void setError() {
            this.error = true;
        }
    }

    private static class DiscmanCommandSource extends ServerCommandSource {
        private final DiscmanCommandOutput output;

        public DiscmanCommandSource(DiscmanCommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String name, Text displayName, MinecraftServer server, @Nullable Entity entity) {
            super(output, pos, rot, world, level, name, displayName, server, entity);
            this.output = output;
        }

        @Override
        public void sendError(Text message) {
            output.setError();
            super.sendError(message);
        }
    }
}
