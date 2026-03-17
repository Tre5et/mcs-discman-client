package net.treset.discmanextras.rpc;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcMethodBuilder;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import dev.treset.servermanagementextender.wrapper.enumeration.EnumTransformer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.treset.discmanextras.DiscmanExtrasMod;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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

    public static RpcCommand runCommand(MinecraftApi dispatcher, String command, ClientInfo remote) {
        DiscmanCommandOutput output = new DiscmanCommandOutput();
        try {
            DiscmanExtrasMod.getServerInstance().getCommands().getDispatcher().execute(
                    command,
                    new DiscmanCommandSource(
                            output,
                            Vec3.atCenterOf(DiscmanExtrasMod.getServerInstance().getRespawnData().pos()),
                            Vec2.ZERO,
                            DiscmanExtrasMod.getServerInstance().overworld(),
                            PermissionSet.ALL_PERMISSIONS,
                            "Discman",
                            Component.literal("Discman"),
                            DiscmanExtrasMod.getServerInstance(),
                            null
                    )
            );
        } catch (CommandSyntaxException e) {
            return RpcCommand.response(e.getMessage(), CommandStatus.FAILURE);
        }
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

    private static RpcCommand response(Component message, CommandStatus status) {
        return new RpcCommand(RpcText.of(message), status);
    }

    private static RpcCommand response(String message, CommandStatus status) {
        return new RpcCommand(RpcText.ofString(message), status);
    }

    private static class DiscmanCommandOutput implements CommandSource {
        private final Object responseLock = new Object();
        private boolean error = false;
        private Component response = null;

        public Object getResponseLock() {
            return responseLock;
        }

        public Component getResponse() {
            return response;
        }

        public boolean isError() {
            return error;
        }

        @Override
        public void sendSystemMessage(@NonNull Component message) {
            synchronized (responseLock) {
                this.response = message;
                responseLock.notify();
            }
            DiscmanExtrasMod.getServerInstance().sendSystemMessage(message);
        }

        @Override
        public boolean acceptsSuccess() {
            return DiscmanExtrasMod.getServerInstance().acceptsSuccess();
        }

        @Override
        public boolean acceptsFailure() {
            return DiscmanExtrasMod.getServerInstance().acceptsFailure();
        }

        @Override
        public boolean shouldInformAdmins() {
            return DiscmanExtrasMod.getServerInstance().shouldInformAdmins();
        }

        public void setError() {
            this.error = true;
        }
    }

    private static class DiscmanCommandSource extends CommandSourceStack {
        private final DiscmanCommandOutput output;

        public DiscmanCommandSource(DiscmanCommandOutput output, Vec3 pos, Vec2 rot, ServerLevel world, PermissionSet permission, String name, Component displayName, MinecraftServer server, @Nullable Entity entity) {
            super(output, pos, rot, world, permission, name, displayName, server, entity);
            this.output = output;
        }

        @Override
        public void sendFailure(@NonNull Component message) {
            output.setError();
            super.sendFailure(message);
        }
    }

    private enum CommandStatus {
        SUCCESS,
        FAILURE,
        NO_RESPONSE
    }
}
