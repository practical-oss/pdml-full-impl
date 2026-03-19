package dev.ps.pdml.ext.scripting;

import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.shared.scriptingbase.env.ScriptingEnvironment;
import dev.ps.shared.scriptingbase.env.ScriptingException;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.ExtensionNodeHandlerContext;
import dev.ps.pdml.ext.ExtensionNodeHandler;
import dev.ps.pdml.ext.InsertReaderResourceExtensionResult;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.NewParam;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.io.IOException;

public class DefinitionHandler implements ExtensionNodeHandler {


    public static final @NotNull String NAME = "def";

    public static final DefinitionHandler INSTANCE = new DefinitionHandler();

    public static final Parameter<String> CODE_PARAMETER = NewParam.stringOrNull (
        "def", null, () -> null,
        () -> new SimpleDocumentation (
            "Code (Java Class)",
            "Java source code that defines a class.",
            null ) );

    public static final Parameters PARAMETERS = new Parameters ( CODE_PARAMETER );


    private DefinitionHandler (){}


    public @NotNull String getExtensionName() {
        return NAME;
    }

    @Override
    public @Nullable InsertReaderResourceExtensionResult handleNode ( @NotNull ExtensionNodeHandlerContext context, @NotNull NodeTag nodeName )
        throws IOException, PdmlException {

        @NotNull Arguments arguments = context.parseExtensionNodeArguments ( PARAMETERS, CODE_PARAMETER );
        @NotNull String code = arguments.nonNullStringValue ( CODE_PARAMETER.name() );

        try {
            @NotNull ScriptingEnvironment scriptingEnvironment = context.requireScriptingEnvironment ( nodeName.tagPositionOrRange() );
            scriptingEnvironment.addDefinitions ( code );
            // } catch ( PolyglotException e ) {
            // TODO See if a more precise error position can be reported, using methods in PolyglotException
        } catch ( ScriptingException e ) {
            ScriptingHandlerUtil.scriptingError (
                "Invalid code. Reason:" + StringConstants.OS_LINE_BREAK + e.getMessage(),
                "INVALID_DEFINITION",
                nodeName,
                e,
                context );
        }

        return null;
    }
}
