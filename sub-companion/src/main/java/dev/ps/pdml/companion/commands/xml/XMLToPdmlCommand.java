package dev.ps.pdml.companion.commands.xml;

import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.PdmlCommandsHelper;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.pdml.xml.XMLToPdmlUtil;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.PdmlCommands.APP_NAME;
import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class XMLToPdmlCommand extends Command {

    private static final @NotNull Parameter<Path> OPTIONAL_XML_INPUT_FILE =
        CommonParameters.optionalInputFile (
            "XML Input File",
            "The path of the XML input file.", true,
            "input/data.xml" );

    public static final @NotNull XMLToPdmlCommand COMMAND = new XMLToPdmlCommand();


    private XMLToPdmlCommand() {
        super (
            "XML_to_PDML", "x2p",
            new Parameters ( OPTIONAL_XML_INPUT_FILE, OPTIONAL_PDML_OUTPUT_FILE ),
            () -> new SimpleDocumentation (
                "Convert XML to PDML",
                "Convert an XML document into a PDML document",
                APP_NAME + " x2p -i input/data.xml -o output/data.pdml" ) );
    }


    public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @Nullable Path xmlInputFile = arguments.nullableCastedValue ( OPTIONAL_XML_INPUT_FILE );
        @Nullable Path pdmlOutputFile = arguments.nullableCastedValue ( OPTIONAL_PDML_OUTPUT_FILE );
        try ( TextResourceReader xmlReader =
                  TextResourceReader.createForOptionalFilePathOrStdin ( xmlInputFile );
              TextResourceWriter pdmlWriter =
                  TextResourceWriter.createForOptionalFilePathOrStdout ( pdmlOutputFile, true ) ) {

            XMLToPdmlUtil.readerToWriter (
                xmlReader, pdmlWriter,
                PdmlNodeWriterConfig.DEFAULT_CONFIG );

            PdmlCommandsHelper.fileCreatedMessageToStdout ( pdmlWriter );
        } catch ( IOException | ParserConfigurationException | SAXException e ) {
            return new ErrorCommandOutput ( e );
        }

        return VoidCommandOutput.INSTANCE;
    }
}
