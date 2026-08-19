package dev.ps.pdml.companion.commands.html;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.prt.parameter.*;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.CommandsHelper;
import dev.ps.pdml.html.treeview.PdmlToHtmlTreeViewUtil;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.gui.DesktopUtil;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.prt.util.ReaderResourceUtil;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.prt.command.output.FailureCLICommandOutput;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.shared.text.ioresource.writer.WriterResource;
import dev.ps.prt.util.WriterResourceUtil;

import java.io.IOException;
import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class PdmlToHtmlTreeViewCommand extends CLICommand {

    private static final @NotNull Parameter<Path> HTML_OUTPUT_FILE_OR_STDOUT =
        CommonParameters.outputFileOrStdout (
            "HTML Output File",
            "The path of the HTML output file (relative or absolute).",
            "-o output/tree_view.html" );

    private static final @NotNull Parameter<Boolean> OPEN_BROWSER =
        ParameterBuilder.ofFlag ( "open-browser", "b" )
            .title ( "Open Browser" )
            .description ( "If this flag is set to true, the default OS browser will open the HTML output file." )
            .build();

    // TODO
    // add flag display-whitespace-nodes


    public static final @NotNull PdmlToHtmlTreeViewCommand INSTANCE = new PdmlToHtmlTreeViewCommand ();

    private PdmlToHtmlTreeViewCommand () {
        super (
            "pdml-to-html-tree", "p2h",
            new Parameters ( PDML_INPUT_FILE_OR_STDIN, HTML_OUTPUT_FILE_OR_STDOUT, OPEN_BROWSER ),
            () -> new SimpleDocumentation (
                "Convert PDML to an HTML Tree View",
                "Convert a PDML document to an HTML document that displays the PDML data as a tree view.",
                PdmlcApplication.CLI_APP_NAME + " p2h input/doc.pdml output/tree_view.html" ) );
    }


    public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {

        try {
            assert arguments != null;
            ReaderResource pdmlReaderResource = ReaderResourceUtil.createForFileOrStdinArgument (
                arguments, PDML_INPUT_FILE_OR_STDIN );
            WriterResource htmlWriterResource = WriterResourceUtil.createForFileOrStdoutArgument (
                arguments, HTML_OUTPUT_FILE_OR_STDOUT, true );

            PdmlToHtmlTreeViewUtil.pdmlReaderToHtmlWriterResource (
                pdmlReaderResource, htmlWriterResource,
                PdmlParserConfig.defaultConfig(), true );

            CommandsHelper.fileCreatedMessageToStdout ( htmlWriterResource );

            boolean openBrowser = arguments.nonNullCastedValue ( OPEN_BROWSER );
            if ( openBrowser && DesktopUtil.isDesktopSupported() ) {
                Path htmlFilePath = htmlWriterResource.resourceAsFilePath();
                if ( htmlFilePath != null ) {
                    DesktopUtil.openInDefaultBrowser ( htmlFilePath );
                }
            }

        } catch ( IOException | InvalidDataException e ) {
            return FailureCLICommandOutput.ofException ( e );
        }

        return SuccessCLICommandOutput.ofVoid();
    }
}
