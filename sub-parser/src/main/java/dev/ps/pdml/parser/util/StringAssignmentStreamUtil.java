package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.exception.PdmlException;
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

public class StringAssignmentStreamUtil {

    private static class StringAssignmentSpliterator
        extends Spliterators.AbstractSpliterator<StringArgument>
        implements Spliterator<StringArgument> {


        private final PdmlParser pdmlParser;
        private final PdmlTokenReader pdmlReader;
        boolean isFirstElement = true;


        public StringAssignmentSpliterator ( PdmlParser pdmlParser ) {

            super ( Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE );
            this.pdmlParser = pdmlParser;
            this.pdmlReader = pdmlParser.pdmlReader ();
        }


        @Override
        public boolean tryAdvance ( Consumer<? super StringArgument> consumer ) {

            try {
                boolean whitespaceSkipped = pdmlReader.skipWhitespaceAndComments();
                if ( pdmlReader.isAtNodeEnd()
                    // TODO should add flag to enable/disable
                    || pdmlReader.isAtChar ( PdmlExtensionsConstants.ATTRIBUTES_END_CHAR )
                    || pdmlReader.isAtEnd() ) {
                    return false;
                }

                if ( ! isFirstElement && ! whitespaceSkipped ) {
                    throw new MalformedPdmlException (
                        "Whitespace (e.g. a single space) is required to separate assignments.",
                        "ASSIGNMENT_SEPARATOR_REQUIRED",
                        pdmlReader.currentTextPosition() );
                }

                // TODO use other version for text leaf (escape chars)
                @Nullable StringArgument stringArgument = pdmlParser.parseAttribute();
                if ( stringArgument == null ) {
                    throw new MalformedPdmlException (
                        "Invalid character '" + pdmlReader.currentCodePointAsString() + "'. Expecting an assignment (e.g. name = value).",
                        "EXPECTING_ASSIGNMENT",
                        pdmlReader.currentTextPosition() );
                }

                consumer.accept ( stringArgument );

                isFirstElement = false;

                return true;

            } catch ( IOException | PdmlException e ) {
                throw new RuntimeException ( e.getMessage(), e );
            }
        }
    }


    // Streams

    public static @NotNull Stream<StringArgument> stringArgumentStream (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) {

        Stream<StringArgument> stream = StreamSupport.stream (
            new StringAssignmentSpliterator ( pdmlParser ), false );
        if ( allowNullValues ) {
            return stream;
        } else {
            return stream.filter ( stringArgument -> {
                if ( stringArgument.value() != null ) {
                    return true;
                } else {
                    throw new RuntimeException ( new InvalidPdmlDataException (
                        "Assigning null or an empty string is not allowed.",
                        "INVALID_NULL_ASSIGNMENT",
                        stringArgument.valueOrNameLocation() ) );
                }
            } );
        }
    }

    public static <N> @NotNull Stream<Argument<N>> scalarArgumentStream (
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> valueType,
        boolean allowNullValues ) {

        return stringArgumentStream ( pdmlParser, allowNullValues )
            .map ( stringArgument -> {
                try {
                    return Argument.of ( stringArgument, valueType, null );
                } catch ( InvalidDataException e ) {
                    throw new RuntimeException ( e.getMessage(), e );
                }
            } );
    }


    // ForEach

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
        @NotNull ScalarType<N> valueType,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            scalarArgumentStream ( pdmlParser, valueType, allowNullValues ),
            consumer );
    }
}
