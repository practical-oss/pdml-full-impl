package dev.ps.pdml.ext.types;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.namespace.NodeNamespace;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pdml.reader.PdmlTokenReader;

import java.io.IOException;

public interface PdmlType<T> {


    record ObjectTokenPair<T> (
        @Nullable T object,
        // @Nullable TextToken textToken
        @Nullable TextRange textLocation
    ) {}


    NodeNamespace NAMESPACE = new NodeNamespace (
        "t", "https://www.pdml-lang.dev/extensions/types" );


    @NotNull String getName();

    boolean isNullAllowed();

    default @NotNull NodeTag nodeName() {
        return new NodeTag ( getName(), NAMESPACE.namePrefix() );
    }

    @NotNull ObjectTokenPair<T> parseObject (
        @NotNull PdmlParser pdmlParser ) throws IOException, PdmlException;

    void validateObject (
        @NotNull ObjectTokenPair<T> objectTokenPair ) throws InvalidPdmlDataException;

    void handleObject (
        @NotNull ObjectTokenPair<T> objectTokenPair,
        @Nullable TaggedNode parentNode,
        @NotNull PdmlTokenReader pdmlReader ) throws IOException;

    default void parseValidateAndHandleObject (
        @NotNull PdmlParser pdmlParser,
        @Nullable TaggedNode parentNode,
        boolean consumeNodeEnd ) throws IOException, PdmlException {

        @Nullable ObjectTokenPair<T> objectTokenPair = parseObject ( pdmlParser );

        PdmlTokenReader pdmlReader = pdmlParser.pdmlReader ();
        if ( ! pdmlReader.isAtNodeEnd() ) {
            throw new InvalidPdmlDataException (
                "End of node (" + CorePdmlConstants.NODE_END_CHAR + ") expected.",
                "NODE_END_REQUIRED",
                pdmlReader.currentTextPosition() );
        }
        if ( consumeNodeEnd ) {
            boolean ok = pdmlReader.skipNodeEnd ();
            assert ok;
        }

        validateObject ( objectTokenPair );
        handleObject ( objectTokenPair, parentNode, pdmlParser.pdmlReader () );
    }
}
