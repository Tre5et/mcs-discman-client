package net.treset.discmanextras.wrapper;

import com.mojang.serialization.Codec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.minecraft.server.dedicated.management.RpcRequestParameter;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.server.dedicated.management.schema.RpcSchemaEntry;
import net.minecraft.util.Identifier;
import net.treset.discmanextras.accessors.OutgoingRpcMethodBuilderAccessor;

public class RpcNotificationBuilder<T> {
    private final Codec<T> codec;
    private String name;
    private final RpcSchema schema;
    private Identifier identifier;
    private String description;

    private RpcNotificationBuilder(Codec<T> codec, String name, RpcSchema schema) {
        this.codec = codec;
        this.name = name;
        this.schema = schema;
    }

    public static <T> RpcNotificationBuilder<T> of(Codec<T> codec, RpcSchemaEntry schema) {
        return new RpcNotificationBuilder<>(codec, schema.name(), schema.schema());
    }

    public static <T> RpcNotificationBuilder<T> of(SchemaWrapper<T> wrapper) {
        return new RpcNotificationBuilder<>(wrapper.getCodec(), wrapper.getName(), wrapper.getSchema());
    }

    public RpcNotificationBuilder<T> identifier(String namespace, String path) {
        this.identifier = Identifier.of(namespace, path);
        return this;
    }

    public RpcNotificationBuilder<T> description(String description) {
        this.description = description;
        return this;
    }

    public RpcNotificationBuilder<T> propertyName(String name) {
        this.name = name;
        return this;
    }

    public RpcNotificationHandler<T> build() {
        if(identifier == null) {
            throw new IllegalStateException("Identifier is not set");
        }

        OutgoingRpcMethod.Builder<OutgoingRpcMethod.Notification<T>> builder = OutgoingRpcMethod.createNotificationBuilder(codec);
        if(description != null) {
            builder.description(description);
        }
        builder.requestParameter(new RpcRequestParameter(name, schema));

        RegistryEntry.Reference<? extends OutgoingRpcMethod<T, ?>> method = ((OutgoingRpcMethodBuilderAccessor<? extends OutgoingRpcMethod<T, ?>>)builder)
                .register(identifier);

        return new RpcNotificationHandler<>(method);
    }
}
