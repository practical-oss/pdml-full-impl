package dev.ps.pdml.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.pdml.writer.node.PdmlNodeWriterUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class JsonToPdmlUtil {

    public static @NotNull TaggedNode jsonToPdmlTree (
        @NotNull JsonNode jsonNode,
        JsonTreeToPdmlTreeConverter.@NotNull JsonToPdmlConfig config ) {

        return new JsonTreeToPdmlTreeConverter ( config ).convert ( jsonNode );
    }

    public static @NotNull TaggedNode jsonReaderResourceToPdmlTree (
        @NotNull ReaderResource jsonReaderResource,
        JsonTreeToPdmlTreeConverter.@NotNull JsonToPdmlConfig config ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull JsonNode jsonNode = parseJson ( jsonReaderResource );
        return jsonToPdmlTree ( jsonNode, config );
    }

    public static void jsonToPdmlResource (
        @NotNull ReaderResource jsonReaderResource,
        @NotNull WriterResource pdmlWriterResource,
        JsonTreeToPdmlTreeConverter.@NotNull JsonToPdmlConfig treeConverterConfig,
        @NotNull PdmlNodeWriterConfig pdmlCodeWriterConfig,
        boolean usePrettyPrinting ) throws IOException, PdmlException,JsonProcessingException {

        @NotNull TaggedNode pdmlRootNode = jsonReaderResourceToPdmlTree (
            jsonReaderResource, treeConverterConfig );
        try ( Writer writer = pdmlWriterResource.newWriter() ) {
            PdmlNodeWriterUtil.write (
                writer, pdmlRootNode, usePrettyPrinting, pdmlCodeWriterConfig );
            writer.flush();
        }
    }

    private static @NotNull JsonNode parseJson (
        @NotNull ReaderResource jsonReaderResource ) throws IOException {

        ObjectMapper jsonMapper = new ObjectMapper();
        try ( Reader reader = jsonReaderResource.newReader() ) {
            return jsonMapper.readTree ( reader );
        }
    }
}
