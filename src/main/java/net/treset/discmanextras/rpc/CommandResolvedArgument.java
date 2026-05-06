package net.treset.discmanextras.rpc;

import dev.treset.servermanagementextender.wrapper.ManagementSchema;
import dev.treset.servermanagementextender.wrapper.RpcOutgoingHandler;
import dev.treset.servermanagementextender.wrapper.ServerManagementInitialized;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

@ServerManagementInitialized
public record CommandResolvedArgument<T>(
        String name,
        T value
) {
    public static final ManagementSchema<CommandResolvedArgument<?>> WRAPPER = ManagementSchema.<CommandResolvedArgument<?>>builder("discman", "command_resolved_argument")
            .property("name", ManagementSchema.STRING, CommandResolvedArgument::name)
            .optionalProperty("value", ManagementSchema.STRING, a -> a.value() == null ? Optional.empty() : Optional.of(a.value().toString()))
            .build(CommandResolvedArgument::new);

    public static final RpcOutgoingHandler.RpcRespondingOutgoingHandler<List<CommandResolvedArgument<?>>, String> HANDLER = RpcOutgoingHandler.builder(WRAPPER.asList())
            .identifier("discman", "request/commands/execute")
            .description("Sends a command request to the discman server.")
            .withResponse(ManagementSchema.STRING)
            .build();

    @Override
    public @NonNull String toString() {
        if(value != null) return name + "=" + value;
        return name;
    }
}
