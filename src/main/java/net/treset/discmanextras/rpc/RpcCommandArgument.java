package net.treset.discmanextras.rpc;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcMethodBuilder;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.treset.discmanextras.DiscmanExtrasMod;
import net.treset.discmanextras.accessor.CommandNodeAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@ServerManagementInitialized
public record RpcCommandArgument(
        String name,
        Optional<RpcCommandArgumentType> type,
        Optional<Boolean> allowAction,
        Optional<List<RpcCommandArgument>> children,
        Optional<Double> min,
        Optional<Double> max
) {
    public static final ManagementSchema<RpcCommandArgument> WRAPPER = ManagementSchema.recursive("discman", "command_argument", (b, s) -> b
            .property("name", ManagementSchema.STRING, RpcCommandArgument::name)
            .optionalProperty("type", RpcCommandArgumentType.WRAPPER, RpcCommandArgument::type)
            .optionalProperty("allowAction", ManagementSchema.BOOLEAN, RpcCommandArgument::allowAction)
            .optionalProperty("children", s.asList(), RpcCommandArgument::children)
            .optionalProperty("min", ManagementSchema.DOUBLE, RpcCommandArgument::min)
            .optionalProperty("max", ManagementSchema.DOUBLE, RpcCommandArgument::max)
            .build(RpcCommandArgument::new)
        );

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void build(ArgumentBuilder<CommandSourceStack, ?> builder, List<CommandSetArgument<?>> before, BiFunction<CommandContext<CommandSourceStack>,  List<CommandSetArgument<?>>, Integer> onExecute) {
        ArgumentBuilder<CommandSourceStack, ?> newBuilder = type.map(rpcCommandArgumentType -> switch (rpcCommandArgumentType) {
            case LITERAL -> Commands.literal(name);
            case INTEGER -> Commands.argument(name,
                    min.isPresent() && max.isPresent() ? IntegerArgumentType.integer(min.get().intValue(), max.get().intValue())
                            : min.map(aDouble -> IntegerArgumentType.integer(aDouble.intValue())).orElseGet(IntegerArgumentType::integer)
            );
            case DOUBLE -> Commands.argument(name,
                    min.isPresent() && max.isPresent() ? DoubleArgumentType.doubleArg(min.get(), max.get())
                            : min.map(DoubleArgumentType::doubleArg).orElseGet(DoubleArgumentType::doubleArg)
            );
            case BOOLEAN -> Commands.argument(name, BoolArgumentType.bool());
            case STRING -> Commands.argument(name, StringArgumentType.string());
            case GREEDY_STRING -> Commands.argument(name, StringArgumentType.greedyString());
            case WORD -> Commands.argument(name, StringArgumentType.word());
        }).orElseGet(() -> Commands.literal(name));

        Optional<Class<?>> parameterClass = type.flatMap(rpcCommandArgumentType -> switch (rpcCommandArgumentType) {
            case LITERAL -> Optional.empty();
            case INTEGER -> Optional.of(Integer.class);
            case DOUBLE -> Optional.of(Double.class);
            case BOOLEAN -> Optional.of(Boolean.class);
            case STRING, GREEDY_STRING, WORD -> Optional.of(String.class);
        });

        List<CommandSetArgument<?>> current = new ArrayList<>(before);
        current.add(new CommandSetArgument(name, parameterClass));

        if(allowAction.isPresent() && allowAction.get()) {
            newBuilder = newBuilder.executes(ctx -> onExecute.apply(ctx, current));
        }

        if(children.isPresent()) {
            for (RpcCommandArgument arg : children.get()) {
                arg.build(newBuilder, current, onExecute);
            }
        }

        builder.then(newBuilder);
    }

    static {
        RpcMethodBuilder.of(ManagementSchema.BOOLEAN)
                .responsePropertyName("success")
                .parameter(WRAPPER.asList())
                .parameterName("commands")
                .description("Set the available commands")
                .identifier("discman", "commands/set")
                .build(RpcCommandArgument::registerCommands);
    }

    @SuppressWarnings("unchecked")
    private static Boolean registerCommands(MinecraftApi dispatcher, List<RpcCommandArgument> commands, ClientInfo remote) {
        for(RpcCommandArgument command : commands) {
            if(command.type().isPresent() && command.type().get() != RpcCommandArgumentType.LITERAL) {
                DiscmanExtrasMod.LOGGER.warn("Invalid command registered; command identifier is not literal but {}", command.type());
                return false;
            }
            LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(command.name());
            if(command.children().isPresent()) {
                for (RpcCommandArgument argument : command.children().get()) {
                    argument.build(builder, List.of(new CommandSetArgument<Void>(command.name(), Optional.empty())), RpcCommandArgument::send);
                }
            }
            DiscmanExtrasMod.dispatcher.register(builder);
            ((CommandNodeAccessor<CommandSourceStack>)DiscmanExtrasMod.dispatcher.getRoot()).putChild(builder.build());
            DiscmanExtrasMod.LOGGER.info("Registered discman command {}", command.name());
        }
        return true;
    }

    private static Integer send(CommandContext<CommandSourceStack> ctx, List<CommandSetArgument<?>> arguments) {
        List<? extends CommandResolvedArgument<?>> resolved = arguments.stream()
                .map(a -> a.resolve(ctx))
                .toList();

        // TODO: output
        ctx.getSource().sendSuccess(() -> Component.literal(resolved.toString()), true);

        return 1;
    }
}
