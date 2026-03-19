package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.namedobject.DuplicateKeyPolicy;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.range.TextPosition;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.decoder.PdmlDecoder;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.argument.Argument;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.argument.ArgumentsBuilder;
import dev.ps.prt.argument.StringArguments;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.nulltype.NullInstance;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;
import dev.ps.prt.type.AnyType;
import dev.ps.prt.type.scalar.ScalarType;

import java.io.IOException;

public class ParseArgumentsUtil {


    private static final char DEFAULT_VALUE_SYMBOL = '-';


/*
    @Deprecated
    public static void parseNodesAsArguments (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull Parameters parameters,
        @NotNull ArgumentsBuilder argumentsBuilder,
        @NotNull DuplicateKeyPolicy duplicateKeyPolicy,
        boolean ignoreInvalidArgumentNames,
        // boolean parseParentNode,
        // boolean skipLeadingTrailingWhitespace,
        boolean supportPositionalArgumentsForScalarTypes ) throws IOException, InvalidDataException {

        if ( skipLeadingTrailingWhitespace ) {
            pdmlParser.skipWhitespaceBeforeRootNode();
        }

        if ( parseParentNode ) {
            pdmlParser.requireNodeStartAndTagAndSeparator();
        }

        parseNodesAsArguments (
            pdmlParser, pdmlDecoder, parameters, argumentsBuilder,
            duplicateKeyPolicy, ignoreInvalidArgumentNames, supportPositionalArgumentsForScalarTypes );

        if ( parseParentNode ) {
            pdmlParser.requireNodeEnd();
        }

        if ( skipLeadingTrailingWhitespace ) {
            pdmlParser.requireDocumentEnd();
        }
    }
 */

    public static @NotNull Arguments parseNodesAsArguments (
        @NotNull PdmlParser pdmlParser,
        @NotNull PdmlDecoder pdmlDecoder,
        @NotNull Parameters parameters,
        @NotNull DuplicateKeyPolicy duplicateKeyPolicy,
        boolean ignoreInvalidArgumentNames,
        // boolean parseParentNode,
        // boolean skipLeadingTrailingWhitespace,
        boolean supportPositionalArgumentsForScalarTypes ) throws IOException, InvalidDataException {

        ArgumentsBuilder builder = new ArgumentsBuilder();
        parseNodesAsArguments ( pdmlParser, pdmlDecoder, parameters, builder,
            duplicateKeyPolicy, ignoreInvalidArgumentNames,
            supportPositionalArgumentsForScalarTypes );
        return builder.build();
    }

    public static @NotNull Arguments parseNodesAsArguments (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull Parameters parameters,
        @NotNull DuplicateKeyPolicy duplicateKeyPolicy,
        boolean ignoreInvalidArgumentNames,
        // boolean parseParentNode,
        boolean supportPositionalArgumentsForScalarTypes ) throws IOException, InvalidDataException {

        PdmlParser parser = PdmlParser.create ( readerResource, parserConfig );
        PdmlDecoder decoder = new PdmlDecoder ( parser );
        return parseNodesAsArguments ( parser, decoder, parameters, duplicateKeyPolicy,
            ignoreInvalidArgumentNames, supportPositionalArgumentsForScalarTypes );
    }

    public static @NotNull Arguments parseNodesAsArguments (
        @NotNull ReaderResource readerResource,
        @NotNull Parameters parameters ) throws IOException, InvalidDataException {
        // boolean parseParentNode ) throws IOException, InvalidDataException {

        return parseNodesAsArguments ( readerResource, PdmlParserConfig.defaultConfig(),
            parameters, DuplicateKeyPolicy.ERROR,
            false, true );
    }


    public static void parseNodesAsArguments (
        @NotNull PdmlParser parser,
        @NotNull PdmlDecoder decoder,
        @NotNull Parameters parameters,
        @NotNull ArgumentsBuilder argumentsBuilder,
        @NotNull DuplicateKeyPolicy duplicateKeyPolicy,
        boolean ignoreInvalidArgumentNames,
        boolean supportPositionalArgumentsForScalarTypes ) throws IOException, InvalidDataException {

        PdmlTokenReader pdmlReader = parser.pdmlReader ();
        TextPosition startPosition = null;
        int currentArgumentIndex = 0;

        while ( true ) {

            pdmlReader.skipWhitespaceAndComments();
            // if ( pdmlReader.isAtNodeEnd() || pdmlReader.isAtEnd() ) {
            if ( ! supportPositionalArgumentsForScalarTypes ) {
                if ( ! pdmlReader.isAtNodeStart() || pdmlReader.isAtEnd() ) {
                    break;
                }
            } else {
                if ( pdmlReader.isAtNodeEnd() || pdmlReader.isAtEnd() ) {
                    break;
                }
            }

            if ( startPosition == null ) {
                startPosition = pdmlReader.currentTextPosition();
            }

            @SuppressWarnings ( "rawtypes" )
            Argument argument;
            if ( supportPositionalArgumentsForScalarTypes &&
                ! pdmlReader.isAtNodeStart() ) {

                argument = parsePositionalArgumentValue (
                    parser, pdmlReader, parameters, currentArgumentIndex );
            } else {
                argument = parseArgument (
                    parser, pdmlReader, parameters, decoder );
            }

/*
            if ( argumentsBuilder.contains ( argument.name() ) ) {
                switch ( argumentsBuilder.getDuplicateArgumentsPolicy() ) {
                    case ERROR -> throw new InvalidPdmlDataException (
                        "Argument '" + argument.name() + "' has already been defined.",
                        "DUPLICATE_ARGUMENT",
                        argument.nameLocation() );
                    case OVERWRITE -> argumentsBuilder.append ( argument );
                    case IGNORE -> {}
                }
            } else {
                argumentsBuilder.append ( argument );
            }
 */
            argumentsBuilder.append ( argument, duplicateKeyPolicy );

            currentArgumentIndex++;
        }

        if ( startPosition == null ) {
            // no arguments defined; use only default values
            startPosition = pdmlReader.currentTextPosition();
        }
        argumentsBuilder.setLocation ( startPosition );

        checkAllArgumentsDefined ( argumentsBuilder, parameters );
    }

    private static @NotNull Argument<?> parseArgument (
        @NotNull PdmlParser parser,
        @NotNull PdmlTokenReader reader,
        @NotNull Parameters parameters,
        @NotNull PdmlDecoder decoder ) throws IOException, InvalidDataException {

        NodeTag nodeTag = parseNodeTag ( parser, parameters );
        String argumentName = nodeTag.tag();
        Parameter<?> parameter = parameters.get ( argumentName );
        AnyType<?> type = parameter.type ();

        reader.readSeparator();

        AnyInstance<?> argumentValue = type.decodeToInstance ( decoder );
        if ( ! reader.skipNodeEnd () ) {
            throw new InvalidPdmlDataException (
                "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "' to end argument '" + argumentName + "'.",
                "MISSING_NODE_END",
                reader.currentTextPosition() );
        }

        @SuppressWarnings ({"rawtypes", "unchecked"})
        Argument argument = new Argument ( parameter.name(), argumentValue, parameter,
            nodeTag.startLocation() );
        return argument;
    }

    private static @NotNull NodeTag parseNodeTag (
        @NotNull PdmlParser parser,
        @NotNull Parameters parameters ) throws IOException, PdmlException {

        NodeTag nodeTag = parser.requireFromNodeStartToTag ();

        if ( nodeTag.hasNamespacePrefix() ) {
            throw new InvalidPdmlDataException (
                "Namespaces cannot be used in this context.",
                "NAMESPACE_NOT_ALLOWED",
                nodeTag.namespacePrefixPositionOrRange() );
        }
        String argumentName = nodeTag.tag();
        if ( ! parameters.contains ( argumentName ) ) {
            throw new InvalidPdmlDataException (
                "Argument name '" + argumentName + "' is invalid. The following names are valid: " + parameters.sortedNamesAsString(),
                "INVALID_ARGUMENT_NAME",
                nodeTag.tagPositionOrRange() );
        }

        return nodeTag;
    }

    private static @NotNull Argument<?> parsePositionalArgumentValue (
        @NotNull PdmlParser parser,
        @NotNull PdmlTokenReader reader,
        @NotNull Parameters parameters,
        int currentFieldIndex ) throws IOException, InvalidDataException {

        if ( currentFieldIndex >= parameters.count() ) {
            throw new InvalidPdmlDataException (
                "Too many arguments. There are only " + parameters.count() + " parameters.",
                "TOO_MANY_ARGUMENTS",
                reader.currentTextPosition() );
        }

        Parameter<?> parameter = parameters.getAtIndex ( currentFieldIndex );
        AnyType<?> parameterType = parameter.type();
        ScalarType<?> scalarType;
        // TODO consider union type scalar or null (e.g. string or null)
        if ( parameterType instanceof ScalarType<?> sc ) {
            scalarType = sc;
        } else {
            throw new InvalidPdmlDataException (
                "A positional value is not allowed, because parameter '" + parameterType.name() +
                    "' is not a scalar type parameter. The type is '" + parameterType.name() + "'.",
                "INVALID_POSITIONAL_ARGUMENT",
                reader.currentTextPosition() );
        }

        AnyInstance<?> argumentValue;
        TextPosition valuePosition = reader.currentTextPosition();
        if ( reader.isAtChar ( DEFAULT_VALUE_SYMBOL ) ) {
            // use default value
            AnyInstance<?> defaultValue = parameter.defaultValueAsInstance();
            if ( defaultValue != null ) {
                argumentValue = defaultValue;
                reader.advanceChar();
                // TODO check at whitespace char or end of fields
            } else {
                throw new InvalidPdmlDataException (
                    "There is no default value for parameter '" + parameter.name() + "'.",
                    "NO_DEFAULT_VALUE_AVAILABLE",
                    valuePosition );
            }
        } else {
            String stringValue = parser.parseStringLiteralOrNullInTextLeaf();
            if ( stringValue != null ) {
                argumentValue = scalarType.genericObjectToInstance ( stringValue, valuePosition );
            } else {
                argumentValue = NullInstance.create ( valuePosition );
            }
        }

        @SuppressWarnings ({"rawtypes", "unchecked"})
        Argument<?> argument = new Argument (
            parameter.name(), argumentValue, parameter, null );
        return argument;
    }

    private static void checkAllArgumentsDefined (
        @NotNull ArgumentsBuilder argumentsBuilder,
        @NotNull Parameters parameters) throws InvalidDataException {

        for ( Parameter<?> parameter : parameters.list() ) {
            String name = parameter.name();
            if ( ! argumentsBuilder.contains ( name ) ) {
                AnyInstance<?> defaultValue = parameter.defaultValueAsInstance();
                if (defaultValue == null) {
                    throw new InvalidDataException (
                        "Missing argument '" + name + "'.",
                        "MISSING_ARGUMENT",
                        argumentsBuilder.getLocation(), null, null);
                }

                @SuppressWarnings ({"rawtypes", "unchecked"})
                Argument<?> argument = new Argument ( name, defaultValue, parameter, null );
                argumentsBuilder.append(argument);
            }
        }
    }


    public static @Nullable StringArguments parseTextNodesAsStringArguments (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig ) throws IOException, PdmlException {

        // TODO
        throw new RuntimeException ( "Not yet implemented" );
    }
}
