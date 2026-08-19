package dev.ps.pdml.companion.commands.json;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.CommandsHelper;
import dev.ps.pdml.json.JsonToPdmlUtil;
import dev.ps.pdml.json.JsonTreeToPdmlTreeConverter;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
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

public class JsonToPdmlCommand extends CLICommand {

    private static final @NotNull Parameter<Path> JSON_INPUT_FILE_OR_STDIN =
        CommonParameters.inputFileOrStdin (
            "JSON Input File",
            "The path of the JSON input file.",
            "-i input/data.json" );

    // TODO add config parameters


    public static final @NotNull JsonToPdmlCommand INSTANCE = new JsonToPdmlCommand();

    private JsonToPdmlCommand() {
        super (
            "json-to-pdml", "j2p",
            new Parameters ( JSON_INPUT_FILE_OR_STDIN, PDML_OUTPUT_FILE_OR_STDOUT ),
            () -> new SimpleDocumentation (
                "Convert JSON to PDML",
                "Convert a JSON document to a PDML document.",
                PdmlcApplication.CLI_APP_NAME + " j2p -i input/data.json -o output/data.pdml" ) );
    }


    public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {

        try {
            assert arguments != null;
            ReaderResource jsonReaderResource = ReaderResourceUtil.createForFileOrStdinArgument (
                arguments, JSON_INPUT_FILE_OR_STDIN );
            WriterResource pdmlWriterResource = WriterResourceUtil.createForFileOrStdoutArgument (
                arguments, PDML_OUTPUT_FILE_OR_STDOUT, true );

            JsonToPdmlUtil.jsonToPdmlResource (
                jsonReaderResource, pdmlWriterResource,
                JsonTreeToPdmlTreeConverter.DEFAULT_CONFIG,
                PdmlNodeWriterConfig.DEFAULT_CONFIG,
                false );

            CommandsHelper.fileCreatedMessageToStdout ( pdmlWriterResource );

        } catch ( IOException | InvalidDataException e ) {
            return FailureCLICommandOutput.ofException ( e );
        }

        return SuccessCLICommandOutput.ofVoid();
    }
}
