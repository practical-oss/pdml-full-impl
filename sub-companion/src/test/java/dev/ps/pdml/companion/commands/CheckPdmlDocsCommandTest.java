package dev.ps.pdml.companion.commands;

import dev.ps.shared.text.utilities.file.TextFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class CheckPdmlDocsCommandTest {


    @TempDir Path tempDir;

    @Test
    void execute() throws IOException {

        // Invalid Code

        Path inputFile = tempDir.resolve ( "input.pdml" );
        TextFileWriterUtil.writeStringToUTF8File ( "[root text", inputFile, true );

        List<String> cliArguments = new ArrayList<> ();
        cliArguments.add ( "check_PDML_docs" );
        cliArguments.add ( "--input" );
        cliArguments.add ( inputFile.toString() );


        // TODO when test is run, causes: org.gradle.internal.remote.internal.MessageIOException: Could not write '/127.0.0.1:54897'.

/*
        @Nullable String stdErrOutput = OSIO.captureStderr ( () -> {
            try {
                PdmlCommands.runCommand ( cliArguments.toArray ( String[]::new ), false );
            } catch ( Exception e ) {}
        });
        assertNotNull ( stdErrOutput );
        DebugUtils.writeNameValue ( "stdErrOutput", stdErrOutput );
        assertTrue ( stdErrOutput.contains ( "EXPECTING_NODE_END" ) );


        // Valid Code
        TextFileWriterUtil.writeStringToUTF8File ( "[root text]", inputFile, true );
        stdErrOutput = OSIO.captureStderr ( () -> {
            try {
                PdmlCommands.runCommand ( cliArguments.toArray ( String[]::new ), false );
            } catch ( Exception e ) {}
        });
        assertNull ( stdErrOutput );
 */
    }
}
