package net.treset.discmanextras.wrapper;

import com.mojang.serialization.Codec;
import net.minecraft.server.dedicated.management.RpcKickReason;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.dedicated.management.UriUtil;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.server.dedicated.management.schema.RpcSchemaEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.BiFunction;

/**
 * A wrapper containing codec and schema required for RPC methods.
 * @param <T> The type of object the schema represents.
 */
public class SchemaWrapper<T> {
    private final Codec<T> codec;
    private final RpcSchema schema;
    private final String name;

    public SchemaWrapper(Codec<T> codec, RpcSchema schema, String name) {
        this.codec = codec;
        this.schema = schema;
        this.name = name;
    }

    public SchemaWrapper(Codec<T> codec, RpcSchemaEntry schema) {
        this.codec = codec;
        this.schema = schema == null ? null : schema.ref();
        this.name = schema == null ? null : schema.name();
    }

    public Codec<T> getCodec() {
        return codec;
    }

    public RpcSchema getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    /**
     * Converts this schema to a list schema of the same type.
     * @return The list schema of the same type.
     */
    public SchemaWrapper<List<T>> asList() {
        return new SchemaWrapper<>(codec == null ? null : Codec.list(codec), schema == null ? null : schema.asArray(), name);
    }

    /**
     * Creates a builder for an RPC schema extension.
     * @param identifier The identifier of the schema. Must be unique.
     * @return A RPC schema builder.
     * @param <T> The type of object this schema represents. Must sometimes be manually specified since the compiler can't always infer it correctly.
     */
    public static <T> RecordSchemaBuilder.RecordSchemaBuilder0<T> builder(Identifier identifier) {
        return new RecordSchemaBuilder.RecordSchemaBuilder0<>(identifier);
    }

    /**
     * Creates a builder for an RPC schema extension.
     * @param namespace The namespace the schema should be in.
     * @param name The name of the schema. Must be unique.
     * @return A RPC schema builder.
     * @param <T> The type of object this schema represents. Must sometimes be manually specified since the compiler can't always infer it correctly.
     */
    public static <T> RecordSchemaBuilder.RecordSchemaBuilder0<T> builder(String namespace, String name) {
        return builder(Identifier.of(namespace, name));
    }

    /**
     * Calls a function with a placeholder RPC schema and the accompanying schema builder to allow building schemas with recursive properties.
     * @param identifier The identifier of the schema. Must be unique.
     * @param builderFunction A function getting an RPC schema builder as the first parameter and a SchemaWrapper placeholder as the second parameter.
     *                        The SchemaWrapper parameter can be used to define recursive parameters by using it in the {@code parameter} or {@code optionalParameter} builder methods.
     *                        Must return a SchemaWrapper of the same type.
     * @return The constructed SchemaWrapper.
     * @param <T> The type of object this schema represents.
     */
    public static <T> SchemaWrapper<T> recursive(Identifier identifier, BiFunction<RecordSchemaBuilder.RecordSchemaBuilder0<T>, SchemaWrapper<T>, SchemaWrapper<T>> builderFunction) {
        SchemaWrapper<T> wrapper = builderFunction.apply(builder(identifier), new SchemaWrapper<>(null, RpcSchema.ofReference(UriUtil.createSchemasUri(identifier.toString())), identifier.toString()));
        Codec<T> codec = Codec.recursive(identifier.toString(), c -> builderFunction.apply(builder(identifier), new SchemaWrapper<>(c, null, identifier.toString())).getCodec());
        return new SchemaWrapper<>(codec, wrapper.getSchema(), identifier.toString());
    }

    /**
     * Calls a function with a placeholder RPC schema and the accompanying schema builder to allow building schemas with recursive properties.
     * @param namespace The namespace the schema should be in.
     * @param name The name of the schema.
     * @param builderFunction A function getting an RPC schema builder as the first parameter and a SchemaWrapper placeholder as the second parameter.
     *                        The SchemaWrapper parameter can be used to define recursive parameters by using it in the {@code parameter} or {@code optionalParameter} builder methods.
     *                        Must return a SchemaWrapper of the same type.
     * @return The constructed SchemaWrapper.
     * @param <T> The type of object this schema represents.
     */
    public static <T> SchemaWrapper<T> recursive(String namespace, String name, BiFunction<RecordSchemaBuilder.RecordSchemaBuilder0<T>, SchemaWrapper<T>, SchemaWrapper<T>> builderFunction) {
        return recursive(Identifier.of(namespace, name), builderFunction);
    }

    public static SchemaWrapper<Boolean> BOOLEAN = new SchemaWrapper<>(Codec.BOOL, RpcSchema.BOOLEAN, "boolean");
    public static SchemaWrapper<Integer> INTEGER = new SchemaWrapper<>(Codec.INT, RpcSchema.INTEGER, "integer");
    public static SchemaWrapper<String> STRING = new SchemaWrapper<>(Codec.STRING, RpcSchema.STRING, "string");
    public static SchemaWrapper<RpcPlayer> PLAYER = new SchemaWrapper<>(RpcPlayer.CODEC.codec(), RpcSchema.PLAYER);
    public static SchemaWrapper<RpcKickReason> MESSAGE = new SchemaWrapper<>(RpcKickReason.CODEC, RpcSchema.MESSAGE);
}
