package dev.ps.pdml.companion.commands.xml;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.CommandsHelper;
import dev.ps.pdml.parser.PdmlParserConfigBuilder;
import dev.ps.pdml.xml.PdmlToXMLUtil;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class PdmlToXMLCommand extends CLICommand {

    private static final @NotNull Parameter<Path> XML_OUTPUT_FILE_OR_STDOUT =
        CommonParameters.outputFileOrStdout (
            "XML Output File",
            "The path of the XML output file.",
            "-o output/data.xml" );


    public static final @NotNull PdmlToXMLCommand INSTANCE = new PdmlToXMLCommand();

    private PdmlToXMLCommand() {
        super (
            "pdml-to-xml", "p2x",
            new Parameters ( PDML_INPUT_FILE_OR_STDIN, XML_OUTPUT_FILE_OR_STDOUT ),
            () -> new SimpleDocumentation (
                "Convert PDML to XML",
                "Convert a PDML document to an XML document.",
                PdmlcApplication.CLI_APP_NAME + " p2x -i input/data.pdml -o output/data.xml" ) );
    }


    public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {

        try {
            assert arguments != null;
            ReaderResource pdmlReaderResource = ReaderResourceUtil.createForFileOrStdinArgument (
                arguments, PDML_INPUT_FILE_OR_STDIN );
            WriterResource xmlWriterResource = WriterResourceUtil.createForFileOrStdoutArgument (
                arguments, XML_OUTPUT_FILE_OR_STDOUT, true );

            PdmlToXMLUtil.pdmlToXMLResource (
                pdmlReaderResource, xmlWriterResource,
                // PdmlParserConfig.defaultConfig() );
                new PdmlParserConfigBuilder().ignoreComments ( false ).build() );

            CommandsHelper.fileCreatedMessageToStdout ( xmlWriterResource );

        } catch ( IOException | InvalidDataException | TransformerException | ParserConfigurationException e ) {
            return FailureCLICommandOutput.ofException ( e );
        }

        return SuccessCLICommandOutput.ofVoid();
    }
}
