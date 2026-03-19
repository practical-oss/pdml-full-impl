package dev.ps.pdml.data.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.leaf.TextLeaf;

public class NonNullTextNode extends NullableTextNode {


    private final @NotNull TextLeaf textLeaf;
    @Override
    public @NotNull TextLeaf textLeaf() { return textLeaf; }


    public NonNullTextNode (
        @NotNull NodeTag tag,
        @NotNull TextLeaf textLeaf,
        @Nullable TextRange location ) {

        super ( tag, location );

        this.textLeaf = textLeaf;
    }


    @Override
    public @NotNull String text() {
        return textLeaf.getText();
    }

    @Override
    public @Nullable TextRange textLocation() {
        return textLeaf.getTextLocation ();
    }
}
