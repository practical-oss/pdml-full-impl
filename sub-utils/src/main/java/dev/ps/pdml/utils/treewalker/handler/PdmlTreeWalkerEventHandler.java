package dev.ps.pdml.utils.treewalker.handler;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;

public interface PdmlTreeWalkerEventHandler<N, R> {

    // TODO? add continuationKind in all events (CONTINUE, ABORT, SKIP, SKIP_SIBLINGS)

    default void onStart() throws IOException, PdmlException {}
    default void onEnd() throws IOException, PdmlException {}

    @NotNull N onRootNodeStart ( @NotNull TaggedNodeStartEvent event ) throws IOException, PdmlException;
    default void onRootNodeEnd ( @NotNull TaggedNodeEndEvent event, @NotNull N rootNode ) throws IOException, PdmlException {}

    @NotNull N onTaggedNodeStart ( @NotNull TaggedNodeStartEvent event, @NotNull N parentNode ) throws IOException, PdmlException;
    default void onTaggedNodeEnd ( @NotNull TaggedNodeEndEvent event, @NotNull N node ) throws IOException, PdmlException {}

    default void onText ( @NotNull TextEvent event, @NotNull N parentNode ) throws IOException, PdmlException {}
    default void onComment ( @NotNull CommentEvent event, @NotNull N parentNode ) throws IOException, PdmlException {}

    @Nullable R getResult() throws IOException, PdmlException;
}
