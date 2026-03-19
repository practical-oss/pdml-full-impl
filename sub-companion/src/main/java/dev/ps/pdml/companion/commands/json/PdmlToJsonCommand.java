package dev.ps.pdml.companion.commands.json;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.PdmlCommandsHelper;
import dev.ps.pdml.json.PdmlToJsonUtil;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CommandOutput;
import dev.ps.prt.command.output.ErrorCommandOutput;
import dev.ps.prt.command.output.VoidCommandOutput;
import dev.ps.prt.parameter.CommonParameters;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.io.IOException;
import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.PdmlCommands.APP_NAME;
import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class PdmlToJsonCommand extends Command {

    private static final @NotNull Parameter<Path> OPTIONAL_JSON_OUTPUT_FILE =
        CommonParameters.optionalOutputFile (
            "JSON Output File",
            "The path of the JSON output file.", true,
            "output/data.json" );

    // TODO
    // add parameter boolean include_whitespace_nodes
    // add parameter boolean include_node_paths (default=no)
    // add parameter boolean use_pretty_printing (default=yes)
    // ? add parameter boolean open_file (default=no)


    public static final @NotNull PdmlToJsonCommand COMMAND = new PdmlToJsonCommand();

    private PdmlToJsonCommand() {
        super (
            "PDML_to_JSON", "p2j",
            new Parameters ( OPTIONAL_PDML_INPUT_FILE, OPTIONAL_JSON_OUTPUT_FILE ),
            () -> new SimpleDocumentation (
                "Convert PDML to JSON",
                "Convert a PDML document to a JSON document.",
                APP_NAME + " p2j -i input/data.pdml -o output/data.json" ) );
    }


    public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @Nullable Path inputFile = arguments.nullableCastedValue ( OPTIONAL_PDML_INPUT_FILE );
        @Nullable Path outputFile = arguments.nullableCastedValue ( OPTIONAL_JSON_OUTPUT_FILE );

        try ( TextResourceReader pdmlReader =
                  TextResourceReader.createForOptionalFilePathOrStdin ( inputFile );
              TextResourceWriter jsonWriter =
                  TextResourceWriter.createForOptionalFilePathOrStdout ( outputFile, true ) ) {

            PdmlToJsonUtil.readerToWriter (
                pdmlReader, jsonWriter,
                PdmlParserConfig.defaultConfig(), true, true );

            PdmlCommandsHelper.fileCreatedMessageToStdout ( jsonWriter );
        } catch ( IOException | PdmlException e ) {
            return new ErrorCommandOutput ( e );
        }

        return VoidCommandOutput.INSTANCE;
    }
}
