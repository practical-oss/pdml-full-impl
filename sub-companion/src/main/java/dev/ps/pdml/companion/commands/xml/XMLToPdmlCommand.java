package dev.ps.pdml.companion.commands.xml;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.CommandsHelper;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.pdml.xml.XMLToPdmlUtil;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class XMLToPdmlCommand extends CLICommand {

    private static final @NotNull Parameter<Path> XML_INPUT_FILE_OR_STDIN =
        CommonParameters.inputFileOrStdin (
            "XML Input File",
            "The path of the XML input file.",
            "-i input/data.xml" );

    public static final @NotNull XMLToPdmlCommand INSTANCE = new XMLToPdmlCommand();

    private XMLToPdmlCommand() {
        super (
            "xml-to-pdml", "x2p",
            new Parameters ( XML_INPUT_FILE_OR_STDIN, PDML_OUTPUT_FILE_OR_STDOUT ),
            () -> new SimpleDocumentation (
                "Convert XML to PDML",
                "Convert an XML document into a PDML document",
                PdmlcApplication.CLI_APP_NAME + " x2p -i input/data.xml -o output/data.pdml" ) );
    }


    public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {

        try {
            assert arguments != null;
            ReaderResource xmlReaderResource = ReaderResourceUtil.createForFileOrStdinArgument (
                arguments, XML_INPUT_FILE_OR_STDIN );
            WriterResource pdmlWriterResource = WriterResourceUtil.createForFileOrStdoutArgument (
                arguments, PDML_OUTPUT_FILE_OR_STDOUT, true );

            XMLToPdmlUtil.xmlToPdmlResource (
                xmlReaderResource, pdmlWriterResource,
                PdmlNodeWriterConfig.DEFAULT_CONFIG );

            CommandsHelper.fileCreatedMessageToStdout ( pdmlWriterResource );

        } catch ( IOException | InvalidDataException | ParserConfigurationException | SAXException e ) {
            return FailureCLICommandOutput.ofException ( e );
        }

        return SuccessCLICommandOutput.ofVoid();
    }
}
