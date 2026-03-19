package dev.ps.pdml.utils.treewalker.handler;

import dev.ps.shared.text.range.TextPosition;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public record CommentEvent(
    @NotNull String comment,
    @Nullable TextPosition position,
    @NotNull TaggedNodeStartEvent startEvent ) {

    public @NotNull String commentWithoutDelimiters() {
        return CommentLeaf.removeDelimiters ( comment );
    }

    @Override
    public @NotNull String toString() { return commentWithoutDelimiters(); }
}

/*
public record CommentLeafEvent(
    @NotNull CommentLeaf commentLeaf,
    @NotNull BranchNode parentNode ) {

    @Override
    public String toString() { return commentLeaf.getText(); }
}

 */
