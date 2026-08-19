package dev.ps.pdml.companion.commands;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.parameter.CommonParameters;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.inspection.message.TextInspectionMessageUtil;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.reader.StdinReaderResource;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.prt.command.output.FailureCLICommandOutput;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.prt.parameter.Parameters;

import java.nio.file.Path;
import java.util.List;

import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class CheckPdmlDocsCommand extends CLICommand {


    public static final @NotNull CheckPdmlDocsCommand INSTANCE = new CheckPdmlDocsCommand();

    private CheckPdmlDocsCommand() {
        super (
            "check-pdml-docs", "check",
            new Parameters ( PDML_INPUT_FILES_OR_STDIN ),
            () -> new SimpleDocumentation (
                "Check PDML Documents For Errors",
                "Parse one or more PDML documents and report errors encountered.",
                PdmlcApplication.CLI_APP_NAME + " check -i input/document.pdml" ) );
    }


    public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @NotNull List<Path> pdmlInputFiles = arguments.nonNullCastedValue ( PDML_INPUT_FILES_OR_STDIN );
        // DebugUtils.writeNameValue ( "arguments", arguments );
        // DebugUtils.writeNameValue ( "inputFiles", inputFiles );

        PdmlParserConfig config = PdmlParserConfig.defaultConfig();
        int errorCount = 0;

        for ( Path pdmlFile : pdmlInputFiles ) {
            try {
                ReaderResource readerResource = pdmlFile.equals ( CommonParameters.STDIN_FILE_PATH )
                    ? StdinReaderResource.INSTANCE
                    : new FileReaderResource ( pdmlFile );
                ParseASTUtil.parseReaderResource ( readerResource, config );
            } catch ( Exception e ) {
                reportError ( e );
                errorCount++;
            }
        }

        if ( errorCount == 0 ) {
            return SuccessCLICommandOutput.ofMessage ( "No errors detected." );
        } else {
            // String message = errorCount == 1 ? "One error reported." : errorCount + " errors reported.";
            // return new FailureCommandOutput ( message );
            return FailureCLICommandOutput.ofVoid();
        }
    }

    private static void reportError ( @NotNull Exception e ) {

        String message;
        if ( e instanceof InvalidDataException invalidDataException ) {
            message = TextInspectionMessageUtil.messageToDisplayString (
                invalidDataException.toTextInspectionError(), true );
        } else {
            message = e.getMessage();
        }
        System.err.println ( message );
    }
}
