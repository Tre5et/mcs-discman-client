package net.treset.discmanextras.accessor;

import com.mojang.brigadier.tree.CommandNode;

public interface CommandNodeAccessor<S> {
    void putChild(CommandNode<S> node);
}
