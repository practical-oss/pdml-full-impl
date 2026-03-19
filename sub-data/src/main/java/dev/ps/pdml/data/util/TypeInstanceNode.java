package dev.ps.pdml.data.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.prt.type.AnyInstance;

public class TypeInstanceNode<N> {


    private final @NotNull NodeTag tag;
    public @NotNull NodeTag tag() { return tag; }

    private final @NotNull AnyInstance<N> typeInstance;
    public @NotNull AnyInstance<N> typeInstance() { return typeInstance; }

    private final @Nullable TextRange location;
    public @Nullable TextRange location() { return location; }


    public TypeInstanceNode (
        @NotNull NodeTag tag,
        @NotNull AnyInstance<N> typeInstance,
        @Nullable TextRange location ) {

        this.tag = tag;
        this.typeInstance = typeInstance;
        this.location = location;
    }
}
