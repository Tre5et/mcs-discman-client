package net.treset.discmanextras.wrapper;

import com.mojang.serialization.Codec;
import net.minecraft.server.dedicated.management.IncomingRpcMethod;
import net.minecraft.server.dedicated.management.RpcRequestParameter;
import net.minecraft.server.dedicated.management.RpcResponseResult;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.server.dedicated.management.schema.RpcSchemaEntry;
import net.minecraft.util.Identifier;
import net.treset.discmanextras.accessors.IncomingRpcMethodBuilderAccessor;

import java.util.function.Function;

public abstract class RpcMethodBuilder<R> {
    protected String name;
    protected final Codec<R> codec;
    protected final RpcSchema schema;
    protected Identifier identifier;
    protected String description;

    private RpcMethodBuilder(String name, Codec<R> codec, RpcSchema schema) {
        this.name = name;
        this.codec = codec;
        this.schema = schema;
    }

    public static <R> RpcParameterlessMethodBuilder<R> of(Codec<R> codec, RpcSchemaEntry schema) {
        return new RpcParameterlessMethodBuilder<>(schema.name(), codec, schema.schema());
    }

    public static <R> RpcParameterlessMethodBuilder<R> of(SchemaWrapper<R> wrapper) {
        return new RpcParameterlessMethodBuilder<>(wrapper.getName(), wrapper.getCodec(), wrapper.getSchema());
    }

    public static class RpcParameterlessMethodBuilder<R> extends RpcMethodBuilder<R> {
        private RpcParameterlessMethodBuilder(String name, Codec<R> codec, RpcSchema schema) {
            super(name, codec, schema);
        }

        public RpcParameterlessMethodBuilder<R> resultPropertyName(String name) {
            this.name = name;
            return this;
        }

        public RpcParameterlessMethodBuilder<R> identifier(String namespace, String path) {
            this.identifier = Identifier.of(namespace, path);
            return this;
        }

        public RpcParameterlessMethodBuilder<R> description(String description) {
            this.description = description;
            return this;
        }

        public <T> RpcParametrizedMethodBuilder<T,R> parameter(SchemaWrapper<T> wrapper) {
            return new RpcParametrizedMethodBuilder<>(
                    name,
                    codec,
                    schema,
                    identifier,
                    description,
                    wrapper.getName(),
                    wrapper.getCodec(),
                    wrapper.getSchema()
            );
        }

        public IncomingRpcMethod.Parameterless<R> build(Function<ManagementHandlerDispatcher, R> handler) {
            if(identifier == null) {
                throw new IllegalStateException("Identifier is not set");
            }

            IncomingRpcMethod.Builder<IncomingRpcMethod.Parameterless<R>> builder = IncomingRpcMethod.createParameterlessBuilder(
                    handler,
                    codec
            ).result(
                    new RpcResponseResult(name, schema)
            );

            if(description != null) {
                builder = builder.description(description);
            }

            return ((IncomingRpcMethodBuilderAccessor<IncomingRpcMethod.Parameterless<R>>)builder)
                    .register(identifier);
        }
    }

    public static class RpcParametrizedMethodBuilder<T,R> extends RpcMethodBuilder<R> {
        private String parameterName;
        private final Codec<T> parameterCodec;
        private final RpcSchema parameterSchema;

        private RpcParametrizedMethodBuilder(String name, Codec<R> codec, RpcSchema schema, Identifier identifier, String description, String parameterName, Codec<T> parameterCodec, RpcSchema parameterSchema) {
            super(name, codec, schema);
            this.identifier = identifier;
            this.description = description;
            this.parameterCodec = parameterCodec;
            this.parameterSchema = parameterSchema;
            this.parameterName = parameterName;
        }

        public RpcParametrizedMethodBuilder<T,R> resultPropertyName(String name) {
            this.name = name;
            return this;
        }

        public RpcParametrizedMethodBuilder<T,R> parameterName(String name) {
            this.parameterName = name;
            return this;
        }

        public RpcParametrizedMethodBuilder<T,R> identifier(String namespace, String path) {
            this.identifier = Identifier.of(namespace, path);
            return this;
        }

        public RpcParametrizedMethodBuilder<T,R> description(String description) {
            this.description = description;
            return this;
        }

        public IncomingRpcMethod.Parameterized<T,R> build(IncomingRpcMethod.ParameterizedHandler<T,R> handler) {
            if(identifier == null) {
                throw new IllegalStateException("Identifier is not set");
            }

            IncomingRpcMethod.Builder<IncomingRpcMethod.Parameterized<T,R>> builder = IncomingRpcMethod.createParameterizedBuilder(
                    handler,
                    parameterCodec,
                    codec
            ).parameter(
                    new RpcRequestParameter(
                            parameterName,
                            parameterSchema
                    )
            ).result(
                    new RpcResponseResult(name, schema)
            );

            if(description != null) {
                builder = builder.description(description);
            }

            return ((IncomingRpcMethodBuilderAccessor<IncomingRpcMethod.Parameterized<T,R>>)builder)
                    .register(identifier);
        }
    }
}
