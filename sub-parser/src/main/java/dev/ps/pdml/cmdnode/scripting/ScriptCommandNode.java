package dev.ps.pdml.cmdnode.scripting;

import dev.ps.pdml.cmdnode.*;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.shared.scriptingbase.env.ScriptingEnvironment;
import dev.ps.shared.scriptingbase.env.ScriptingException;

import java.io.IOException;

public class ScriptCommandNode {

    public static final @NotNull String NAME = "script";

    public static final Parameter<@NotNull String> CODE_PARAMETER = ParameterBuilder
        .ofString ( "code" )
        .isPositional ( true )
        .title ( "Code" )
        .description ( "A sequence of Java statements (called script)." )
        .examples ( "System.out.println ( \"Hello\");" ).build();


    public static final CommandNode INSTANCE = new CommandNodeBuilder ( NAME )
        .parameter ( CODE_PARAMETER )
        .executor ( ScriptCommandNode::execute )
        .title ( "Java Script" )
        .build();


    private static @Nullable CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException {

        assert arguments != null;

        @NotNull String script = arguments.nonNullStringValue ( CODE_PARAMETER.name() );
        executeJavaCode ( script, context, nodeTag );

        return null;
    }

    private static void executeJavaCode (
        @NotNull String code,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeName ) throws PdmlException {

        try {
            ScriptingEnvironment scriptingEnvironment = context.requireScriptingEnvironment ( nodeName.tagLocation () );
            scriptingEnvironment.executeScript ( code );
        } catch ( ScriptingException e ) {
            ScriptingCommandsUtil.scriptingError (
                "Invalid script. Reason:" + StringConstants.OS_LINE_BREAK + e.getMessage(),
                "INVALID_SCRIPT",
                nodeName,
                e,
                context );
        }
    }

/*
    private void executeJavaScriptCode (
        @NotNull String code,
        @NotNull ExtensionNodeHandlerContext context,
        @NotNull NodeName nodeName ) throws PdmlException {

        try {
//          Map<String, Object> bindings = BindingsCreator.createMap ( reader, nodeName.getToken() );
            // ScriptingEnvironment scriptingEnvironment =
            //    ExpressionHandler.requireScriptingEnvironment ( context, nodeName.localNameToken() );
            @NotNull ScriptingEnvironment scriptingEnvironment = context.requireScriptingEnvironment ( nodeName.token() );
//            scriptingEnvironment.executeScript (
//                ScriptingConstants.JAVASCRIPT_LANGUAGE_ID, script, bindings, true );
            scriptingEnvironment.executeScript ( code );
            // } catch ( PolyglotException e ) {
            // TODO See if a more precise error position can be reported, using methods in PolyglotException
        } catch ( ScriptingException e ) {
            scriptingError ( e, context, nodeName );
        }
    }
 */
}
