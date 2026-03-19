package dev.ps.pdml.companion.commands.tocore;

import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.PdmlCommandsHelper;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.utils.PdmlToCorePdmlUtil;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CommandOutput;
import dev.ps.prt.command.output.ErrorCommandOutput;
import dev.ps.prt.command.output.VoidCommandOutput;
import dev.ps.prt.parameter.Parameters;

import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.PdmlCommands.APP_NAME;
import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class PdmlToCorePdmlCommand extends Command {


    public static final @NotNull Command COMMAND = new PdmlToCorePdmlCommand();


    private PdmlToCorePdmlCommand() {

        super (
            "PDML_to_Core_PDML", "p2c",
            // TODO? add parameter keep_attributes
            new Parameters ( OPTIONAL_PDML_INPUT_FILE, OPTIONAL_PDML_OUTPUT_FILE ),
            () -> new SimpleDocumentation (
                "Convert PDML to Core PDML",
                "Convert a PDML document to a Core PDML document (i.e. a standalone PDML document that doesn't use PDML options)",
                APP_NAME + " p2c -i input/doc.pdml -o output/core_doc.pdml" )
         );
    }

    public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @Nullable Path inputFile = arguments.nonNullCastedValue ( OPTIONAL_PDML_INPUT_FILE.name() );
        @Nullable Path outputFile = arguments.nonNullCastedValue ( OPTIONAL_PDML_OUTPUT_FILE.name() );

        try ( TextResourceReader pdmlReader =
                  TextResourceReader.createForOptionalFilePathOrStdin ( inputFile );
              TextResourceWriter pdmlWriter =
                  TextResourceWriter.createForOptionalFilePathOrStdout ( outputFile, true ) ) {

            PdmlToCorePdmlUtil.pdmlCodeToCorePdml (
                pdmlReader, pdmlWriter,
                PdmlParserConfig.defaultConfig(), PdmlNodeWriterConfig.DEFAULT_CONFIG,
                false );

            PdmlCommandsHelper.fileCreatedMessageToStdout ( pdmlWriter );
        } catch ( Exception e ) {
            return new ErrorCommandOutput ( e );
        }

        return VoidCommandOutput.INSTANCE;
    }
}
