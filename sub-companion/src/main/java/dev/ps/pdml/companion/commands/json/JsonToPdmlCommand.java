package dev.ps.pdml.companion.commands.json;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.PdmlCommandsHelper;
import dev.ps.pdml.json.JsonToPdmlUtil;
import dev.ps.pdml.json.JsonTreeToPdmlTreeConverter;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
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

public class JsonToPdmlCommand extends Command {

    private static final @NotNull Parameter<Path> OPTIONAL_JSON_INPUT_FILE =
        CommonParameters.optionalInputFile (
            "JSON Input File",
            "The path of the JSON input file.", true,
            "input/data.json" );

    // TODO add config parameters

    public static final @NotNull JsonToPdmlCommand COMMAND = new JsonToPdmlCommand();


    private JsonToPdmlCommand() {
        super (
            "JSON_to_PDML", "j2p",
            new Parameters ( OPTIONAL_JSON_INPUT_FILE, OPTIONAL_PDML_OUTPUT_FILE ),
            () -> new SimpleDocumentation (
                "Convert JSON to PDML",
                "Convert a JSON document to a PDML document.",
                APP_NAME + " j2p -i input/data.json -o output/data.pdml" ) );
    }


    public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @Nullable Path jsonInputFile = arguments.nullableCastedValue ( OPTIONAL_JSON_INPUT_FILE );
        @Nullable Path pdmlOutputFile = arguments.nullableCastedValue ( OPTIONAL_PDML_OUTPUT_FILE );
        try ( TextResourceReader jsonReader =
                  TextResourceReader.createForOptionalFilePathOrStdin ( jsonInputFile );
              TextResourceWriter pdmlWriter =
                  TextResourceWriter.createForOptionalFilePathOrStdout ( pdmlOutputFile, true ) ) {

            JsonToPdmlUtil.readerToWriter (
                jsonReader, pdmlWriter,
                JsonTreeToPdmlTreeConverter.DEFAULT_CONFIG,
                PdmlNodeWriterConfig.DEFAULT_CONFIG,
                false );

            PdmlCommandsHelper.fileCreatedMessageToStdout ( pdmlWriter );
        } catch ( IOException | PdmlException e ) {
            return new ErrorCommandOutput ( e );
        }

        return VoidCommandOutput.INSTANCE;
    }
}
