package dev.ps.pdml.cmdnode.scripting;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.cmdnode.*;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.shared.scriptingbase.env.ScriptingEnvironment;
import dev.ps.shared.scriptingbase.env.ScriptingException;

import java.io.IOException;

import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.ESCAPE_TEXT_PARAMETER;

public class InsertExpressionCommandNode {

    public static final @NotNull String NAME = "ins-exp";

    public static final Parameter<@NotNull String> CODE_PARAMETER = ParameterBuilder
        // TODO? .ofString ( "exp" )
        .ofString ( "code" )
        .isPositional ( true )
        .title ( "Code" )
        .description ( "The expression to be evaluated and inserted into the document." )
        .examples ( "^[ins-exp \"1 + 1\"]" ).build();


    public static final CommandNode INSTANCE = new CommandNodeBuilder ( NAME )
        .parameter ( CODE_PARAMETER )
        .parameter ( ESCAPE_TEXT_PARAMETER )
        .executor ( InsertExpressionCommandNode::execute )
        .title ( "Insert Java Expression" )
        .build();


    private static @Nullable CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException {

        assert arguments != null;

        @NotNull String expression = arguments.nonNullStringValue ( CODE_PARAMETER.name() );
        boolean escapeText = arguments.nonNullBooleanValue ( ESCAPE_TEXT_PARAMETER.name() );

        @Nullable String result = evaluateJavaExpression ( expression, context, nodeTag );

        return result != null
            ? new CommandNodeResult ( result, escapeText )
            : null;
    }

    private static @Nullable String evaluateJavaExpression (
        @NotNull String expression,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeName ) throws PdmlException {

        try {
            ScriptingEnvironment scriptingEnvironment = context.requireScriptingEnvironment ( nodeName.tagLocation () );
            return scriptingEnvironment.evaluateExpressionAsString ( expression );
        } catch ( ScriptingException e ) {
            // scriptingError ( e, expression, context, nodeName );
            ScriptingCommandsUtil.scriptingError (
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
