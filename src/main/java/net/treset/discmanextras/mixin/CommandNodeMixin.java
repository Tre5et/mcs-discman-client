package net.treset.discmanextras.mixin;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.treset.discmanextras.accessor.CommandNodeAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CommandNode.class)
public abstract class CommandNodeMixin<S> implements CommandNodeAccessor<S> {
    @Accessor("children")
    public abstract Map<String, CommandNode<S>> getChildren();
    @Accessor("literals")
    public abstract Map<String, LiteralCommandNode<S>> getLiterals();
    @Accessor("arguments")
    public abstract Map<String, ArgumentCommandNode<S, ?>> getArguments();

    @Override
    public void putChild(CommandNode<S> node) {
        if (node instanceof RootCommandNode) {
            throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode");
        }

        getChildren().put(node.getName(), node);
        if (node instanceof LiteralCommandNode) {
            getLiterals().put(node.getName(), (LiteralCommandNode<S>) node);
        } else if (node instanceof ArgumentCommandNode) {
            getArguments().put(node.getName(), (ArgumentCommandNode<S, ?>) node);
        }
    }
}
