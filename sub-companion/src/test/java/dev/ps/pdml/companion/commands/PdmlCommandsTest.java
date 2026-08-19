package dev.ps.pdml.companion.commands;

/*
import dev.ps.pdml.data.PdmlVersion;
import dev.ps.prt.command.output.CommandOutput;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.os.process.OSCommand;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
 */

@Deprecated
class PdmlCommandsTest {

/*
    @Test
    void basics() throws IOException, InterruptedException {

        testSuccessCommand ( "info", null, null, "PDML Companion" );
        testSuccessCommand ( "version", null, null, PdmlVersion.VERSION );
    }

    @Test
    void CLIErrors() throws IOException, InterruptedException {
        testFailureCommand ( "invalid_command", null, null, CommandOutput.COMMAND_NOT_FOUND_OS_EXIT_CODE, "Invalid command" );
    }

    @Test
    void invalidCorePDMLErrors() throws IOException, InterruptedException {
        // TODO input doesn't work (freezes)
        // testFailureCommand ( "p2c", null, "[root]", "Invalid command" );
    }

    private OSCommand. @NotNull StringResult testSuccessCommand (
        @NotNull String commandName,
        @Nullable String[] arguments,
        @Nullable String input,
        @Nullable String expectedOutContains ) throws IOException, InterruptedException {

        return testCommand ( commandName, arguments, input, 0, expectedOutContains, null );
    }

    private OSCommand. @NotNull StringResult testFailureCommand (
        @NotNull String commandName,
        @Nullable String[] arguments,
        @Nullable String input,
        @Nullable String expectedErrContains ) throws IOException, InterruptedException {

        return testFailureCommand ( commandName, arguments, input, 1, expectedErrContains );
    }

    private OSCommand. @NotNull StringResult testFailureCommand (
        @NotNull String commandName,
        @Nullable String[] arguments,
        @Nullable String input,
        int expectedExitCode,
        @Nullable String expectedErrContains ) throws IOException, InterruptedException {

        return testCommand ( commandName, arguments, input, expectedExitCode, null, expectedErrContains );
    }

    private OSCommand. @NotNull StringResult testCommand (
        @NotNull String commandName,
        @Nullable String[] arguments,
        @Nullable String input,
        int expectedExitCode,
        @Nullable String expectedOutContains,
        @Nullable String expectedErrContains ) throws IOException, InterruptedException {

        List<String> commandTokens = new ArrayList<>();

        // TODO use env var
        commandTokens.add ( "C:\\aa\\work\\PS_Projects\\PDML\\dev\\full-pdml-impl\\app\\build\\install\\app\\bin\\pdmlc.bat" );
        commandTokens.add ( commandName );
        if ( arguments != null ) {
            commandTokens.addAll ( Arrays.asList ( arguments ) );
        }

        OSCommand.StringResult commandResult = OSCommand.runWithStrings (
            commandTokens.toArray ( new String[0] ), input, null, null );

        assertEquals ( commandResult.exitCode(), expectedExitCode );

        String outString = commandResult.stdout();
        if ( expectedOutContains != null ) {
            assertNotNull ( outString );
            assert ( outString.contains ( expectedOutContains ) );
        } else {
            assertNull ( outString );
        }
        if ( outString != null ) {
            System.out.print ( outString );
        }

        String errString = commandResult.stderr();
        if ( expectedErrContains != null ) {
            assertNotNull ( errString );
            assert ( errString.contains ( expectedErrContains ) );
        } else {
            assertNull ( errString );
        }
        if ( errString != null ) {
            System.err.print ( errString );
        }

        return commandResult;
    }

 */
}
