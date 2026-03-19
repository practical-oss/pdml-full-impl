package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.util.TypeInstanceNode;
import dev.ps.pdml.decoder.PdmlDecoder;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.AnyType;

import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TypeInstanceNodeStreamUtil {

    private static class TypeInstanceNodeSpliterator<N>
        extends Spliterators.AbstractSpliterator<TypeInstanceNode<N>>
        implements Spliterator<TypeInstanceNode<N>> {


        private final @NotNull PdmlParser pdmlParser;
        private final @NotNull PdmlTokenReader pdmlReader;
        private final @NotNull PdmlDecoder pdmlDecoder;
        private final @NotNull AnyType<N> type;


        public TypeInstanceNodeSpliterator (
            @NotNull PdmlParser pdmlParser,
            @NotNull PdmlDecoder pdmlDecoder,
            @NotNull AnyType<N> type ) {

            super ( Long.MAX_VALUE,
                Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE );

            this.pdmlParser = pdmlParser;
            this.pdmlReader = pdmlParser.pdmlReader ();
            // this.pdmlDecoder = new PdmlDecoder ( pdmlParser );
            this.pdmlDecoder = pdmlDecoder;
            this.type = type;
        }


        @Override
        public boolean tryAdvance ( Consumer<? super TypeInstanceNode<N>> consumer ) {

            try {
                pdmlReader.skipWhitespaceAndComments();
                // if ( pdmlReader.isAtNodeEnd() || pdmlReader.isAtEnd() ) {
                if ( ! pdmlReader.isAtNodeStart() ) {
                    return false;
                }

                @NotNull TextRange startPosition = pdmlReader.currentTextPosition();
                /*
                @Nullable TaggedNode taggedNode = pdmlParser.parseTaggedNodeStartAndTag();
                if ( taggedNode == null ) {
                    throw new InvalidPdmlDataException (
                        "Node required",
                        "NODE_REQUIRED",
                        startPosition );
                }
                 */
                NodeTag tag = pdmlParser.requireFromNodeStartToTag();
                TaggedNode taggedNode = new TaggedNode ( tag );


                pdmlReader.readSeparator();

                AnyInstance<N> instance = type.decodeToInstance ( pdmlDecoder );
                @NotNull TypeInstanceNode<N> typeInstanceNode = new TypeInstanceNode<> (
                    taggedNode.getTag(), instance, startPosition );

                pdmlParser.requireTaggedNodeEnd ( taggedNode );

                consumer.accept ( typeInstanceNode );

                return true;

            } catch ( IOException | InvalidDataException e ) {
                throw new RuntimeException ( e.getMessage(), e );
            }
        }
    }


    // Stream

    public static <N> @NotNull Stream<TypeInstanceNode<N>> typeInstanceNodeStream (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) {

        Stream<TypeInstanceNode<N>> stream = StreamSupport.stream (
            new TypeInstanceNodeSpliterator<> ( pdmlParser, pdmlDecoder, type ), false );
        if ( allowNullValues ) {
            return stream;
        } else {
            return stream.filter ( typeInstanceNode -> {
                if ( ! typeInstanceNode.typeInstance().isNull() ) {
                    return true;
                } else {
                    throw new RuntimeException ( new InvalidPdmlDataException (
                        "An empty text node (null) is not allowed.",
                        "INVALID_EMPTY_TEXT_NODE",
                        typeInstanceNode.tag().tagPositionOrRange() ) );
                }
            } );
        }
    }

    public static <N> @NotNull Stream<AnyInstance<N>> typeInstanceStream (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) {

        return typeInstanceNodeStream ( pdmlParser, pdmlDecoder, type, allowNullValues )
            .map ( TypeInstanceNode::typeInstance );
    }

    public static <N> @NotNull Stream<N> objectStream (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) {

        return typeInstanceStream ( pdmlParser, pdmlDecoder, type, allowNullValues )
            .map ( AnyInstance::nativeObject );
    }


    // ForEach

    public static <N> void forEachTypeInstanceNode (
        @NotNull ThrowableConsumer<? super TypeInstanceNode<N>> consumer,
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            typeInstanceNodeStream ( pdmlParser, pdmlDecoder, type, allowNullValues ),
            consumer );
    }

    public static <N> void forEachTypeInstance (
        @NotNull ThrowableConsumer<? super AnyInstance<N>> consumer,
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            typeInstanceStream ( pdmlParser, pdmlDecoder, type, allowNullValues ),
            consumer );
    }

    public static <N> void forEachObject (
        @NotNull ThrowableConsumer<? super N> consumer,
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StreamHelper.forEachT (
            objectStream ( pdmlParser, pdmlDecoder, type, allowNullValues ),
            consumer );
    }
}
