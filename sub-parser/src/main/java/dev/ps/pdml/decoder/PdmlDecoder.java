package dev.ps.pdml.decoder;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.namedobject.DuplicateKeyPolicy;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.inspection.InvalidTextException;
import dev.ps.shared.text.range.TextPosition;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.reader.util.NullableParsedString;
import dev.ps.shared.text.reader.util.ParsedString;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.util.*;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.type.collection.map.MapInstance;
import dev.ps.prt.parameter.Parameters;
import dev.ps.prt.type.collection.list.ListType;
import dev.ps.prt.type.collection.map.MapType;
import dev.ps.prt.type.scalar.ScalarType;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.prt.type.decoder.Decoder;
import dev.ps.prt.type.AnyType;
import dev.ps.prt.type.union.UnionType;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.nulltype.NullInstance;

import java.io.IOException;
import java.util.*;

public class PdmlDecoder implements Decoder {


    private final @NotNull PdmlParser parser;
    private final @NotNull PdmlTokenReader reader;

    // Experimental
    // Default must be false (now true for testing)
    private final boolean supportPositionalRecordFieldsForScalarTypes = true;


    public PdmlDecoder ( @NotNull PdmlParser parser ) {

        this.parser = parser;
        this.reader = parser.pdmlReader ();
    }

    public PdmlDecoder (
        @NotNull ReaderResource readerResource ) throws IOException {
        this ( PdmlParser.create ( readerResource, PdmlParserConfig.defaultConfig() ) );
    }

    /*
    public PdmlDecoder (
        @NotNull Reader reader,
        @NotNull ReaderResource readerResource ) throws IOException {
        this ( PdmlParser.create ( reader, readerResource, PdmlParserConfig.defaultConfig() ) );
    }
     */


    @Override
    public @NotNull NullableParsedString<?> decodeNull()
        throws InvalidTextException {

        var reader = parser.pdmlReader ();
        if ( reader.isAtNodeEnd() ) {
            // return new NullValue ( reader.currentLocation() );
            return new NullableParsedString<> ( null, reader.currentTextPosition() );
        } else {
            throw new InvalidTextException (
                "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "' to represent a 'null' value",
                "EXPECTING_NODE_END_FOR_NULL_VALUE",
                reader.currentTextPosition() );
        }
    }

    @Override
    public @NotNull ParsedString<?> decodeScalar()
        throws IOException, InvalidTextException {

        TextPosition position = reader.currentTextPosition();
        String string = parser.parseTextLeafAsStringAndIgnoreComments ();
        if ( string != null ) {
            return new ParsedString<> ( string, position );
        } else {
            throw new InvalidTextException (
                "Expecting a string value.",
                "EXPECTING_STRING_VALUE",
                position );
        }
    }


    // List

    @Override
    public @NotNull <E> ListResult<E> decodeList ( @NotNull ListType<?,E> listType )
        throws IOException, InvalidDataException {

        // TODO empty list?

        @NotNull AnyType<E> elementType = listType.elementType();

        reader.skipWhitespaceAndComments();
        @Nullable TextPosition startPosition = reader.currentTextPosition();
        @Nullable List<AnyInstance<E>> elements;
        if ( elementType instanceof ScalarType<E> scalarType &&
            ! reader.isAtNodeStart() ) {
            elements = StringLiteralsUtil.parseAsScalarInstanceList (
                parser, scalarType, false );
        } else {
            elements = TypeInstanceNodesUtil.parseTypeInstances (
                parser, this, elementType, true );
        }

        if ( elements == null ) {
            throw new InvalidPdmlDataException (
                "A list with elements of type '" + elementType + "' is required.",
                "LIST_REQUIRED",
                startPosition );
        }

        return new Decoder.ListResult<> ( elements, startPosition );
    }


    // Map

    public @NotNull <K,V> MapResult<K,V> decodeMap ( @NotNull MapType<?,K,V> mapType )
        throws IOException, InvalidDataException {

        TextPosition startPosition = reader.currentTextPosition();

        @NotNull AnyType<K> keyType = mapType.keyType();
        @NotNull AnyType<V> valueType = mapType.valueType();
        @Nullable List<MapInstance.Entry<K,V>> entries;
        if ( keyType instanceof ScalarType<K> scalarKeyType ) {
            entries = TypeInstanceNodesUtil.parseMapEntriesWithScalarKeys (
                parser, this, scalarKeyType, valueType );
        } else {
            entries = TypeInstanceNodesUtil.parseMapEntriesWithComplexKeys (
                parser, this, keyType, valueType );
        }

        if ( entries == null ) {
            throw new InvalidPdmlDataException (
                "Missing map entries.",
                "MISSING_MAP_ENTRIES",
                reader.currentTextPosition() );
        }

        return new MapResult<> ( entries, startPosition );
    }


    // Record

    @Override
    public @NotNull Arguments decodeParameters ( @NotNull Parameters parameters )
        throws IOException, InvalidDataException {

        return ParseArgumentsUtil.parseNodesAsArguments (
            parser, this, parameters,
            DuplicateKeyPolicy.ERROR,
            false,
            supportPositionalRecordFieldsForScalarTypes );
    }


    // public @NotNull DecoderUnionResult decodeUnion ( @NotNull UnionType unionType )
    public @NotNull AnyInstance<?> decodeUnion ( @NotNull UnionType<?> unionType )
        throws IOException, InvalidDataException {

        // TODO remove when the member type is parsed
        if ( ! unionType.isNullablePair() ) {
            throw new RuntimeException ( "Currently, union type can be used only to define a pair of type with one member being the 'null' type." );
        }

        if ( reader.isAtNodeEnd() ) {
            return NullInstance.create ( reader.currentTextPosition() );
        } else {
            return unionType.firstNonNullMember().decodeToInstance ( this );
        }
        /*
        TextLocation startLocation = reader.currentLocation();
        AnyValue<?> value = reader.isAtNodeEnd() ?
            new NullValue ( reader.currentLocation() ) :
            unionType.firstNonNullMember().decode ( this );
        return new DecoderUnionResult ( value, startLocation );
         */
    }
}
