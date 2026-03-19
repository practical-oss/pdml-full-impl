package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.reader.util.NullableParsedString;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.scalar.ScalarType;

import java.io.IOException;
import java.util.*;

public class StringLiteralsUtil {


    // TextSegment

    public static @Nullable List<NullableParsedString<?>> parseAsTextSegmentList (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<NullableParsedString<?>> list = new ArrayList<>();
        StringLiteralStreamUtil.forEachTextSegment ( list::add, pdmlParser, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static @Nullable List<NullableParsedString<?>> parseAsTextSegmentList (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsTextSegmentList ( pdmlParser, allowNullValues ) );
    }


    // String

    public static @Nullable List<String> parseAsStringList (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<String> list = new ArrayList<>();
        StringLiteralStreamUtil.forEachString ( list::add, pdmlParser, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static @Nullable List<String> parseAsStringList (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsStringList ( pdmlParser, allowNullValues ) );
    }

    public static @Nullable Set<String> parseAsStringSet (
        @NotNull PdmlParser pdmlParser,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        Set<String> set = new HashSet<>();
        StringLiteralStreamUtil.forEachTextSegment ( textSegment -> {
            String string = textSegment.string ();
            if ( set.contains ( string ) ) {
                throw new InvalidPdmlDataException (
                    "Duplicate element. Element '" + string + "' has already been added to the set.",
                    "DUPLICATE_STRING_IN_SET",
                    textSegment.source() );
            }
            set.add ( string );
        }, pdmlParser, allowNullValues );
        return set.isEmpty() ? null : set;
    }

    public static @Nullable Set<String> parseAsStringSet (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsStringSet ( pdmlParser, allowNullValues ) );
    }


    // Scalar Instance

    public static <N> @Nullable List<AnyInstance<N>> parseAsScalarInstanceList (
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> elementType,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<AnyInstance<N>> list = new ArrayList<>();
        StringLiteralStreamUtil.forEachScalarInstance ( list::add, pdmlParser, elementType, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static <N> @Nullable List<AnyInstance<N>> parseAsScalarInstanceList (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull ScalarType<N> elementType,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsScalarInstanceList ( pdmlParser, elementType, allowNullValues ) );
    }


    // Scalar

    public static <N> @Nullable List<N> parseAsScalarList (
        @NotNull PdmlParser pdmlParser,
        @NotNull ScalarType<N> elementType,
        boolean allowNullValues ) throws IOException, InvalidDataException {

        List<N> list = new ArrayList<>();
        StringLiteralStreamUtil.forEachScalar ( list::add, pdmlParser, elementType, allowNullValues );
        return list.isEmpty() ? null : list;
    }

    public static <N> @Nullable List<N> parseAsScalarList (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull ScalarType<N> elementType,
        boolean allowNullValues,
        boolean hasRootNode ) throws IOException, InvalidDataException {

        return StreamHelper.parseReaderResource (
            readerResource, parserConfig, hasRootNode,
            pdmlParser -> parseAsScalarList ( pdmlParser, elementType, allowNullValues ) );
    }


    /* TODO?
    public static @NotNull Arguments parseAsPositionalScalarArguments (
        @NotNull PdmlParser pdmlParser,
        @NotNull Parameters parameters ) throws IOException, InvalidDataException {
    }
     */
}
