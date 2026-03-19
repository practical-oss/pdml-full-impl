package dev.ps.pdml.companion.commands;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.utilities.file.TextFileReaderUtil;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestHelper {

    public static void testCLICommandWithInputOutputFile (
        @NotNull String commandName,
        @NotNull Path workingDirectory,
        @NotNull String inputFileContent,
        @NotNull String expectedOutputFileContent ) throws IOException {

        testCLICommandWithInputOutputFile (
            commandName, workingDirectory,
            "input.pdml", "output.pdml",
            inputFileContent, expectedOutputFileContent, null, false );
    }

    public static void testCLICommandWithInputOutputFile (
        @NotNull String commandName,
        @NotNull Path workingDirectory,
        @NotNull String inputFilePath,
        @NotNull String outputFilePath,
        @NotNull String inputFileContent,
        @NotNull String expectedOutputFileContent,
        @Nullable List<String> additionalCLIArguments,
        boolean assertExpectedOutputIsContained ) throws IOException {

        Path inputFile = workingDirectory.resolve ( inputFilePath );
        Path outputFile = workingDirectory.resolve ( outputFilePath );
        TextFileWriterUtil.writeStringToUTF8File ( inputFileContent, inputFile, true );
        Files.deleteIfExists ( outputFile );
        assertTrue ( Files.exists ( inputFile ) );
        assertFalse ( Files.exists ( outputFile ) );

        List<String> cliArguments = new ArrayList<> ();
        cliArguments.add ( commandName );
        // cliArguments.add ( "--input" );
        cliArguments.add ( "-i" );
        cliArguments.add ( inputFile.toString() );
        cliArguments.add ( "--output" );
        cliArguments.add ( outputFile.toString() );
        if ( additionalCLIArguments != null ) {
            cliArguments.addAll ( additionalCLIArguments );
        }

        PdmlCommands.runCommand ( cliArguments.toArray ( String[]::new ), false );

        assertTrue ( Files.exists ( outputFile ) );
        String result = TextFileReaderUtil.readAllFromUTF8File ( outputFile );
        assertNotNull ( result );

        result = result.replace ( "\r\n", "\n" );
        expectedOutputFileContent = expectedOutputFileContent.replace ( "\r\n", "\n" );
        if ( ! assertExpectedOutputIsContained ) {
            assertEquals ( expectedOutputFileContent, result );
        } else {
            assertTrue ( result.contains ( expectedOutputFileContent ) );
        }
    }
}
