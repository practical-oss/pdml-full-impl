package dev.ps.pdml.ext.scripting;

import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.ExtensionNodeHandlerContext;
import dev.ps.pdml.ext.ExtensionNodeHandler;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.ext.InsertReaderResourceExtensionResult;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.shared.scriptingbase.env.ScriptingEnvironment;
import dev.ps.shared.scriptingbase.env.ScriptingException;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.NewParam;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.io.IOException;

public class ScriptHandler implements ExtensionNodeHandler {


    public static final @NotNull String NAME = "script";

    public static final ScriptHandler INSTANCE = new ScriptHandler ();

    public static final Parameter<String> CODE_PARAMETER = NewParam.stringOrNull (
        "code", null, () -> null,
        () -> new SimpleDocumentation (
            "Code",
            "A list of Java statement (called script).",
            "System.out.println ( \"Hello\");" ) );

    public static final Parameters PARAMETERS = new Parameters ( CODE_PARAMETER );


    private ScriptHandler (){}


    public @NotNull String getExtensionName() {
        return NAME;
    }

    @Override
    public @Nullable InsertReaderResourceExtensionResult handleNode ( @NotNull ExtensionNodeHandlerContext context, @NotNull NodeTag nodeName )
        throws IOException, PdmlException {

        @NotNull Arguments arguments = context.parseExtensionNodeArguments ( PARAMETERS, CODE_PARAMETER );
        @NotNull String script = arguments.nonNullStringValue ( CODE_PARAMETER.name() );
        executeJavaCode ( script, context, nodeName );

        return null;
    }

    private void executeJavaCode (
        @NotNull String code,
        @NotNull ExtensionNodeHandlerContext context,
        @NotNull NodeTag nodeName ) throws PdmlException {

        try {
            ScriptingEnvironment scriptingEnvironment = context.requireScriptingEnvironment ( nodeName.tagPositionOrRange() );
            scriptingEnvironment.executeScript ( code );
        } catch ( ScriptingException e ) {
            ScriptingHandlerUtil.scriptingError (
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
