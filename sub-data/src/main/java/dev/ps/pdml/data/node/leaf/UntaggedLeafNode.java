package dev.ps.pdml.data.node.leaf;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.node.Node;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public abstract class UntaggedLeafNode extends Node {


    protected @NotNull String text;
    public @NotNull String getText() { return text; }
    public void setText ( @NotNull String text ) {
        checkTextNotEmpty ( text );
        this.text = text;
    }


    protected UntaggedLeafNode (
        @NotNull String text,
        @Nullable TextRange textLocation ) {

        super ( textLocation );
        checkTextNotEmpty ( text );
        this.text = text;
    }


    public boolean isRootNode() { return false; }
    public boolean isTaggedNode () { return false; }
    public boolean isLeafNode() { return true; }


    @Override
    public @NotNull String toString() { return text; }


    private void checkTextNotEmpty ( @NotNull String text ) {

        if ( text.isEmpty() ) {
            throw new IllegalStateException ( "The node's text cannot be empty." );
        }
    }
}
