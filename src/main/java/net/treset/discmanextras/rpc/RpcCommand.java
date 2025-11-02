package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcMethodBuilder;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import dev.treset.servermanagementextender.wrapper.enumeration.EnumTransformer;
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
        CommandStatus status
) {
    public static ManagementSchema<RpcCommand> SCHEMA = ManagementSchema.<RpcCommand>builder("discman", "command")
            .property("message", RpcText.WRAPPER, RpcCommand::message)
            .property("status", ManagementSchema.ofEnum(CommandStatus.class, EnumTransformer.snakeCaseLower()), RpcCommand::status)
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
                    output.getResponseLock().wait(1000);
                } catch (InterruptedException e) {
                    DiscmanExtrasMod.LOGGER.warn("Waiting for command response failed", e);
                }
            }
        }
        if(output.getResponse() == null) {
            return RpcCommand.response("No command response", CommandStatus.NO_RESPONSE);
        }
        return RpcCommand.response(output.getResponse(), output.isError() ? CommandStatus.FAILURE : CommandStatus.SUCCESS);
    }

    private static RpcCommand response(Text message, CommandStatus status) {
        return new RpcCommand(RpcText.of(message), status);
    }

    private static RpcCommand response(String message, CommandStatus status) {
        return new RpcCommand(RpcText.ofString(message), status);
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

    private enum CommandStatus {
        SUCCESS,
        FAILURE,
        NO_RESPONSE
    }
}
