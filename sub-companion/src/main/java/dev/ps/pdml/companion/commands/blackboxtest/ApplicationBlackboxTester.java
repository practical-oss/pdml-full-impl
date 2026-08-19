package dev.ps.pdml.companion.commands.blackboxtest;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.file.FilePathUtils;
import dev.ps.shared.basics.utilities.os.OSName;
import dev.ps.shared.basics.utilities.os.process.OSCommand;
import dev.ps.shared.text.styling.AnsiEscapeSequences;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationBlackboxTester {


    public static class ApplicationTestException extends Exception {

        public ApplicationTestException ( @NotNull String message ) {
            super ( message );
        }
    }


    private final @NotNull String appName;
    private final @NotNull Path exeFilePath;
    private final @NotNull Path workingDir;


    public ApplicationBlackboxTester (
        @NotNull String appName,
        @NotNull Path exeFilePath,
        @NotNull Path workingDir ) {

        this.appName = appName;
        this.exeFilePath = exeFilePath;
        this.workingDir = workingDir;
    }


    public OSCommand. @NotNull StringResult assertOutput (
        @Nullable String stdIn,
        @Nullable String expectedStdOut,
        @Nullable String... commandLine )
            throws ApplicationTestException, IOException, InterruptedException {

        return testApp ( stdIn, expectedStdOut, "", 0, commandLine );
    }

    public OSCommand. @NotNull StringResult assertError (
        @Nullable String stdIn,
        @Nullable String expectedStdErr,
        int expectedExitCode,
        @Nullable String... commandLine )
            throws ApplicationTestException, IOException, InterruptedException {

        return testApp ( stdIn, null, expectedStdErr, expectedExitCode, commandLine );
    }

    public OSCommand. @NotNull StringResult testApp (
        @Nullable String stdIn,
        @Nullable String expectedStdOut,
        @Nullable String expectedStdErr,
        int expectedExitCode,
        @Nullable String... commandLine )
            throws ApplicationTestException, IOException, InterruptedException {

        List<String> commandTokens = new ArrayList<>();
        commandTokens.add ( FilePathUtils.makeAbsoluteAndNormalize ( exeFilePath ).toString() );
        if ( commandLine != null ) {
            commandTokens.addAll ( Arrays.asList ( commandLine ) );
        }

        String title = appName + " " + String.join ( " ", commandTokens.stream().skip ( 1 ).toList() );
        writeTestTitle ( title );

        writeIOE ( "In:  ", stdIn );

        OSCommand.StringResult commandResult = OSCommand.callWithStringsIO (
            commandTokens.toArray ( new String[0] ), stdIn, workingDir, null );

        String realStdOut = commandResult.output();
        writeIOE ( "Out: ", realStdOut );
        checkString ( realStdOut, expectedStdOut );

        String realStdErr = commandResult.error();
        writeIOE ( "Err: ", realStdErr );
        checkString ( realStdErr, expectedStdErr );

        System.out.println();

        if ( commandResult.exitCode() != expectedExitCode ) {
            String message = "Expected exit code: " + expectedExitCode + ". Actual exit code: " + commandResult.exitCode() + ".";
            System.err.println ( message );
            throw new ApplicationTestException ( message );
        }

        return commandResult;
    }

    private static void writeIOE ( @NotNull String title, @Nullable String content ) {

        if ( content == null ) return;

        System.out.print ( title );
        if ( content.contains ( "\n" ) ) {
            System.out.println();
        }
        // System.out.println ( trimString ( in, 80, true ) );
        System.out.println ( content );
    }

    private static void checkString (
        @Nullable String real,
        @Nullable String expected ) throws ApplicationTestException {

        if ( expected == null ) {
            return;
        } else if ( expected.isEmpty() ) {
            if ( real != null && ! real.isEmpty() ) {
                handleError ( "'Real' is not null or empty", real, expected );
            }
        } else if ( expected.startsWith ( ".." ) && expected.endsWith ( ".." ) ) {
            if ( real == null || ! real.contains ( expected.substring ( 2, expected.length() - 2 ) ) ) {
                handleError ( "'expected' is not contained in 'real'", real, expected );
            }
        } else if ( expected.startsWith ( ".." ) ) {
            if ( real == null || ! real.startsWith ( expected.substring ( 2 ) ) ) {
                handleError ( "'real' does not start with 'expected'", real, expected );
            }
        } else if ( expected.endsWith ( ".." ) ) {
            if ( real == null || ! real.endsWith ( expected.substring ( 0, expected.length() - 2 ) ) ) {
                handleError ( "'real' does not end with 'expected'", real, expected );
            }
        } else {
            if ( real == null || ! real.equals ( expected ) ) {
                handleError ( "'real' is not equal to 'expected'", real, expected );
            }
        }
    }

    private static void handleError (
        @NotNull String message,
        @Nullable String real,
        @Nullable String expected ) throws ApplicationTestException {

        /*
        System.err.println ( message );
        System.err.println ( "Expected: " + expected );
        System.err.println ( "Real:     " + real );
        throw new ApplicationTestException ( message );
         */
        String fullMessage = message + "\nExpected: " + expected + "\nReal:     " + real;
        System.err.println ( fullMessage );
        throw new ApplicationTestException ( fullMessage );
    }

    public static void writeGroupTitle ( String title ) {
        writeTitle ( title, "=" );
    }

    private static void writeTestTitle ( String title ) {
        writeTitle ( title, null );
    }

    private static void writeTitle ( @NotNull String title, @Nullable String underlineChar ) {

        System.out.print ( AnsiEscapeSequences.BOLD );
        System.out.println ( title );
        if ( underlineChar != null ) {
            System.out.println ( underlineChar.repeat ( title.length() ) );
        }
        System.out.print ( AnsiEscapeSequences.RESET_STYLE );
        System.out.println();
    }

    public static void askPressEnter() {

        System.out.println ( "Scroll up to see the results or" );
        System.out.print ( "press <Enter> to continue: " );
        try {
            System.in.read(); // Waits for a byte of input
            if ( OSName.isWindowsOS() ) {
                System.in.read (); // \r\n
            }
        } catch (IOException e) {
            throw new UncheckedIOException ( e );
        }

        System.out.print ( AnsiEscapeSequences.CLEAR_SCREEN +
            AnsiEscapeSequences.DELETE_SCROLLBACK_BUFFER +
            AnsiEscapeSequences.MOVE_TO_HOME );
    }

/*
    private static @NotNull String trimString (
        @NotNull String string,
        int maxLength,
        boolean replaceLineBreaks ) {

        if ( string.length() > maxLength ) {
            string = string.substring ( 0, maxLength ) + "\n...";
        }

        if ( replaceLineBreaks ) {
            string = string
                .replace ( "\r", "" )
                // .replace ( "\n", "␤" );
                .replace ( "\n", "{NL}" );
        }

        return string;
    }
 */
}
