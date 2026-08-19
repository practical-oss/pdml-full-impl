package dev.ps.pdml.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

public class PdmlToJsonUtil {


    // Basic Methods

    public static @NotNull ObjectNode pdmlToJsonTree ( @NotNull TaggedNode pdmlRootNode ) {
        return new PdmlTreeToJsonTreeConverter().convert ( pdmlRootNode );
    }

    public static @NotNull ObjectNode pdmlReaderResourceToJsonTree (
        @NotNull ReaderResource pdmlReaderResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean removePrettyPrintingWhitespaceInTree ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull TaggedNode pdmlRootNode = ParseASTUtil.parseReaderResource (
            pdmlReaderResource, parserConfig );

        if ( removePrettyPrintingWhitespaceInTree ) {
            pdmlRootNode.removePrettyPrintingWhitespaceInTree();
        }

        return pdmlToJsonTree ( pdmlRootNode );
    }

    /*
    public static void pdmlTreeToJsonWriterResouce (
        @NotNull TaggedNode pdmlRootNode,
        @NotNull WriterResource jsonWriterResource,
        boolean usePrettyPrinting ) throws IOException {

        try ( Writer jsonWriter = jsonWriterResource. newWriter() ) {
            pdmlTreeToJsonWriter ( pdmlRootNode, jsonWriter, usePrettyPrinting );
        }
    }
     */

    public static void pdmlTreeToJsonWriter (
        @NotNull TaggedNode pdmlRootNode,
        @NotNull Writer jsonWriter,
        boolean usePrettyPrinting ) throws IOException {

        ObjectNode jsonObjectNode = pdmlToJsonTree ( pdmlRootNode );
        writeJsonTree ( jsonObjectNode, jsonWriter, usePrettyPrinting );
    }

    public static void pdmlToJsonResource (
        @NotNull ReaderResource pdmlReaderResource,
        @NotNull WriterResource jsonWriterResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean removePrettyPrintingWhitespaceInPdmlTree,
        boolean usePrettyPrintingInJson ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull ObjectNode jsonObjectNode = pdmlReaderResourceToJsonTree (
            pdmlReaderResource, parserConfig, removePrettyPrintingWhitespaceInPdmlTree );
        writeJsonTree ( jsonObjectNode, jsonWriterResource, usePrettyPrintingInJson );
    }


    // Convenience Methods

    public static @NotNull String pdmlTreeToJsonCode (
        @NotNull TaggedNode pdmlRootNode,
        boolean usePrettyPrinting ) throws JsonProcessingException {

        try ( StringWriter jsonWriter = new StringWriter() ) {
            pdmlTreeToJsonWriter ( pdmlRootNode, jsonWriter, usePrettyPrinting );
            return jsonWriter.toString();
        } catch ( IOException e ) {
            // should never happen
            throw new UncheckedIOException ( e );
        }
    }


    private static void writeJsonTree (
        @NotNull ObjectNode jsonObjectNode,
        @NotNull WriterResource jsonWriterResource,
        boolean usePrettyPrinting ) throws IOException {

        try ( Writer writer = jsonWriterResource.newWriter() ) {
            writeJsonTree ( jsonObjectNode, writer, usePrettyPrinting );
        }
    }

    private static void writeJsonTree (
        @NotNull ObjectNode jsonObjectNode,
        @NotNull Writer jsonWriter,
        boolean usePrettyPrinting ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        if ( usePrettyPrinting ) {
            objectMapper.writerWithDefaultPrettyPrinter();
        }

        // 'writeValue' may or may not close the writer. doc says:
        // Note: method does not close the underlying stream explicitly here; however, JsonFactory this mapper uses may choose to close the stream depending on its settings (by default, it will try to close it when JsonGenerator we construct is closed).
        // objectMapper.writeValue ( jsonWriter, jsonObjectNode );

        String string = objectMapper.writeValueAsString ( jsonObjectNode );
        jsonWriter.write ( string );
        jsonWriter.flush();
    }
}
