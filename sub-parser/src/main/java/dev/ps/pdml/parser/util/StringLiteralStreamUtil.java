package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.range.TextRange;
import dev.ps.shared.text.range.TextPosition;
import dev.ps.shared.text.reader.util.NullableParsedString;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.nulltype.NullInstance;
import dev.ps.prt.type.scalar.ScalarType;

import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StringLiteralStreamUtil {

    private static class TextSegmentSpliterator
        extends Spliterators.AbstractSpliterator<NullableParsedString<?>>
        implements Spliterator<NullableParsedString<?>> {


        private final PdmlParser pdmlParser;
        private final PdmlTokenReader pdmlReader;
        boolean isFirstElement = true;


        public TextSegmentSpliterator ( PdmlParser pdmlParser ) {

            super ( Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE );
            this.pdmlParser = pdmlParser;
            this.pdmlReader = pdmlParser.pdmlReader ();
        }


        @Override
        public boolean tryAdvance ( Consumer<? super NullableParsedString<?>> consumer ) {

            try {
                boolean whitespaceSkipped = pdmlReader.skipWhitespaceAndComments();
                if ( pdmlReader.isAtNodeEnd()
                    || pdmlReader.isAtNodeStart()
                    || pdmlReader.isAtEnd() ) {
                    return false;
                }

                if ( ! isFirstElement && ! whitespaceSkipped ) {
                    throw new MalformedPdmlException (
                        "Whitespace (e.g. a single space) is required to separate elements.",
                        "LIST_ELEMENT_SEPARATOR_REQUIRED",
                        pdmlReader.currentTextPosition() );
                }


                TextPosition elementStartPosition = pdmlReader.currentTextPosition();
                @Nullable String element = pdmlParser.parseEmptyableStringLiteralInTextLeaf();
                if ( element == null ) {
                    throw new MalformedPdmlException (
                        "Expecting a value. A value cannot start with '" + pdmlReader.currentCodePointAsString() + "'.",
                        "EXPECTING_COLLECTION_VALUE",
                        pdmlReader.currentTextPosition() );
                }
                if ( element.isEmpty() ) {
                    element = null;
                }

                NullableParsedString<?> string = new NullableParsedString<> ( element, elementStartPosition );
                consumer.accept ( string );

                isFirstElement = false;

                return true;

            } catch ( IOException | PdmlException e ) {
                throw new RuntimeException ( e.getMessage(), e );
            }
        }
    }


    // Streams

    public static @NotNull Stream<NullableParsedString<?>> textSegmentStream (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) {

        Stream<NullableParsedString<?>> stream = StreamSupport.stream (
            new TextSegmentSpliterator ( pdmlParser ), false );
        if ( allowNullValues ) {
            return stream;
        } else {
            return stream.filter ( textSegment -> {
                if ( textSegment.string () != null ) {
                    return true;
                } else {
                    throw new RuntimeException ( new InvalidPdmlDataException (
                        "Null or empty strings are not allowed.",
                        "INVALID_NULL_STRING",
                        textSegment.source() ) );
                }
            } );
        }
    }

    public static @NotNull Stream<String> stringStream (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) {

        return textSegmentStream ( pdmlParser, allowNullValues )
            .map ( NullableParsedString::string );
    }

    public static <N> @NotNull Stream<AnyInstance<N>> scalarInstanceStream (
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> elementType,
        boolean allowNullValues ) {

        return textSegmentStream ( pdmlParser, allowNullValues )
            .map ( textSegment -> {
                String text = textSegment.string ();
                TextRange location = textSegment.source();
                if ( text != null ) {
                    try {
                        return elementType.genericObjectToInstance ( text, location );
                    } catch ( InvalidDataException e ) {
                        throw new RuntimeException ( e.getMessage(), e );
                    }
                } else {
                    @SuppressWarnings ( "unchecked" )
                    AnyInstance<N> element = (AnyInstance<N>) NullInstance.create ( location );
                    return element;
                }
            } );
    }

    public static <N> @NotNull Stream<N> scalarStream (
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> elementType,
        boolean allowNullValues ) {

        return scalarInstanceStream ( pdmlParser, elementType, allowNullValues )
            .map ( AnyInstance::nativeObject );
    }


    // ForEach

    public static void forEachTextSegment (
        @NotNull ThrowableConsumer<? super NullableParsedString<?>> consumer,
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            textSegmentStream ( pdmlParser, allowNullValues ),
            consumer );
    }

    public static void forEachString (
        @NotNull ThrowableConsumer<? super String> consumer,
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            stringStream ( pdmlParser, allowNullValues ),
            consumer );
    }

    public static <N> void forEachScalarInstance (
        @NotNull ThrowableConsumer<? super AnyInstance<N>> consumer,
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> elementType,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            scalarInstanceStream ( pdmlParser, elementType, allowNullValues ),
            consumer );
    }

    public static <N> void forEachScalar (
        @NotNull ThrowableConsumer<? super N> consumer,
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> elementType,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            scalarStream ( pdmlParser, elementType, allowNullValues ),
            consumer );
    }
}
