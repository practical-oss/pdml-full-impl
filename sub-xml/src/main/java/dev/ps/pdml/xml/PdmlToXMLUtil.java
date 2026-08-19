package dev.ps.pdml.xml;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class PdmlToXMLUtil {

    public static @NotNull Document pdmlToXMLTree (
        @NotNull TaggedNode pdmlRootNode ) throws ParserConfigurationException {

        return new PdmlTreeToXMLTreeConverter().convert ( pdmlRootNode );
    }

    public static @NotNull Document pdmlReaderResourceToXMLTree (
        @NotNull ReaderResource pdmlReaderResource,
        @NotNull PdmlParserConfig parserConfig )
            throws IOException, PdmlException, ParserConfigurationException {

        @NotNull TaggedNode pdmlRootNode = ParseASTUtil.parseReaderResource (
            pdmlReaderResource, parserConfig );
        return pdmlToXMLTree ( pdmlRootNode );
    }

    public static void pdmlToXMLResource (
        @NotNull ReaderResource pdmlReaderResource,
        @NotNull WriterResource xmlWriterResource,
        @NotNull PdmlParserConfig parserConfig )
        throws IOException, PdmlException, ParserConfigurationException, TransformerException {
        // boolean removeWhitespaceNodes,
        // boolean usePrettyPrinting ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull Document xmlDocument = pdmlReaderResourceToXMLTree (
            pdmlReaderResource, parserConfig );
        XMLUtilities.writeXMLDocument ( xmlDocument, xmlWriterResource );
    }
}
