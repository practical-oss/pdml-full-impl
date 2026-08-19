package dev.ps.pdml.companion.commands.json;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.CommandsHelper;
import dev.ps.pdml.json.PdmlToJsonUtil;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.prt.util.ReaderResourceUtil;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.prt.command.output.FailureCLICommandOutput;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.prt.parameter.CommonParameters;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;
import dev.ps.shared.text.ioresource.writer.WriterResource;
import dev.ps.prt.util.WriterResourceUtil;

import java.io.IOException;
import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class PdmlToJsonCommand extends CLICommand {

    private static final @NotNull Parameter<Path> JSON_OUTPUT_FILE_OR_STDOUT =
        CommonParameters.outputFileOrStdout (
            "JSON Output File",
            "The path of the JSON output file.",
            "-o output/data.json" );

    // TODO
    // add parameter boolean include_whitespace_nodes
    // add parameter boolean include_node_paths (default=no)
    // add parameter boolean use_pretty_printing (default=yes)
    // ? add parameter boolean open_file (default=no)


    public static final @NotNull PdmlToJsonCommand INSTANCE = new PdmlToJsonCommand();

    private PdmlToJsonCommand() {
        super (
            "pdml-to-json", "p2j",
            new Parameters ( PDML_INPUT_FILE_OR_STDIN, JSON_OUTPUT_FILE_OR_STDOUT ),
            () -> new SimpleDocumentation (
                "Convert PDML to JSON",
                "Convert a PDML document to a JSON document.",
                PdmlcApplication.CLI_APP_NAME + " p2j -i input/data.pdml -o output/data.json" ) );
    }


    public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {

        try {
            assert arguments != null;
            ReaderResource pdmlReaderResource = ReaderResourceUtil.createForFileOrStdinArgument (
                arguments, PDML_INPUT_FILE_OR_STDIN );
            WriterResource jsonWriterResource = WriterResourceUtil.createForFileOrStdoutArgument (
                arguments, JSON_OUTPUT_FILE_OR_STDOUT, true );

            PdmlToJsonUtil.pdmlToJsonResource (
                pdmlReaderResource, jsonWriterResource,
                PdmlParserConfig.defaultConfig(), true, true );

            CommandsHelper.fileCreatedMessageToStdout ( jsonWriterResource );

        } catch ( IOException | InvalidDataException e ) {
            return FailureCLICommandOutput.ofException ( e );
        }

        return SuccessCLICommandOutput.ofVoid();
    }
}
