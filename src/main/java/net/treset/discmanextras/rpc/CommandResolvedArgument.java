package net.treset.discmanextras.rpc;

import org.jspecify.annotations.NonNull;

public record CommandResolvedArgument<T>(
        String name,
        T value
) {
    @Override
    public @NonNull String toString() {
        if(value != null) return name + "=" + value;
        return name;
    }
}
