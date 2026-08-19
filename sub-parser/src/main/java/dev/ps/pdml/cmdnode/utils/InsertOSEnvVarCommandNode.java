package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.cmdnode.*;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;

import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.DEFAULT_TEXT_PARAMETER;
import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.ESCAPE_TEXT_PARAMETER;

public class InsertOSEnvVarCommandNode {


    private static final @NotNull String NAME = "ins-env";

    private static final @NotNull Parameter<@NotNull String> VAR_NAME_PARAMETER = ParameterBuilder
        .ofString ( "var" )
        .isPositional ( true )
        .title ( "Environment Variable Name" )
        .description ( "The name of the OS environment variable whose content is to be inserted in the document." )
        .examples ( "HOME" ).build();

    public static final CommandNode INSTANCE = new CommandNodeBuilder ( NAME )
        .parameter ( VAR_NAME_PARAMETER )
        .parameter ( ESCAPE_TEXT_PARAMETER )
        .parameter ( DEFAULT_TEXT_PARAMETER )
        .executor ( InsertOSEnvVarCommandNode::execute )
        .title ( "Insert OS Environment Variable" )
        .build();


    private static @NotNull CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException {

        assert arguments != null;

        @NotNull String varName = arguments.nonNullCastedValue ( VAR_NAME_PARAMETER );

        @Nullable String value = System.getenv ( varName );
        // DebugUtils.writeNameValue ( "varName", varName );
        // DebugUtils.writeNameValue ( "value", value );
        if ( value == null ) {
            @Nullable String defaultValue = arguments.nullableCastedValue ( DEFAULT_TEXT_PARAMETER );
            if ( defaultValue != null ) {
                value = defaultValue;
            } else {
                throw context.error (
                    "OS environment variable '" + varName + "' is not defined and no default value is provided.",
                    "OS_ENV_VAR_NOT_DEFINED",
                    arguments.get ( VAR_NAME_PARAMETER.name() ).valueOrNameLocation() );
            }
        }

        // TODO? warning if value is empty

        boolean escapeText = arguments.nonNullCastedValue ( ESCAPE_TEXT_PARAMETER );
        return new CommandNodeResult ( value, escapeText );
    }
}
