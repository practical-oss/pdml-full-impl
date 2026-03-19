package dev.ps.pdml.xml;

import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.utils.treewalker.PdmlCodeWalker;
import dev.ps.pdml.utils.treewalker.handler.PdmlTreeWalkerEventHandler;
import dev.ps.pdml.xml.eventhandlers.CreateDOM_ParserEventHandler;
import dev.ps.pdml.xml.eventhandlers.WriteXMLParserEventHandler;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.IOResource;
import dev.ps.shared.text.utilities.file.TextFileReaderUtil;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

public class PdmlToXMLConverter {

    public static void PDMLFileToXMLFile ( @NotNull Path PDMLFile, @NotNull Path XMLFile ) throws Exception {

        final Reader PDMLFileReader = TextFileReaderUtil.createUTF8FileReader ( PDMLFile );
        final Writer XMLFileWriter = TextFileWriterUtil.createUTF8FileWriter ( XMLFile, true );
        pipePDMLReaderToXMLWriter ( PDMLFileReader, XMLFileWriter, new FileReaderResource ( PDMLFile ) );
        PDMLFileReader.close();
        XMLFileWriter.close();
    }

    public static void PDMLFileToXMLFile ( @NotNull String PDMLFilePath, @NotNull String XMLFilePath ) throws Exception {
        PDMLFileToXMLFile ( Path.of ( PDMLFilePath ), Path.of ( XMLFilePath ) );
    }

    // doesn't close reader nor writer
    public static void pipePDMLReaderToXMLWriter (
        @NotNull Reader pdmlReader,
        @NotNull Writer XMLWriter,
        @NotNull ReaderResource pdmlReaderResource ) throws Exception {

        PdmlParser parser = PdmlParser.create (
            pdmlReader, pdmlReaderResource, PdmlParserConfig.defaultConfig() );
        PdmlCodeWalker<NodeTag, String> eventBasedPdmlParser =
            new PdmlCodeWalker<> ( parser, new WriteXMLParserEventHandler ( XMLWriter ) );
        eventBasedPdmlParser.walk ();
    }

    public static @NotNull Document PDMLFileToXMLDocument ( @NotNull Path PDMLFile ) throws Exception {

        return PDMLToXMLDocument ( TextFileReaderUtil.createUTF8FileReader ( PDMLFile ), new FileReaderResource ( PDMLFile ) );
    }

    @Deprecated
    public static @NotNull Document PDMLToXMLDocument (
        @NotNull Reader pdmlReader,
        @Nullable IOResource ioResource ) throws Exception {

        return PDMLToXMLDocument ( pdmlReader, (ReaderResource) ioResource );
    }

    public static @NotNull Document PDMLToXMLDocument (
        @NotNull Reader pdmlReader,
        @NotNull ReaderResource pdmlReaderResource ) throws Exception {

        // long startTimeNanos = System.nanoTime();

        PdmlParser parser = PdmlParser.create (
            pdmlReader, pdmlReaderResource, PdmlParserConfig.defaultConfig() );
        PdmlTreeWalkerEventHandler<Node, Document> eventHandler = new CreateDOM_ParserEventHandler();
        PdmlCodeWalker<Node, Document> eventBasedPdmlParser =
            new PdmlCodeWalker<> ( parser, eventHandler );
        eventBasedPdmlParser.walk ();

        /*
        long endTimeNanos = System.nanoTime();
        long time = endTimeNanos - startTimeNanos;
        long micros = TimeUnit.NANOSECONDS.toMicros ( time );
        System.out.println ( "PXMLToXMLDocument time: " + String.valueOf ( micros ) + " microseconds" );
        */

        return eventHandler.getResult();
    }
}
