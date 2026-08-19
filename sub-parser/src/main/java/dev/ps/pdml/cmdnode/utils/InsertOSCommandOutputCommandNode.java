package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.cmdnode.*;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.os.process.OSCommand;

import java.io.IOException;
import java.util.List;

import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.DEFAULT_TEXT_PARAMETER;
import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.ESCAPE_TEXT_PARAMETER;

// TODO
// This is an experimental version

public class InsertOSCommandOutputCommandNode {


    private static final @NotNull String NAME = "ins-cmdo";

    private static final @NotNull Parameter<@NotNull List<@NotNull String>> CMD_TOKENS_PARAMETER = ParameterBuilder
        .ofNonNullCSVStrings ( "cmd" )
        .title ( "Command Line Tokens" )
        .description ( "A comma-separated list of string literals (tokens) representing the command line (typically a command name, optionally followed by one or more CLI arguments." )
        .examples ( """
            [cmd my_tool_1.exe]
            [cmd path/to/my_tool_2.exe, --arg1, value1, --arg2, "value 2"]
            """ ).build();

    private static final @NotNull Parameter<@Nullable String> STDIN_PARAMETER = ParameterBuilder
        .ofStringOrNull ( "stdin", "i" )
        .defaultValue ( null )
        .title ( "STDIN String" )
        .description ( "The input string to the command (read via STDIN)." )
        .examples ( "-i \"command input string\"" ).build();

    public static final CommandNode INSTANCE = new CommandNodeBuilder ( NAME )
        .parameter ( CMD_TOKENS_PARAMETER )
        .parameter ( STDIN_PARAMETER )
        .parameter ( ESCAPE_TEXT_PARAMETER )
        .parameter ( DEFAULT_TEXT_PARAMETER )
        .executor ( InsertOSCommandOutputCommandNode::execute )
        .title ( "Insert OS Command Output" )
        .build();


    private static @Nullable CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException {

        assert arguments != null;

        @NotNull List<@NotNull String> cmdLineTokens = arguments.nonNullCastedValue ( CMD_TOKENS_PARAMETER );
        @Nullable String stdinString = arguments.nullableCastedValue ( STDIN_PARAMETER );
        // DebugUtils.writeNameValue ( "stdinString", stdinString );
        boolean escapeText = arguments.nonNullCastedValue ( ESCAPE_TEXT_PARAMETER );
        // TODO handle other parameters (default, ...)
        // TODO workingDirectory, envVars

        try {
            OSCommand.@NotNull StringResult result = OSCommand.callWithStringsIO (
                cmdLineTokens.toArray (new String[0]), stdinString, null, null );
            // DebugUtils.writeNameValue ( "result.output()", result.output() );
            @Nullable String output = result.output();
            if ( output != null ) {
                return new CommandNodeResult ( output, escapeText );
            } else {
                return null;
            }
        } catch ( InterruptedException e ) {
            throw new PdmlException ( e.getMessage(), "CMD_EXECUTION_ERROR", nodeTag.tagLocation(), e );
        }
    }
}
