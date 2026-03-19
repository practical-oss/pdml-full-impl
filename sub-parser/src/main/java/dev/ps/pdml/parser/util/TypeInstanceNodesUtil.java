package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.util.TypeInstanceNode;
import dev.ps.pdml.decoder.PdmlDecoder;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.collection.map.MapInstance;
import dev.ps.prt.type.scalar.ScalarInstance;
import dev.ps.prt.type.AnyType;
import dev.ps.prt.type.scalar.ScalarType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeInstanceNodesUtil {

    // List<TypeInstanceNode<N>>

    public static <N> @Nullable List<TypeInstanceNode<N>> parseTypeInstanceNodes (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<TypeInstanceNode<N>> list = new ArrayList<> ();
        TypeInstanceNodeStreamUtil.forEachTypeInstanceNode (
            list::add, pdmlParser, pdmlDecoder, type, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static <N> @Nullable List<TypeInstanceNode<N>> parseTypeInstanceNodes (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull AnyType<N> type,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseTypeInstanceNodes (
                pdmlParser, new PdmlDecoder ( pdmlParser ), type, allowNullValues ) );
    }


    // List<AnyInstance<N>>

    public static <N> @Nullable List<AnyInstance<N>> parseTypeInstances (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<AnyInstance<N>> list = new ArrayList<> ();
        TypeInstanceNodeStreamUtil.forEachTypeInstance (
            list::add, pdmlParser, pdmlDecoder, type, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static <N> @Nullable List<AnyInstance<N>> parseTypeInstances (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull AnyType<N> type,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseTypeInstances (
                pdmlParser, new PdmlDecoder ( pdmlParser ), type, allowNullValues ) );
    }


    // List<N>

    public static <N> @Nullable List<N> parseObjects (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<N> list = new ArrayList<> ();
        TypeInstanceNodeStreamUtil.forEachObject (
            list::add, pdmlParser, pdmlDecoder, type, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static <N> @Nullable List<N> parseObjects (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull AnyType<N> type,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseObjects (
                pdmlParser, new PdmlDecoder ( pdmlParser ), type, allowNullValues ) );
    }


    // TODO Map<String,TypeInstanceNode<N>>
    // TODO Map<String,TypeInstance<N>>


    // Map<String,N>

    public static <N> @Nullable Map<String,N> parseObjectsMap (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<N> type,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        Map<String,N> map = new HashMap<>();
        TypeInstanceNodeStreamUtil.forEachTypeInstanceNode (typeInstanceNode -> {
            String key = typeInstanceNode.tag().qualifiedTag();
            StreamHelper.checkUniqueKey ( map, key, typeInstanceNode.tag().tagPositionOrRange() );
            map.put ( key, typeInstanceNode.typeInstance().nativeObject() );
        }, pdmlParser, pdmlDecoder, type, allowNullValues );
        return map.isEmpty() ? null : map;
    }

    public static <N> @Nullable Map<String,N> parseObjectsMap (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull AnyType<N> type,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseObjectsMap (
                pdmlParser, new PdmlDecoder ( pdmlParser ), type, allowNullValues ) );
    }



    /*
        [map
            [key1 [value ...]]
            [key2 [value ...]]
            ...
        ]
     */
    public static @Nullable <K,V> List<MapInstance.Entry<K,V>> parseMapEntriesWithScalarKeys (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull ScalarType<K> keyType,
        @NotNull AnyType<V> valueType ) throws IOException, InvalidDataException {

        List<MapInstance.Entry<K,V>> entries = new ArrayList<>();

        ThrowableConsumer<TypeInstanceNode<V>> consumer = typeInstanceNode -> {

            NodeTag nodeTag = typeInstanceNode.tag();
            // TODO? provide parameter to allow namespace prefix
            /*
            if ( nodeTag.hasNamespacePrefix() ) {
                throw new InvalidPdmlDataException (
                    "Namespaces are invalid in map keys,",
                    "INVALID_MAP_KEY",
                    nodeTag.namespacePrefixPositionOrRange() );
            }
             */

            ScalarInstance<K> key = keyType.genericObjectToInstance (
                nodeTag.qualifiedTag(), nodeTag.tagPositionOrRange() );

            AnyInstance<V> value = typeInstanceNode.typeInstance();

            entries.add ( new MapInstance.Entry<> ( key, value ) );
        };

        TypeInstanceNodeStreamUtil.forEachTypeInstanceNode (
            consumer, pdmlParser, pdmlDecoder, valueType, true );

        return entries.isEmpty() ? null : entries;
    }

    /*
        [map
            [entry
                [key ...]
                [value ...]
            ]
            ...
        ]
     */
    public static @Nullable <K,V> List<MapInstance.Entry<K,V>> parseMapEntriesWithComplexKeys (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull AnyType<K> keyType,
        @NotNull AnyType<V> valueType ) throws IOException, InvalidDataException {

        throw new RuntimeException ( "Not yet implemented" );
    }



    /* TODO?
    public static @NotNull Arguments parseAsArguments (
        @NotNull PdmlParser pdmlParser,
        @NotNull Parameters parameters ) throws IOException, InvalidDataException {

        return null;
    }

    public static @NotNull Arguments parseAsArguments (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull Parameters parameters,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsArguments (
                pdmlParser, parameters ) );
    }
     */
}
