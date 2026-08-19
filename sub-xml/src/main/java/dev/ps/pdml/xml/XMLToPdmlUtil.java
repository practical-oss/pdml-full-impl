package dev.ps.pdml.xml;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.pdml.writer.node.PdmlNodeWriterUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Writer;

public class XMLToPdmlUtil {

    public static @NotNull TaggedNode xmlToPdmlTree (
        @NotNull Document xmlDocument ) {

        return new XMLTreeToPdmlTreeConverter().convert ( xmlDocument );
    }

    public static @NotNull TaggedNode xmlReaderResourceToPdmlTree (
        @NotNull ReaderResource xmlReaderResource )
            throws SAXException, ParserConfigurationException, IOException {

        Document xmlDocument = XMLUtilities.readXMLDocument ( xmlReaderResource );
        return xmlToPdmlTree ( xmlDocument );
    }

    public static void xmlToPdmlResource (
        @NotNull ReaderResource xmlReaderResource,
        @NotNull WriterResource pdmlWriterResource,
        @NotNull PdmlNodeWriterConfig pdmlCodeWriterConfig )
            throws SAXException, ParserConfigurationException, IOException {

        @NotNull TaggedNode pdmlRootNode = xmlReaderResourceToPdmlTree ( xmlReaderResource );
        writePdmlTree ( pdmlRootNode, pdmlWriterResource, pdmlCodeWriterConfig );
    }


    private static void writePdmlTree (
        @NotNull TaggedNode pdmlRootNode,
        @NotNull WriterResource pdmlWriterResource,
        @NotNull PdmlNodeWriterConfig pdmlCodeWriterConfig ) throws IOException {
        // boolean usePrettyPrinting ) throws IOException {

        // Could be made faster by piping from reader to writer (no need to build a tree)

        try ( Writer writer = pdmlWriterResource.newWriter() ) {
            PdmlNodeWriterUtil.write (
                writer, pdmlRootNode, false, pdmlCodeWriterConfig );
            writer.flush();
        }
    }
}
