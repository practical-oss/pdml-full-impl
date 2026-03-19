package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.prt.argument.StringArguments;
import dev.ps.prt.argument.StringArgumentsBuilder;
import dev.ps.prt.type.scalar.ScalarType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StringAssignmentsUtil {

    public static @Nullable StringArguments parseAsStringArguments (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        StringArgumentsBuilder builder = new StringArgumentsBuilder();
        StringAssignmentStreamUtil.forEachStringArgument ( builder::append, pdmlParser, allowNullValues );
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

/*
    public static @Nullable List<StringArgument> parseAsStringArgumentList (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<StringArgument> list = new ArrayList<>();
        StringAssignmentStreamUtil.forEachStringArgument ( list::add, pdmlParser, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static @Nullable List<StringArgument> parseAsStringArgumentList (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsStringArgumentList ( pdmlParser, allowNullValues ) );
    }
 */


    // StringMap

    public static @Nullable Map<String,String> parseAsStringMap (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {
        // TODO? @NotNull DuplicateKeyPolicy duplicateKeyPolicy,

        Map<String,String> map = new HashMap<>();
        StringAssignmentStreamUtil.forEachStringArgument ( stringArgument -> {
            String name = stringArgument.name();
            StreamHelper.checkUniqueKey ( map, name, stringArgument.nameLocation() );
            map.put ( name, stringArgument.value() );
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


    // ScalarMap

    public static <N> @Nullable Map<String,N> parseAsScalarMap (
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> valueType,
        boolean allowNullValues ) throws IOException, InvalidDataException {
        // TODO? @NotNull DuplicateKeyPolicy duplicateKeyPolicy,

        Map<String,N> map = new HashMap<>();
        StringAssignmentStreamUtil.forEachScalarArgument ( scalarArgument -> {
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
}
