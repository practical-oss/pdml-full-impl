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

public class DefinitionCommandNode {

    public static final @NotNull String NAME = "def";

    private static final Parameter<@NotNull String> CODE_PARAMETER = ParameterBuilder
        .ofString ( "code" )
        .isPositional ( true )
        .title ( "Java Class Code" )
        .description ( "Java source code defining a class." ).build();
        // TODO example


    public static final CommandNode INSTANCE = new CommandNodeBuilder ( NAME )
        .parameter ( CODE_PARAMETER )
        .executor ( DefinitionCommandNode::execute )
        .title ( "Java Class Definition" )
        .build();


    private static @Nullable CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException {

        assert arguments != null;

        @NotNull String code = arguments.nonNullStringValue ( CODE_PARAMETER.name() );

        try {
            @NotNull ScriptingEnvironment scriptingEnvironment = context.requireScriptingEnvironment ( nodeTag.tagLocation () );
            scriptingEnvironment.addDefinitions ( code );
            // } catch ( PolyglotException e ) {
            // TODO See if a more precise error position can be reported, using methods in PolyglotException
        } catch ( ScriptingException e ) {
            ScriptingCommandsUtil.scriptingError (
                "Invalid code. Reason:" + StringConstants.OS_LINE_BREAK + e.getMessage(),
                "INVALID_DEFINITION",
                nodeTag,
                e,
                context );
        }

        return null;
    }
}
