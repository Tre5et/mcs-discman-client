package net.treset.discmanextras.rpc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.dedicated.management.UriUtil;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.server.dedicated.management.schema.RpcSchemaEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record RpcText(String literal, Optional<String> key, Optional<List<RpcText>> args) {
    public static Codec<RpcText> CODEC;
    public static RpcSchemaEntry SCHEMA;

    public static void register() {
        SCHEMA = new RpcSchemaEntry("text",
                UriUtil.createSchemasUri("text"),
                RpcSchema.ofObject()
                        .withProperty("literal", RpcSchema.STRING)
                        .withProperty("key", RpcSchema.STRING)
                        .withProperty("args", RpcSchema.ofReference(UriUtil.createSchemasUri("text")).asArray())
        );

        CODEC = Codec.recursive(
                "RpcText",
                self -> RecordCodecBuilder.create(instance ->
                        instance.group(
                                Codec.STRING.fieldOf("literal").forGetter(RpcText::literal),
                                Codec.STRING.optionalFieldOf("key").forGetter(RpcText::key),
                                Codec.list(self).optionalFieldOf("args").forGetter(RpcText::args)
                        ).apply(instance, RpcText::new)
                )
        );
    }

    public static RpcText of(Text message) {
        if(message.getContent() instanceof TranslatableTextContent content) {
            return ofTranslatable(message, content);
        }
        return ofString(message.getString());
    }

    public static RpcText ofTranslatable(Text message, TranslatableTextContent content) {
        List<RpcText> arguments = Arrays.stream(content.getArgs())
                .map(a -> {
                    if(a instanceof Text t) {
                        return t;
                    }
                    return Text.literal(a.toString());
                }).map(RpcText::of)
                .toList();

        return new RpcText(
                message.getString(),
                Optional.of(content.getKey()),
                Optional.of(arguments)
        );
    }

    public static RpcText ofString(String str) {
        return new RpcText(
                str,
                Optional.empty(),
                Optional.empty()
        );
    }
}
