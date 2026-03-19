package dev.ps.pdml.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;

import java.io.IOException;
import java.io.StringWriter;

public class PdmlToJsonUtil {


    // Basic Methods

    public static @NotNull ObjectNode treeToTree ( @NotNull TaggedNode pdmlRootNode ) {
        return new PdmlTreeToJsonTreeConverter().convert ( pdmlRootNode );
    }

    public static @NotNull ObjectNode readerToTree (
        @NotNull TextResourceReader pdmlCodeReader,
        @NotNull PdmlParserConfig parserConfig,
        boolean removeWhitespaceNodes ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull TaggedNode pdmlRootNode = ParseASTUtil.parseReader (
            pdmlCodeReader, parserConfig );

        if ( removeWhitespaceNodes ) {
            pdmlRootNode.removeWhitespaceTextLeafsInTree ( false );
        }

        return treeToTree ( pdmlRootNode );
    }

    public static void treeToWriter (
        @NotNull TaggedNode pdmlRootNode,
        @NotNull TextResourceWriter jsonCodeWriter,
        boolean usePrettyPrinting ) throws IOException {

        ObjectNode jsonObjectNode = treeToTree ( pdmlRootNode );
        writeJsonTree ( jsonObjectNode, jsonCodeWriter, usePrettyPrinting );
    }

    public static void readerToWriter (
        @NotNull TextResourceReader pdmlCodeReader,
        @NotNull TextResourceWriter jsonCodeWriter,
        @NotNull PdmlParserConfig parserConfig,
        boolean removeWhitespaceNodes,
        boolean usePrettyPrinting ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull ObjectNode jsonObjectNode = readerToTree (
            pdmlCodeReader, parserConfig, removeWhitespaceNodes );
        writeJsonTree ( jsonObjectNode, jsonCodeWriter, usePrettyPrinting );
    }


    // Convenience Methods

    public static @NotNull String treeToCode (
        @NotNull TaggedNode pdmlRootNode,
        boolean usePrettyPrinting ) throws JsonProcessingException {

        try ( StringWriter stringWriter = new StringWriter();
            TextResourceWriter jsonCodeWriter = new TextResourceWriter ( stringWriter, null ) ) {
            treeToWriter ( pdmlRootNode, jsonCodeWriter, usePrettyPrinting );
            return stringWriter.toString();
        } catch ( IOException e ) {
            // should never happen
            throw new RuntimeException ( e );
        }
    }


    private static void writeJsonTree (
        @NotNull ObjectNode jsonObjectNode,
        @NotNull TextResourceWriter jsonCodeWriter,
        boolean usePrettyPrinting ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        if ( usePrettyPrinting ) {
            objectMapper.writerWithDefaultPrettyPrinter();
        }

        objectMapper.writeValue ( jsonCodeWriter.getWriter(), jsonObjectNode );
    }
}
