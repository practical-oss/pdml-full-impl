package dev.ps.pdml.utils.treewalker.handler;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.range.TextPosition;

public record TextEvent(
    @NotNull String text,
    @Nullable TextPosition location,
    @NotNull TaggedNodeStartEvent startEvent ) {

    @Override
    public @NotNull String toString() { return text; }
}

/*
public record TextLeafEvent(
    @NotNull TextLeaf textLeaf,
    @NotNull BranchNode parentNode ) {

    @Override
    public String toString() { return textLeaf.getText(); }
}
 */


