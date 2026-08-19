package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.cmdnode.*;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.gui.DialogUtil;

import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.DEFAULT_TEXT_PARAMETER;
import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.ESCAPE_TEXT_PARAMETER;

public class InsertGUIInputCommandNode {

    private static final @NotNull String NAME = "ins-gui-input";

    private static final @NotNull Parameter<@NotNull String> PROMPT_PARAMETER = ParameterBuilder
        .ofString ( "prompt" )
        .isPositional ( true )
        .title ( "Prompt Message" )
        .description ( "The prompt message displayed in the GUI dialog." )
        .examples ( "[prompt Your name:]" ).build();

    private static final @NotNull Parameter<@NotNull String> WINDOW_TITLE_PARAMETER = ParameterBuilder
        .ofStringOrNull ( "title" )
        .defaultValue ( null )
        .isPositional ( true )
        .title ( "Window Title" )
        .description ( "The window title displayed in the GUI dialog." )
        .examples ( "[title Input requested]" ).build();

    public static final CommandNode INSTANCE = new CommandNodeBuilder ( NAME )
        .parameter ( PROMPT_PARAMETER )
        .parameter ( WINDOW_TITLE_PARAMETER )
        .parameter ( ESCAPE_TEXT_PARAMETER )
        .parameter ( DEFAULT_TEXT_PARAMETER )
        .executor ( InsertGUIInputCommandNode::execute )
        .title ( "Insert GUI input" )
        .examples ( "^[ins-gui-input [prompt Your name:]]" )
        .build();


    private static @Nullable CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) {

        assert arguments != null;

        @Nullable String windowTitle = arguments.nullableCastedValue ( WINDOW_TITLE_PARAMETER );
        @NotNull String prompt = arguments.nonNullCastedValue ( PROMPT_PARAMETER );
        boolean escapeText = arguments.nonNullCastedValue ( ESCAPE_TEXT_PARAMETER );

        @Nullable String value = DialogUtil.askString ( prompt, windowTitle );
        if ( value == null ) {
            @Nullable String defaultValue = arguments.nullableCastedValue ( DEFAULT_TEXT_PARAMETER );
            if ( defaultValue != null ) {
                value = defaultValue;
            } else {
                // TODO
                value = "";
                /*
                throw context.error (
                    "OS environment variable '" + varName + "' is not defined and no default value is provided.",
                    "OS_ENV_VAR_NOT_DEFINED",
                    arguments.get ( VAR_NAME_PARAMETER.name() ).valueOrNameLocation() );
                 */
            }
        }

        // TODO? warning if value is empty

        return new CommandNodeResult ( value, escapeText );
    }
}
