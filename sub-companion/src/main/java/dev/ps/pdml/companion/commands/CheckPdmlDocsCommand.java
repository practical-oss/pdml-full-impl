package dev.ps.pdml.companion.commands;

import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.reader.StdinReaderResource;
import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.handler.TextInspectionMessageHandler;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CommandOutput;
import dev.ps.prt.command.output.ErrorCommandOutput;
import dev.ps.prt.command.output.ResultCommandOutput;
import dev.ps.prt.parameter.Parameters;

import java.nio.file.Path;
import java.util.List;

import static dev.ps.pdml.companion.commands.PdmlCommands.APP_NAME;
import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class CheckPdmlDocsCommand extends Command {


    public static final @NotNull CheckPdmlDocsCommand COMMAND = new CheckPdmlDocsCommand();


    private CheckPdmlDocsCommand() {
        super (
            "check_PDML_docs", "ch",
            new Parameters ( OPTIONAL_PDML_INPUT_FILES ),
            () -> new SimpleDocumentation (
                "Check PDML Documents For Errors",
                "Parse one or more PDML documents and report errors encountered.",
                APP_NAME + " ch -i input/document.pdml" ) );
    }


    public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @Nullable List<Path> inputFiles = arguments.nullableCastedValue ( OPTIONAL_PDML_INPUT_FILES );
        // DebugUtils.writeNameValue ( "arguments", arguments );
        // DebugUtils.writeNameValue ( "inputFiles", inputFiles );

        PdmlParserConfig config = PdmlParserConfig.defaultConfig();
        // boolean success = true;
        Exception parserException = null;

        if ( inputFiles != null ) {
            for ( Path pdmlFile : inputFiles ) {
                // try ( TextResourceReader reader = new TextResourceReader ( pdmlFile ) ) {
                //    ParseASTUtil.parseReader ( reader, config );
                try {
                    // ReaderResource reader = new FileReaderResource ( pdmlFile )
                    ParseASTUtil.parseReaderResource ( new FileReaderResource ( pdmlFile ), config );
                } catch ( Exception e ) {
                    reportError ( e );
                    if ( parserException == null ) {
                        parserException = e;
                    }
                }
            }
        } else {
            try {
                // ParseASTUtil.parseReader ( TextResourceReader.STDIN_READER, config );
                ParseASTUtil.parseReaderResource ( StdinReaderResource.INSTANCE, config );
            } catch ( Exception e ) {
                reportError ( e );
                parserException = e;
            }
        }

        if ( parserException == null ) {
            return new ResultCommandOutput<> ( "No errors detected." );
        } else {
            return new ErrorCommandOutput ( "Errors detected." );
        }
    }

    private static void reportError ( @NotNull Exception e ) {

        if ( e instanceof PdmlException pdmlException ) {
            TextInspectionMessageHandler handler = TextInspectionMessageHandler.newDefaultHandler();
            handler.handleMessage ( pdmlException.toTextInspectionError() );

        } else {
            System.err.println ( e.getMessage() );
        }
    }
}
