package dev.ps.pdml.utils.treewalker.handler;

import dev.ps.shared.text.range.TextPosition;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.namespace.NodeNamespaces;
import dev.ps.pdml.data.nodespec.PdmlNodeSpec;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.prt.argument.StringArguments;

public record TaggedNodeStartEvent(
    @Nullable TextPosition startPosition,
    @NotNull NodeTag tag,
    @Nullable NodeNamespaces declaredNamespaces,
    @Nullable StringArguments attributes,
    boolean isEmptyNode,
    @Nullable PdmlNodeSpec nodeSpec ) {


    // public @NotNull TextToken tagToken() { return tag.qualifiedTagToken(); }

    @Override
    public @NotNull String toString() { return tag + " start"; }
}
