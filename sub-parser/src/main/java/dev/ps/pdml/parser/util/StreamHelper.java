package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.range.TextRange;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

public class StreamHelper {

    // Unwrap unchecked exceptions to checked exceptions
    public static <T> void forEachT (
        @NotNull Stream<T> stream,
        @NotNull ThrowableConsumer<? super T> consumer ) throws IOException, InvalidDataException {

        try {
            // stream.forEach ( consumer );
            stream.forEachOrdered ( consumer );

        } catch ( RuntimeException e ) {
            Throwable cause = e.getCause();
            if ( cause instanceof IOException ioException ) {
                throw ioException;
            } else if ( cause instanceof InvalidDataException invalidDataException ) {
                throw invalidDataException;
            } else {
                throw e;
            }
        }
    }

    public static <T> @Nullable T parseReaderResource (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean hasRootNode,
        @NotNull ThrowableFunction<PdmlParser,T> resultSupplier ) throws IOException, InvalidDataException {

        // try ( Reader reader = readerResource.newReader() ) {

        PdmlParser pdmlParser = PdmlParser.create ( readerResource, parserConfig );

        NodeTag rootNodeTag = null;
        if ( hasRootNode ) {
            pdmlParser.skipWhitespaceBeforeRootNode();
            // pdmlParser.requireTaggedNodeStartAndTagAndSeparator ();

            rootNodeTag = pdmlParser.requireFromNodeStartToTag();
            pdmlParser.requireSeparator();
        }

        T result = resultSupplier.applyOrThrow ( pdmlParser );

        if ( hasRootNode ) {
            // pdmlParser.requireNodeEnd();
            pdmlParser.requireTaggedNodeEnd ( new TaggedNode ( rootNodeTag ) );
            pdmlParser.requireDocumentEnd();
        }

        return result;
        // }
    }

    public static void checkUniqueKey (
        @NotNull Map<String,?> map,
        @NotNull String key,
        @Nullable TextRange location ) throws InvalidDataException {

        if ( map.containsKey ( key ) ) {
            throw new InvalidPdmlDataException (
                "Key '" + key + "' already exists.",
                "DUPLICATE_KEY_IN_MAP",
                location );
        }
    }
}
