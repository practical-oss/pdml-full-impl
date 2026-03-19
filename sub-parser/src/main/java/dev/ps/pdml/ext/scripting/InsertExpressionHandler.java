package dev.ps.pdml.ext.scripting;

import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.ExtensionNodeHandlerContext;
import dev.ps.pdml.ext.ExtensionNodeHandler;
import dev.ps.pdml.ext.InsertReaderResourceExtensionResult;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.shared.scriptingbase.env.ScriptingEnvironment;
import dev.ps.shared.scriptingbase.env.ScriptingException;
import dev.ps.pdml.ext.SharedExtensionParameters;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.NewParam;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.io.IOException;

public class InsertExpressionHandler implements ExtensionNodeHandler {

    public static final @NotNull String NAME = "ins_exp";

    public static final InsertExpressionHandler INSTANCE = new InsertExpressionHandler();

    public static final Parameter<String> CODE_PARAMETER = NewParam.stringOrNull (
        "code", null, () -> null,
        () -> new SimpleDocumentation (
            "Code",
            "The expression to be evaluated and inserted into the document.",
            "[exp 1 + 1]" ) );

    public static final Parameter<Boolean> ESCAPE_TEXT_PARAMETER =
        SharedExtensionParameters.ESCAPE_TEXT_PARAMETER;

    public static final Parameters PARAMETERS = new Parameters (
        CODE_PARAMETER, ESCAPE_TEXT_PARAMETER );


    private InsertExpressionHandler() {}


    @Override
    public @NotNull String getExtensionName() {
        return NAME;
    }

    @Override
    public @Nullable InsertReaderResourceExtensionResult handleNode (
        @NotNull ExtensionNodeHandlerContext context,
        @NotNull NodeTag nodeName ) throws IOException, PdmlException {

        // Arguments arguments = context.parseArguments ( PARAMETERS );
        @NotNull Arguments arguments = context.parseExtensionNodeArguments ( PARAMETERS, CODE_PARAMETER );
        @NotNull String expression = arguments.nonNullStringValue ( CODE_PARAMETER.name() );
        boolean escapeText = arguments.nonNullBooleanValue ( ESCAPE_TEXT_PARAMETER.name() );

        @Nullable String result = evaluateJavaExpression ( expression, context, nodeName );

        return result != null
            ? new InsertReaderResourceExtensionResult ( new StringReaderResource ( result ), escapeText )
            : null;
    }

    private static @Nullable String evaluateJavaExpression (
        @NotNull String expression,
        @NotNull ExtensionNodeHandlerContext context,
        @NotNull NodeTag nodeName ) throws PdmlException {

        try {
            ScriptingEnvironment scriptingEnvironment = context.requireScriptingEnvironment ( nodeName.tagPositionOrRange() );
            return scriptingEnvironment.evaluateExpressionAsString ( expression );
        } catch ( ScriptingException e ) {
            // scriptingError ( e, expression, context, nodeName );
            ScriptingHandlerUtil.scriptingError (
                "Expression '" + expression + "' is invalid. Reason:" +
                    StringConstants.OS_LINE_BREAK + e.getMessage(),
                "INVALID_EXPRESSION",
                nodeName, e, context );
            return null;
        }
    }

/*
    private @Nullable String evaluateJavaScriptExpression (
        @NotNull String expression,
        @NotNull ExtensionNodeHandlerContext context,
        @NotNull NodeName nodeName ) throws PdmlException {

        try {
            // ScriptingEnvironment scriptingEnvironment = requireScriptingEnvironment ( context, nodeName.localNameToken() );
            @NotNull ScriptingEnvironment scriptingEnvironment = context.requireScriptingEnvironment ( nodeName.token() );
//            Map<String, Object> bindings = BindingsCreator.createMap ( reader, nodeName.getToken() );
//            return scriptingEnvironment.evaluateExpressionToString (
//                ScriptingConstants.JAVASCRIPT_LANGUAGE_ID, expression, bindings, true );
            return scriptingEnvironment.evaluateExpressionToString ( expression );

        // } catch ( Exception e ) {
        } catch ( ScriptingException e ) {
            // } catch ( PolyglotException e ) {
            // TODO See if a more precise error position can be reported, using methods in PolyglotException
            // https://www.graalvm.org/sdk/javadoc/org/graalvm/polyglot/Context.html#parse-org.graalvm.polyglot.Source-
            scriptingError ( e, expression, context, nodeName );
            return null;
        }
    }
 */
}
