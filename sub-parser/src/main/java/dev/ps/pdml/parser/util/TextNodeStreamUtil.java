package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.util.NullableTextNode;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.argument.Argument;
import dev.ps.prt.argument.StringArgument;
import dev.ps.prt.type.scalar.ScalarType;

import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TextNodeStreamUtil {

    private static class TextNodeSpliterator
        extends Spliterators.AbstractSpliterator<NullableTextNode>
        implements Spliterator<NullableTextNode> {


        private final PdmlParser pdmlParser;
        private final PdmlTokenReader pdmlReader;


        public TextNodeSpliterator ( PdmlParser pdmlParser ) {

            super ( Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE );
            this.pdmlParser = pdmlParser;
            this.pdmlReader = pdmlParser.pdmlReader ();
        }


        @Override
        public boolean tryAdvance ( Consumer<? super NullableTextNode> consumer ) {

            try {
                pdmlReader.skipWhitespaceAndComments();
                // if ( pdmlReader.isAtNodeEnd() || pdmlReader.isAtEnd() ) {
                if ( ! pdmlReader.isAtNodeStart() ) {
                    return false;
                }

                @NotNull NullableTextNode node = pdmlParser.requireTextNode();
                consumer.accept ( node );
                return true;

            } catch ( IOException | PdmlException e ) {
                throw new RuntimeException ( e.getMessage(), e );
            }
        }
    }


    // Streams

    public static @NotNull Stream<NullableTextNode> textNodeStream (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) {

        Stream<NullableTextNode> stream = StreamSupport.stream (
            new TextNodeSpliterator ( pdmlParser ), false );
        if ( allowNullValues ) {
            return stream;
        } else {
            return stream.filter ( textNode -> {
                if ( textNode.text() != null ) {
                    return true;
                } else {
                    throw new RuntimeException ( new InvalidPdmlDataException (
                        "An empty text node (null) is not allowed.",
                        "INVALID_EMPTY_TEXT_NODE",
                        textNode.tagLocation() ) );
                }
            } );
        }
    }

    public static @NotNull Stream<StringArgument> stringArgumentStream (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) {

        return textNodeStream ( pdmlParser, allowNullValues )
            .map ( NullableTextNode::toStringArgument );
    }

    public static <N> @NotNull Stream<Argument<N>> scalarArgumentStream (
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> scalarType,
        boolean allowNullValues ) {

        /*
        return textNodeStream ( pdmlParser, allowNullValues )
            .map ( textNode -> {
                try {
                    return Argument.ofScalarOrNullType (
                        textNode.tagAsString(), textNode.text(),
                        scalarType, null,
                        textNode.tagLocation(), textNode.textLocation() );                 } catch ( InvalidDataException e ) {
                    throw new RuntimeException ( e.getMessage(), e );
                }
            } );
         */

        return stringArgumentStream ( pdmlParser, allowNullValues )
            .map ( stringArgument -> {
                try {
                    return Argument.of ( stringArgument, scalarType, null );
                } catch ( InvalidDataException e ) {
                    throw new RuntimeException ( e.getMessage (), e );
                }
            } );
    }

    // ForEach

    public static void forEachTextNode (
        @NotNull ThrowableConsumer<? super NullableTextNode> consumer,
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            textNodeStream ( pdmlParser, allowNullValues ),
            consumer );
    }

    public static void forEachStringArgument (
        @NotNull ThrowableConsumer<? super StringArgument> consumer,
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            stringArgumentStream ( pdmlParser, allowNullValues ),
            consumer );
    }

    public static <N> void forEachScalarArgument (
        @NotNull ThrowableConsumer<? super Argument<N>> consumer,
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> scalarType,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            scalarArgumentStream ( pdmlParser, scalarType, allowNullValues ),
            consumer );
    }
}
