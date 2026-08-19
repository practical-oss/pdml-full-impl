package dev.ps.pdml.xml;

import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class XMLUtilities {

    /*
    public static @Nullable String getOfficialNamespaceURI ( @NotNull String namespacePrefix ) {

        if ( namespacePrefix.equals ( XMLConstants.XMLNS_ATTRIBUTE ) ) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else if ( namespacePrefix.equals ( XMLConstants.XML_NS_PREFIX ) ) {
            return XMLConstants.XML_NS_URI;
        } else {
            return null;
        }
    }
     */

    public static @NotNull String getQualifiedNameForNamespaceAttribute ( @NotNull String namespacePrefix ) {

        return XMLConstants.XMLNS_ATTRIBUTE + PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR + namespacePrefix;
    }


    // Read XML

    public static @NotNull Document readXMLDocument ( @NotNull ReaderResource xmlReaderResource )
        throws IOException, SAXException, ParserConfigurationException {

        try ( Reader reader = xmlReaderResource.newReader() ) {
            return readXMLDocument ( reader );
        }
    }

    public static @NotNull Document readXMLDocument ( @NotNull Reader reader )
        throws IOException, SAXException, ParserConfigurationException {

        // long startTimeNanos = System.nanoTime();

        // DocumentBuilderFactory factory = DocumentBuilderFactory.newNSInstance(); // Java version >= 13
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware ( true ); // important!

        DocumentBuilder builder = factory.newDocumentBuilder();

        /*
        Document result = builder.parse ( new InputSource( reader ) );
        long endTimeNanos = System.nanoTime();
        long time = endTimeNanos - startTimeNanos;
        long micros = TimeUnit.NANOSECONDS.toMicros ( time );
        System.out.println ( "readXMLDocument time " + String.valueOf ( micros ) + " microseconds" );
        return result;
        */

        return builder.parse ( new InputSource( reader ) );
    }


    // Write XML

    public static void writeXMLDocument (
        @NotNull Document document,
        @NotNull WriterResource writerResource ) throws IOException, TransformerException {

        try ( Writer writer = writerResource.newWriter() ) {
            writeXMLDocument ( document, writer );
        }
    }

    public static void writeXMLDocument (
        @NotNull Document document,
        @NotNull Writer writer ) throws IOException, TransformerException {

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMSource source = new DOMSource ( document );

        // transformer.setOutputProperty ( OutputKeys.INDENT, "yes" );
        // transformer.setOutputProperty ( "{http://xml.apache.org/xslt}indent-amount", "4" );
        // transformer.setOutputProperty ( OutputKeys.OMIT_XML_DECLARATION, "yes" );

        transformer.transform ( source, new StreamResult ( writer ) );
        writer.flush();
    }
}
