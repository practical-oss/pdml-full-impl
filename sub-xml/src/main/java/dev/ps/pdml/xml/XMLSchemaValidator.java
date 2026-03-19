package dev.ps.pdml.xml;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.IOResource;
import dev.ps.shared.text.utilities.file.TextFileReaderUtil;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.Reader;
import java.nio.file.Path;

public class XMLSchemaValidator {

    public static void validateXMLDataWithXMLSchema (
        @NotNull Reader XMLDataReader, @NotNull Reader XMLSchemaReader ) throws Exception {

        validateDocumentWithSchemaDocument (
            XMLUtilities.readXMLDocument ( XMLDataReader ),
            XMLUtilities.readXMLDocument ( XMLSchemaReader ) );
    }

    public static void validateXMLDataWithPXMLSchema (
        @NotNull Reader XMLDataReader, @NotNull Reader pXMLSchemaReader, @Nullable IOResource pXMLSchemaResource ) throws Exception {

        validateDocumentWithSchemaDocument (
            XMLUtilities.readXMLDocument ( XMLDataReader ),
            PdmlToXMLConverter.PDMLToXMLDocument ( pXMLSchemaReader, pXMLSchemaResource ) );
    }

    public static void validatePXMLDataWithXMLSchema (
        @NotNull Reader pXMLDataReader,
        @Nullable IOResource pXMLDataResource,
        @NotNull Reader XMLSchemaReader ) throws Exception {

        validateDocumentWithSchemaDocument (
            PdmlToXMLConverter.PDMLToXMLDocument ( pXMLDataReader, pXMLDataResource ),
            XMLUtilities.readXMLDocument ( XMLSchemaReader ) );
    }

    public static void validatePXMLFileWithPXMLSchemaFile (
        @NotNull Path pXMLDataFile, @NotNull Path pXMLSchemaFile ) throws Exception {

        validatePXMLDataWithPXMLSchema (
            TextFileReaderUtil.createUTF8FileReader ( pXMLDataFile ), new FileReaderResource ( pXMLDataFile ),
            TextFileReaderUtil.createUTF8FileReader ( pXMLSchemaFile ), new FileReaderResource ( pXMLSchemaFile ) );
    }

    public static void validatePXMLDataWithPXMLSchema (
        @NotNull Reader pXMLDataReader,
        @Nullable IOResource pXMLDataResource,
        @NotNull Reader pXMLSchemaReader,
        @Nullable IOResource pXMLSchemaResource ) throws Exception {

        validateDocumentWithSchemaDocument (
            PdmlToXMLConverter.PDMLToXMLDocument ( pXMLDataReader, pXMLDataResource ),
            PdmlToXMLConverter.PDMLToXMLDocument ( pXMLSchemaReader, pXMLSchemaResource ) );
    }

    public static void validateDocumentWithSchemaDocument (
        @NotNull Document XMLDocument, @NotNull Document XMLSchemaDocument ) throws Exception {

        SchemaFactory factory = SchemaFactory.newInstance ( XMLConstants.W3C_XML_SCHEMA_NS_URI );
        Schema schema = factory.newSchema ( new DOMSource( XMLSchemaDocument ) );
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate ( new DOMSource ( XMLDocument ) );
    }
}
