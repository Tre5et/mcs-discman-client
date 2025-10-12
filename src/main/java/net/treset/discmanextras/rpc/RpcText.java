package net.treset.discmanextras.rpc;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.treset.discmanextras.wrapper.SchemaWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record RpcText(String literal, Optional<String> key, Optional<List<RpcText>> args) {
    public static SchemaWrapper<RpcText> WRAPPER;

    public static void register() {
        WRAPPER = SchemaWrapper.recursive("text", (b,s) -> b
                .property("literal", SchemaWrapper.STRING, RpcText::literal)
                .optionalProperty("key", SchemaWrapper.STRING, RpcText::key)
                .optionalProperty("args", s.asList(), RpcText::args)
                .build(RpcText::new)
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
