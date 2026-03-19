package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.util.NullableTextNode;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.prt.argument.StringArguments;
import dev.ps.prt.argument.StringArgumentsBuilder;
import dev.ps.prt.type.scalar.ScalarType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextNodesUtil {

    // List<NullableTextNode>

    public static @Nullable List<NullableTextNode> parseTextNodes (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<NullableTextNode> list = new ArrayList<>();
        TextNodeStreamUtil.forEachTextNode ( list::add, pdmlParser, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static @Nullable List<NullableTextNode> parseTextNodes (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseTextNodes ( pdmlParser, allowNullValues ) );
    }


    // Map<String,String>

    public static @Nullable Map<String,String> parseAsStringMap (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {
        // TODO? @NotNull DuplicateKeyPolicy duplicateKeyPolicy,

        Map<String,String> map = new HashMap<>();
        TextNodeStreamUtil.forEachTextNode ( textNode -> {
            String name = textNode.tagAsString();
            StreamHelper.checkUniqueKey ( map, name, textNode.tagLocation() );
            map.put ( name, textNode.text() );
        }, pdmlParser, allowNullValues );
        return map.isEmpty() ? null : map;
    }

    public static @Nullable Map<String,String> parseAsStringMap (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsStringMap ( pdmlParser, allowNullValues ) );
    }


    // Map<String,N>

    public static <N> @Nullable Map<String,N> parseAsScalarMap (
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> valueType,
        boolean allowNullValues ) throws IOException, InvalidDataException {
        // TODO? @NotNull DuplicateKeyPolicy duplicateKeyPolicy,

        Map<String,N> map = new HashMap<>();
        TextNodeStreamUtil.forEachScalarArgument ( scalarArgument -> {
            String name = scalarArgument.name();
            StreamHelper.checkUniqueKey ( map, name, scalarArgument.nameLocation() );
            map.put ( name, scalarArgument.value().nativeObject() );
        }, pdmlParser, valueType, allowNullValues );
        return map.isEmpty() ? null : map;
    }

    public static <N> @Nullable Map<String,N> parseAsScalarMap (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull ScalarType<N> valueType,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsScalarMap ( pdmlParser, valueType, allowNullValues ) );
    }


    // StringArguments

    public static @Nullable StringArguments parseAsStringArguments (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {
        // TODO? @NotNull DuplicateKeyPolicy duplicateKeyPolicy,

        StringArgumentsBuilder builder = new StringArgumentsBuilder();
        TextNodeStreamUtil.forEachStringArgument ( builder::append, pdmlParser, allowNullValues );
        return builder.buildOrNull();
    }

    public static @Nullable StringArguments parseAsStringArguments (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsStringArguments ( pdmlParser, allowNullValues ) );
    }
}
