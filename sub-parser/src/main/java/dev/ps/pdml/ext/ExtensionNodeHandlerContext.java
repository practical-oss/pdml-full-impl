package dev.ps.pdml.ext;

import dev.ps.shared.basics.utilities.namedobject.DuplicateKeyPolicy;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.range.TextRange;
import dev.ps.shared.text.range.TextPosition;
import dev.ps.shared.text.reader.util.NullableParsedString;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.parser.util.ParseArgumentsUtil;
import dev.ps.pdml.parser.util.StringAssignmentsUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.scriptingbase.env.ScriptingEnvironment;
import dev.ps.pdml.decoder.PdmlDecoder;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.argument.Argument;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.argument.ArgumentsBuilder;
import dev.ps.prt.argument.StringArguments;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;
import dev.ps.prt.type.AnyType;
import dev.ps.prt.type.scalar.ScalarOrScalarListType;
import dev.ps.prt.type.util.ScalarTypeUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExtensionNodeHandlerContext {


    private final @NotNull PdmlTokenReader pdmlReader;
    public @NotNull PdmlTokenReader getPdmlReader() { return pdmlReader; }

    private final @NotNull PdmlParser pdmlParser;
    public @NotNull PdmlParser getPdmlParser() { return pdmlParser; }

    private final PdmlDecoder decoder;

    private final @Nullable ScriptingEnvironment scriptingEnvironment;
    public @Nullable ScriptingEnvironment getScriptingEnvironment() { return scriptingEnvironment; }

    private final @NotNull Map<String, String> declaredConstants = new HashMap<>();
    public @NotNull Map<String, String> getDeclaredConstants() { return declaredConstants; }


    public ExtensionNodeHandlerContext (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParser pdmlParser,
        @Nullable ScriptingEnvironment scriptingEnvironment ) {

        this.pdmlReader = pdmlReader;
        this.pdmlParser = pdmlParser;
        this.scriptingEnvironment = scriptingEnvironment;
        this.decoder = new PdmlDecoder ( pdmlParser );
    }


    public @NotNull ScriptingEnvironment requireScriptingEnvironment (
        @Nullable TextRange textLocation ) throws PdmlException {

        if ( scriptingEnvironment != null ) {
            return scriptingEnvironment;
        } else {
            throw error (
                "Scripting is not supported in this context (scriptingEnvironment == null)",
                "SCRIPTING_NOT_SUPPORTED",
                textLocation );
        }
    }

    public @Nullable TextPosition currentPosition() {
        return pdmlReader.currentTextPosition();
    }

    public void skipWhitespaceAndComments() throws IOException, MalformedPdmlException {
        pdmlReader.skipWhitespaceAndComments();
    }

    public void requireExtensionNodeEnd ( @NotNull NodeTag nodeName )
        throws IOException, MalformedPdmlException {

        // TODO use nodeName to improve error message
        requireExtensionNodeEnd();
    }

    public void requireExtensionNodeEnd (
        // @NotNull NodeTag nodeName
        ) throws IOException, MalformedPdmlException {

        if ( ! pdmlReader.skipNodeEnd () ) {
            throw new MalformedPdmlException (
                "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "'.",
                "EXTENSION_NODE_END_REQUIRED",
                pdmlReader.currentTextPosition() );
        }
    }

    public @Nullable StringArguments parseStringArgumentAssignments (
        boolean allowNullValues )
        throws IOException, PdmlException {

        // return pdmlParser.parseStringArgumentAssignments (
        //    allowNullValues, CorePdmlConstants.NODE_END_CHAR );
        try {
            return StringAssignmentsUtil.parseAsStringArguments (
                pdmlParser, allowNullValues );
        } catch ( InvalidDataException e ) {
            throw new InvalidPdmlDataException ( e );
        }
    }

    public @Nullable String parseTrimmedTextAndIgnoreComments()
        throws IOException, PdmlException {

        return pdmlParser.parseTrimmedTextLeafAsStringAndIgnoreComments ();
    }

    /*
    public @Nullable String parseTrimmedTextOrStringLiteral()
        throws IOException, PdmlException {

        return pdmlParser.parseTextLeafAsTrimmedTextOrStringLiteral();
    }
     */

    public @Nullable NullableParsedString<TextPosition> parseTrimmedTextOrStringLiteral()
        throws IOException, PdmlException {

        return pdmlParser.parseTrimmedTextOrStringLiteral();
    }

    /*
    public @NotNull Arguments parseArguments (
        @NotNull Parameters parameters ) throws IOException, PdmlException {

        try {
            // PdmlDecoder decoder = new PdmlDecoder ( pdmlParser );
            Arguments arguments = decoder.decodeParameters ( parameters );
            requireExtensionNodeEnd ();
            return arguments;

        } catch ( InvalidDataException e ) {
            throw new InvalidPdmlDataException ( e.getMessage (), e.getId (),
                e.getTextPositionOrRange (), e );
        }
    }
     */

    public @NotNull Arguments parseExtensionNodeArguments (
        @NotNull Parameters parameters,
        @NotNull Parameter<?> textParameter ) throws IOException, PdmlException {

        // Parse
        @Nullable NullableParsedString<TextPosition> textBeforeArguments = parseTrimmedTextOrStringLiteral();
        ArgumentsBuilder argumentsBuilder = new ArgumentsBuilder();
        argumentsBuilder.setLocation ( pdmlReader.currentTextPosition() );
        try {
            ParseArgumentsUtil.parseNodesAsArguments (
                pdmlParser, decoder, parameters, argumentsBuilder,
                DuplicateKeyPolicy.ERROR, false, false );
        } catch ( InvalidDataException e ) {
            throw new InvalidPdmlDataException ( e );
        }
        @Nullable NullableParsedString<TextPosition> textAfterArguments =
            textBeforeArguments == null ? parseTrimmedTextOrStringLiteral() : null;
        skipWhitespaceAndComments();
        requireExtensionNodeEnd();

        // Define Text Argument

        String textParameterName = textParameter.name();
        @Nullable NullableParsedString<TextPosition> parsedText =
            textBeforeArguments != null ? textBeforeArguments : textAfterArguments;
        @Nullable Argument<?> parsedArgument = argumentsBuilder.getOrNull ( textParameterName );
        @Nullable Object parsedArgumentValue = parsedArgument != null ? parsedArgument.value().nativeObject() : null;
        if ( parsedText != null && parsedArgumentValue == null ) {
            AnyType<?> type = textParameter.type();
            @Nullable ScalarOrScalarListType<?> scalarType = ScalarTypeUtil.toScalarOrScalarListType ( type );
            if ( scalarType == null ) {
                throw new IllegalArgumentException (
                    "Invalid type '" + type + "' for parameter '" + textParameterName + "'. The type must be a scalar type." );
            }
            try {
                AnyInstance<?> instance = scalarType.genericObjectToInstance (
                    parsedText.string (), parsedText.source() );
                @SuppressWarnings ( {"rawtypes", "unchecked"} )
                Argument<?> parsedArgument_ = new Argument (
                    textParameterName, instance, textParameter, parsedText.source() );
                argumentsBuilder.replace ( parsedArgument_ );
            } catch ( InvalidDataException e ) {
                throw new InvalidPdmlDataException ( e );
            }

        } else if ( parsedText == null && parsedArgumentValue == null ) {
            throw new InvalidPdmlDataException (
                "Argument '" + textParameterName + "' must be defined.",
                "MISSING_ARGUMENT",
                argumentsBuilder.getLocation() );

        } else if ( parsedText != null && parsedArgumentValue != null ) {
            throw new InvalidPdmlDataException (
                "Argument '" + textParameterName + "' cannot be defined twice (as text '" + parsedText +
                "' and as record field '" + parsedArgumentValue + "'.)",
                "DUPLICATE_ARGUMENT",
                parsedText.source() );
        }

        return argumentsBuilder.build();
    }


    public @NotNull PdmlException error (
        @NotNull String message,
        @NotNull String id,
        @Nullable TextRange textLocation ) {

        return new PdmlException ( message, id, textLocation );
    }

    public @NotNull PdmlException errorAtCurrentLocation (
        @NotNull String message,
        @NotNull String id ) {

        return error ( message, id, pdmlReader.currentTextPosition() );
    }

/*
    public <V> @Nullable Parameters<V> parseAttributes (
        @Nullable MutableOrImmutableParameterSpecs<V> parameterSpecs ) throws IOException, PdmlException {

        @Nullable NodeAttributes attributes = pdmlParser.parseAttributes();

        try {
            return ParametersCreator.createFromStringParameters (
                attributes, attributes == null ? null : attributes.getStartToken(), parameterSpecs );
        } catch ( InvalidTextException e ) {
            throw new InvalidPdmlDataException ( e );
        }
    }
 */

    /*
    public @Nullable NodeAttributes parseAttributes()
        throws IOException, PdmlException {

        return pdmlParser.parseAttributes();
    }
     */


/*
    public @Nullable NodeAttributes parseAttributesWithOptionalParenthesis()
        throws IOException, PdmlException {

        return pdmlParser.parseAttributesWithOptionalParenthesis();
    }

    public <V> @Nullable Parameters<V> parseParametersWithOptionalParenthesis (
        @Nullable MutableOrImmutableParameterSpecs<V> parameterSpecs ) throws IOException, PdmlException {

        @Nullable NodeAttributes attributes = parseAttributesWithOptionalParenthesis();

        try {
            return ParametersCreator.createFromStringParameters (
                attributes, attributes == null ? null : attributes.getStartToken(), parameterSpecs );
        } catch ( InvalidTextException e ) {
            throw new InvalidPdmlDataException ( e );
        }
    }
 */
}
