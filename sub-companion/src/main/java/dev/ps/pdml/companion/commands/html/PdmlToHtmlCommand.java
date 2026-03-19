package dev.ps.pdml.companion.commands.html;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.PdmlCommandsHelper;
import dev.ps.pdml.html.treeview.PdmlToHtmlTreeViewUtil;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.gui.DesktopUtil;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CommandOutput;
import dev.ps.prt.command.output.ErrorCommandOutput;
import dev.ps.prt.command.output.VoidCommandOutput;
import dev.ps.prt.parameter.CommonParameters;
import dev.ps.prt.parameter.NewParam;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static dev.ps.pdml.companion.commands.PdmlCommands.APP_NAME;
import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class PdmlToHtmlCommand extends Command {

    private static final @NotNull Parameter<Path> OPTIONAL_HTML_OUTPUT_FILE =
        CommonParameters.optionalOutputFile (
            "HTML Output File",
            "The path of the HTML output file.", true,
            "output/tree_view.html" );

    private static final @NotNull Parameter<Boolean> OPEN_BROWSER = NewParam.ofBoolean (
        "open_browser", List.of ( "ob" ),
        () -> true, null  );

    // TODO
    // add parameter boolean display_whitespace_nodes

    public static final @NotNull PdmlToHtmlCommand COMMAND = new PdmlToHtmlCommand();


    private PdmlToHtmlCommand() {
        super (
            "PDML_to_HTML", "p2h",
            new Parameters ( OPTIONAL_PDML_INPUT_FILE, OPTIONAL_HTML_OUTPUT_FILE, OPEN_BROWSER ),
            () -> new SimpleDocumentation (
                "Convert PDML to HTML Tree View",
                "Convert a PDML document to an HTML document that displays the PDML data as a tree view.",
                APP_NAME + " p2h input/doc.pdml output/tree_view.html" ) );
    }

    public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @Nullable Path inputFile = arguments.nullableCastedValue ( OPTIONAL_PDML_INPUT_FILE.name() );
        @Nullable Path outputFile = arguments.nullableCastedValue ( OPTIONAL_HTML_OUTPUT_FILE.name() );

        try ( TextResourceReader pdmlReader =
                  TextResourceReader.createForOptionalFilePathOrStdin ( inputFile );
              TextResourceWriter pdmlWriter =
                  TextResourceWriter.createForOptionalFilePathOrStdout ( outputFile, true ) ) {

            PdmlToHtmlTreeViewUtil.readerToWriter (
                pdmlReader, pdmlWriter,
                PdmlParserConfig.defaultConfig(), true );

            PdmlCommandsHelper.fileCreatedMessageToStdout ( pdmlWriter );

            boolean openBrowser = arguments.nonNullCastedValue ( OPEN_BROWSER );
            if ( openBrowser && DesktopUtil.isDesktopSupported() ) {
                Path htmlFilePath = pdmlWriter.getResourceAsFilePath ();
                if ( htmlFilePath != null ) {
                    DesktopUtil.openInDefaultBrowser ( htmlFilePath );
                }
            }
        } catch ( IOException | PdmlException e ) {
            return new ErrorCommandOutput ( e );
        }

        return VoidCommandOutput.INSTANCE;
    }
}
