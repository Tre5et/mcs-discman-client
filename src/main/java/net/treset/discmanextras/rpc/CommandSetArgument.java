package net.treset.discmanextras.rpc;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.Optional;

public record CommandSetArgument<T>(
        String name,
        Optional<Class<T>> expectedType
) {
    public CommandResolvedArgument<T> resolve(CommandContext<CommandSourceStack> ctx) {
        return new CommandResolvedArgument<>(
                name,
                expectedType.map(type -> ctx.getArgument(name, type)).orElse(null)
        );
    }
}
