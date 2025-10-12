package net.treset.discmanextras.wrapper;

import com.mojang.serialization.Codec;
import net.minecraft.server.dedicated.management.RpcKickReason;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.dedicated.management.UriUtil;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.server.dedicated.management.schema.RpcSchemaEntry;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

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
        this.schema = schema.schema();
        this.name = schema.name();
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

    public SchemaWrapper<List<T>> asList() {
        return new SchemaWrapper<>(Codec.list(codec), schema.asArray(), name);
    }

    public static <T> RecordSchemaBuilder.RecordSchemaBuilder0<T> builder(String name) {
        return new RecordSchemaBuilder.RecordSchemaBuilder0<>(name);
    }

    public static <T> SchemaWrapper<T> recursive(String name, BiFunction<RecordSchemaBuilder.RecordSchemaBuilder0<T>, SchemaWrapper<T>, SchemaWrapper<T>> builder) {
        AtomicReference<RpcSchema> schema = new AtomicReference<>();
        Codec<T> codec = Codec.recursive(name, c -> {
            SchemaWrapper<T> res = builder.apply(builder(name), new SchemaWrapper<>(c, RpcSchema.ofReference(UriUtil.createSchemasUri(name)), name));
            schema.set(res.getSchema());
            return res.getCodec();
        });
        return new SchemaWrapper<>(codec, schema.get(), name);
    }

    public static SchemaWrapper<Boolean> BOOLEAN = new SchemaWrapper<>(Codec.BOOL, RpcSchema.BOOLEAN, "boolean");
    public static SchemaWrapper<Integer> INTEGER = new SchemaWrapper<>(Codec.INT, RpcSchema.INTEGER, "integer");
    public static SchemaWrapper<String> STRING = new SchemaWrapper<>(Codec.STRING, RpcSchema.STRING, "string");
    public static SchemaWrapper<RpcPlayer> PLAYER = new SchemaWrapper<>(RpcPlayer.CODEC.codec(), RpcSchema.PLAYER.schema(), "player");
    public static SchemaWrapper<RpcKickReason> KICK_REASON = new SchemaWrapper<>(RpcKickReason.CODEC, RpcSchema.MESSAGE.schema(), "kick_player");
}
