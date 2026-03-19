package dev.ps.pdml.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.pdml.writer.node.PdmlNodeWriterUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;

import java.io.IOException;

public class JsonToPdmlUtil {


    // Basic Methods

    public static @NotNull TaggedNode treeToTree (
        @NotNull JsonNode jsonNode,
        JsonTreeToPdmlTreeConverter.@NotNull JsonToPdmlConfig config ) {

        return new JsonTreeToPdmlTreeConverter ( config ).convert ( jsonNode );
    }

    public static @NotNull TaggedNode readerToTree (
        @NotNull TextResourceReader jsonCodeReader,
        JsonTreeToPdmlTreeConverter.@NotNull JsonToPdmlConfig config ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull JsonNode jsonNode = parseJson ( jsonCodeReader );
        return treeToTree ( jsonNode, config );
    }

    public static void treeToWriter (
        @NotNull JsonNode jsonNode,
        JsonTreeToPdmlTreeConverter.@NotNull JsonToPdmlConfig treeConverterConfig,
        @NotNull TextResourceWriter pdmlCodeWriter,
        @NotNull PdmlNodeWriterConfig pdmlCodeWriterConfig,
        boolean usePrettyPrinting ) throws IOException {

        TaggedNode pdmlRootNode = treeToTree ( jsonNode, treeConverterConfig );
        PdmlNodeWriterUtil.write (
            pdmlCodeWriter.getWriter(), pdmlRootNode, usePrettyPrinting, pdmlCodeWriterConfig );
    }

    public static void readerToWriter (
        @NotNull TextResourceReader jsonCodeReader,
        @NotNull TextResourceWriter pdmlCodeWriter,
        JsonTreeToPdmlTreeConverter.@NotNull JsonToPdmlConfig treeConverterConfig,
        @NotNull PdmlNodeWriterConfig pdmlCodeWriterConfig,
        boolean usePrettyPrinting ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull TaggedNode pdmlRootNode = readerToTree (
            jsonCodeReader, treeConverterConfig );
        PdmlNodeWriterUtil.write (
            pdmlCodeWriter.getWriter(), pdmlRootNode, usePrettyPrinting, pdmlCodeWriterConfig );
    }

    private static @NotNull JsonNode parseJson (
        @NotNull TextResourceReader jsonCodeReader ) throws IOException {

        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readTree ( jsonCodeReader.getReader() );
    }


    // Convenience Methods
}
