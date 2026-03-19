package dev.ps.pdml.xml;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.IOResource;
import dev.ps.shared.text.utilities.file.TextFileReaderUtil;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

public class XSLTTransformer {

    public static void transformXMLWithXMLXSLT (
        @NotNull Reader XMLDataReader, @NotNull Reader XMLXSLTReader, @NotNull Writer outputWriter ) throws Exception {

        transformWithDocuments (
            XMLUtilities.readXMLDocument ( XMLDataReader ),
            XMLUtilities.readXMLDocument ( XMLXSLTReader ),
            outputWriter );
    }

    public static void transformXMLWithPXMLXSLT (
        @NotNull Reader XMLDataReader,
        @NotNull Reader pXMLXSLTReader, @Nullable IOResource pXMLXSLTResource,
        @NotNull Writer outputWriter ) throws Exception {

        transformWithDocuments (
            XMLUtilities.readXMLDocument ( XMLDataReader ),
            PdmlToXMLConverter.PDMLToXMLDocument ( pXMLXSLTReader, pXMLXSLTResource ),
            outputWriter );
    }

    public static void transformPXMLWithXMLXSLT (
        @NotNull Reader pXMLDataReader, @Nullable IOResource pXMLDataResource,
        @NotNull Reader XMLXSLTReader,
        @NotNull Writer outputWriter ) throws Exception {

        transformWithDocuments (
            PdmlToXMLConverter.PDMLToXMLDocument ( pXMLDataReader, pXMLDataResource ),
            XMLUtilities.readXMLDocument ( XMLXSLTReader ),
            outputWriter );
    }

    public static void transformPXMLFileWithPXMLXSLTFile (
        @NotNull Path pXMLFile, @NotNull Path pXMLXSLTFile, @NotNull Path outputFile ) throws Exception {

        transformPXMLWithPXMLXSLT (
            TextFileReaderUtil.createUTF8FileReader ( pXMLFile ), new FileReaderResource ( pXMLFile ),
            TextFileReaderUtil.createUTF8FileReader ( pXMLXSLTFile ), new FileReaderResource ( pXMLXSLTFile ),
            TextFileWriterUtil.createUTF8FileWriter ( outputFile, true ) );
    }

    public static void transformPXMLWithPXMLXSLT (
        @NotNull Reader pXMLDataReader, @Nullable IOResource pXMLDataResource,
        @NotNull Reader pXMLXSLTReader, @Nullable IOResource pXMLXSLTResource,
        @NotNull Writer outputWriter ) throws Exception {

        transformWithDocuments (
            PdmlToXMLConverter.PDMLToXMLDocument ( pXMLDataReader, pXMLDataResource ),
            PdmlToXMLConverter.PDMLToXMLDocument ( pXMLXSLTReader, pXMLXSLTResource ),
            outputWriter );
    }

    public static void transformWithDocuments (
        @NotNull Document XMLDocument, @NotNull Document XSLTDocument, @NotNull Writer outputWriter )
        throws Exception {

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer ( new DOMSource ( XSLTDocument ) );
        transformer.transform ( new DOMSource ( XMLDocument ), new StreamResult ( outputWriter ) );
    }
}
