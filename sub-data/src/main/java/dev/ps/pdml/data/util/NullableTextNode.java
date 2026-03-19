package dev.ps.pdml.data.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.prt.argument.StringArgument;

public abstract class NullableTextNode {

    private final @NotNull NodeTag tag;
    public @NotNull NodeTag tag() { return tag; }

    public abstract @Nullable TextLeaf textLeaf();

    private final @Nullable TextRange location;
    public @Nullable TextRange location() { return location; }


    public NullableTextNode (
        @NotNull NodeTag tag,
        @Nullable TextRange location ) {

        this.tag = tag;
        this.location = location;
    }

    /*
    public TextOrNullNode (
        @NotNull String qualifiedTag,
        @Nullable TextPositionOrRange location ) {

        this (
            NodeTag.create ( qualifiedTag ),
            text != null ? new TextLeaf ( text, null ) : null );
    }
     */


    public @NotNull String tagAsString() { return tag.qualifiedTag(); }

    public abstract @Nullable String text();

    public @Nullable TextRange tagLocation() {
        return tag.tagPositionOrRange();
    }

    public abstract @Nullable TextRange textLocation();

    public @NotNull StringArgument toStringArgument() {
        return new StringArgument ( tagAsString(), text(), tagLocation(), textLocation() );
    }

    /* TODO needed?
    public @NotNull StringArgument toStringArgument() {
        return new StringArgument (
            tagAsString(), text(), tagLocation(), textLocation() );
    }
    */


    /*
    public @NotNull BranchNode toBranchNode() {

        BranchNode branchNode = new BranchNode ( tag );
        branchNode.appendChild ( textLeaf );
        return branchNode;
    }
     */


    // TODO? add asXXX methods from textLeaf (e.g. int asInt())
}
