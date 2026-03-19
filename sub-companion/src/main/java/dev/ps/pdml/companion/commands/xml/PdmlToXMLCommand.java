package dev.ps.pdml.companion.commands.xml;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.PdmlCommandsHelper;
import dev.ps.pdml.parser.PdmlParserConfigBuilder;
import dev.ps.pdml.xml.PdmlToXMLUtil;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.PdmlCommands.APP_NAME;
import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class PdmlToXMLCommand extends Command {

    private static final @NotNull Parameter<Path> OPTIONAL_XML_OUTPUT_FILE =
        CommonParameters.optionalOutputFile (
            "XML Output File",
            "The path of the XML output file.", true,
            "output/data.xml" );

    public static final @NotNull PdmlToXMLCommand COMMAND = new PdmlToXMLCommand();


    private PdmlToXMLCommand() {
        super (
            "PDML_to_XML", "p2x",
            new Parameters ( OPTIONAL_PDML_INPUT_FILE, OPTIONAL_XML_OUTPUT_FILE ),
            () -> new SimpleDocumentation (
                "Convert PDML to XML",
                "Convert a PDML document to an XML document.",
                APP_NAME + " p2x -i input/data.pdml -o output/data.xml" ) );
    }

    public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @Nullable Path inputFile = arguments.nullableCastedValue ( OPTIONAL_PDML_INPUT_FILE );
        @Nullable Path outputFile = arguments.nullableCastedValue ( OPTIONAL_XML_OUTPUT_FILE );

        try ( TextResourceReader pdmlReader =
                  TextResourceReader.createForOptionalFilePathOrStdin ( inputFile );
              TextResourceWriter xmlWriter =
                  TextResourceWriter.createForOptionalFilePathOrStdout ( outputFile, true ) ) {

            PdmlToXMLUtil.readerToWriter (
                pdmlReader, xmlWriter,
                // PdmlParserConfig.defaultConfig() );
                new PdmlParserConfigBuilder().ignoreComments ( false ).build() );

            PdmlCommandsHelper.fileCreatedMessageToStdout ( xmlWriter );
        } catch ( IOException | PdmlException | TransformerException | ParserConfigurationException e ) {
            return new ErrorCommandOutput ( e );
        }

        return VoidCommandOutput.INSTANCE;
    }
}
