package dev.ps.pdml.companion.commands.xml;

/*
import dev.pp.utils.pdml.SharedDefaultOptions;
import dev.pp.xml.pdml.PdmlToXMLConverter;
import dev.pp.core.annotations.basics.NotNull;
import dev.pp.core.annotations.basics.Nullable;
import dev.pp.core.utilities.basics.SimpleLogger;
import dev.pp.core.directory.utilities.basics.DirectoryCreator;
import dev.pp.core.command.commands.CommandSpec;
import dev.pp.core.parameters.parameters.Parameters;
import dev.pp.core.resource.text.File_TextResource;
import dev.pp.core.file.utilities.text.TextFileReaderUtil;
import dev.pp.core.file.utilities.text.TextFileWriterUtil;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Set;
 */

@Deprecated
public class PdmlToXMLCommandOld {

/*
    public static final @NotNull CommandSpec<Path,Void> COMMAND_SPEC = CommandSpec.<Path,Void>builder (
        "PDML_to_XML", PdmlToXMLCommandOld::execute )
        .alternativeNames ( Set.of ( "pdml2xml" ) )
        .inputParameters ( PdmlToXMLParameters.SPECS )
        .documentation ( "Convert PDML to XML",
            """
            Convert a PDML document into a standalone XML document.
            Note: This command is not suitable to convert PML documents into XML.""",
            "pdml pdml2xml input/doc.pdml output/doc.xml" )
        .build();

    public static Void execute ( @Nullable Parameters<Path> parameters ) throws Exception {

        assert parameters != null;

        @Nullable Path inputFile = parameters.nonNullValue ( PdmlToXMLParameters.PDML_INPUT_FILE );
        @Nullable Path outputFile = parameters.nonNullValue ( PdmlToXMLParameters.XML_OUTPUT_FILE );

        // TODO use TextResourceReader.createUTF8FileOrStdinReader ( inputFile )
        // see ExploreCommand
        try ( Reader reader = TextFileReaderUtil.createUTF8FileReader ( inputFile );
              Writer writer = TextFileWriterUtil.createUTF8FileWriter ( outputFile, true ) ) {

            PdmlToXMLConfig config = new PdmlToXMLConfig (
                reader,
                inputFile != null ? new File_TextResource ( inputFile ) : null,
                outputFile != null ? outputFile.getParent () : null,
                writer,
                SharedDefaultOptions.createMessageHandler (),
                SharedDefaultOptions.VERBOSITY,
                null );
            execute ( config );
        }

        return null;
    }


    public static void execute ( @NotNull PdmlToXMLConfig config ) throws Exception {

        @Nullable Path XMLOutputDirectory = config.XMLOutputDirectory();
        if ( XMLOutputDirectory != null ) {
            DirectoryCreator.createWithParentsIfNotExists ( XMLOutputDirectory );
        }

        PdmlToXMLConverter.pipePDMLReaderToXMLWriter (
            config.PMLInputReader(),
            config.XMLOutputWriter(),
            config.PMLInputTextResource() );

        if ( XMLOutputDirectory != null ) {
            SimpleLogger.info ( "Output stored in directory " + XMLOutputDirectory + "." );
        }
    }

 */
}
