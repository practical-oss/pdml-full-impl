package dev.ps.pdml.xml;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.pdml.writer.node.PdmlNodeWriterUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class XMLToPdmlUtil {


    // Basic Methods

    public static @NotNull TaggedNode treeToTree (
        @NotNull Document xmlDocument ) {

        return new XMLTreeToPdmlTreeConverter().convert ( xmlDocument );
    }

    public static @NotNull TaggedNode readerToTree (
        @NotNull TextResourceReader xmlCodeReader )
        throws SAXException, ParserConfigurationException, IOException {

        Document xmlDocument = XMLUtilities.readXMLDocument ( xmlCodeReader );
        return treeToTree ( xmlDocument );
    }

    public static void treeToWriter (
        @NotNull Document xmlDocument,
        @NotNull TextResourceWriter pdmlCodeWriter,
        @NotNull PdmlNodeWriterConfig pdmlCodeWriterConfig ) throws IOException {
        // boolean usePrettyPrinting ) throws IOException {

        TaggedNode pdmlRootNode = treeToTree ( xmlDocument );
        writePdmlTree ( pdmlRootNode, pdmlCodeWriter, pdmlCodeWriterConfig );
    }

    public static void readerToWriter (
        @NotNull TextResourceReader xmlCodeReader,
        @NotNull TextResourceWriter pdmlCodeWriter,
        @NotNull PdmlNodeWriterConfig pdmlCodeWriterConfig )
            throws SAXException, ParserConfigurationException, IOException {

        @NotNull TaggedNode pdmlRootNode = readerToTree ( xmlCodeReader );
        writePdmlTree ( pdmlRootNode, pdmlCodeWriter, pdmlCodeWriterConfig );
    }

    private static void writePdmlTree (
        @NotNull TaggedNode pdmlRootNode,
        @NotNull TextResourceWriter pdmlCodeWriter,
        @NotNull PdmlNodeWriterConfig pdmlCodeWriterConfig ) throws IOException {
        // boolean usePrettyPrinting ) throws IOException {

        // Could be made faster by piping from reader to writer (no need to build a tree)

        PdmlNodeWriterUtil.write (
            pdmlCodeWriter.getWriter(), pdmlRootNode, false, pdmlCodeWriterConfig );
    }


/*
    private static @NotNull JsonNode parseJson (
        @NotNull TextResourceReader jsonCodeReader ) throws IOException {

        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readTree ( jsonCodeReader.getReader() );
    }

 */


    // Convenience Methods
}
