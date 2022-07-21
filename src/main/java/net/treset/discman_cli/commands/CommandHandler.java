package net.treset.discman_cli.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.treset.discman_cli.networking.CommunicationManager;
import net.treset.discman_cli.networking.ConnectionManager;

public class CommandHandler {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment) {
        if(!environment.dedicated) return;
        dispatcher.register(CommandManager.literal("discman")
            .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("connection")
                    .executes(CommandHandler::executeStatus)
                    .then(CommandManager.literal("status")
                        .executes(CommandHandler::executeStatus))
                    .then(CommandManager.literal("establish")
                        .executes(CommandHandler::executeEstablishConnection))
                    .then(CommandManager.literal("close")
                        .executes(CommandHandler::executeCloseConnection)
                        .then(CommandManager.literal("force")
                            .executes(CommandHandler::executeCloseConnectionForce))
                    )
                )
                .then(CommandManager.literal("message")
                    .then(CommandManager.argument("msg", StringArgumentType.greedyString())
                        .executes(CommandHandler::executeMessage)))
        );
    }

    private static int executeStatus(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(Text.literal(ConnectionManager.isConnected()? String.format("A connection is open with the id %s.", ConnectionManager.getSessionId()) : "No connection is open."), true);
        return 1;
    }

    private static int executeEstablishConnection(CommandContext<ServerCommandSource> ctx) {
        new Thread(() -> {
            if(ConnectionManager.isConnected()) {
                ctx.getSource().sendFeedback(Text.literal("A connection is already opened. Close it first."), true);
                return;
            }
                ctx.getSource().sendFeedback(Text.literal(ConnectionManager.establishConnection() ? "Connection to discman established." : "An error occurred trying to connect to discman. Check that the discman is waiting for a connection and the port is set correctly."), true);
        }).start();
        return 1;
    }

    private static int executeCloseConnection(CommandContext<ServerCommandSource> ctx) {
        new Thread(() -> {
            if(!ConnectionManager.isConnected()) {
                ctx.getSource().sendFeedback(Text.literal("No connection is open. Cannot close anything."), true);
                return;
            }
            ctx.getSource().sendFeedback(Text.literal(ConnectionManager.closeConnection(false)? "Connection to discman closed." : "An error occurred trying to close the connection to discman. Please report this as a bug."), true);
        }).start();
        return 1;
    }

    private static int executeCloseConnectionForce(CommandContext<ServerCommandSource> ctx) {
        new Thread(() -> {
            if(!ConnectionManager.isConnected()) {
                ctx.getSource().sendFeedback(Text.literal("No connection is open. Cannot close anything."), true);
                return;
            }
            ctx.getSource().sendFeedback(Text.literal(ConnectionManager.closeConnection(true)? "Connection to discman closed." : "An error occurred trying to close the connection to discman. Please report this as a bug."), true);
        }).start();
        return 1;
    }

    private static int executeMessage(CommandContext<ServerCommandSource> ctx) {
        if(!ConnectionManager.isConnected()) {
            ctx.getSource().sendFeedback(Text.literal("The discman isn't connected. Can't send a message."), true);
            return 1;
        }

        String player = ctx.getSource().getName();
        String message = StringArgumentType.getString(ctx, "msg");
        boolean success = CommunicationManager.requestMessage(player + ": " + message);

        ctx.getSource().sendFeedback(Text.literal(success? String.format("Sent message '%s' to discman.", message) : "Error sending message. Try again."), true);

        return 1;
    }

}
