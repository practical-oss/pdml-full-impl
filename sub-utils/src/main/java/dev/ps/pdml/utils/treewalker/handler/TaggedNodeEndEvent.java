package dev.ps.pdml.utils.treewalker.handler;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.range.TextPosition;

public record TaggedNodeEndEvent(

    @Nullable TextPosition position,
    @NotNull TaggedNodeStartEvent startEvent ) {

    // public boolean isEmptyNode() { return startEvent.isEmptyNode(); }

    @Override
    public String toString() { return startEvent.tag () + " end"; }
}

/*
public record BranchNodeEndEvent(
    @NotNull BranchNode node ) {

    @Override
    public String toString() { return node.getName() + " end"; }
}

 */
