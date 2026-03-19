package dev.ps.pdml.data.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.leaf.TextLeaf;

public class NullTextNode extends NullableTextNode {

    @Override
    public @Nullable TextLeaf textLeaf() { return null; }

    public NullTextNode (
        @NotNull NodeTag tag,
        @Nullable TextRange location ) {

        super ( tag, location );
    }


    @Override
    public @Nullable String text() {
        return null;
    }

    @Override
    public @Nullable TextRange textLocation() {
        return null;
    }
}
