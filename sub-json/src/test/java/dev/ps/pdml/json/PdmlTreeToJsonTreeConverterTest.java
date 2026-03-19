package dev.ps.pdml.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;

class PdmlTreeToJsonTreeConverterTest {

    @Test
    void testConvert() throws IOException, PdmlException {

        TaggedNode pdmlRootNode = ParseASTUtil.parseString ( "[root]" );
        PdmlTreeToJsonTreeConverter converter = new PdmlTreeToJsonTreeConverter();
        ObjectNode objectNode = converter.convert ( pdmlRootNode );
        String jsonString = new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString ( objectNode );
        assertEquals ( """
            {
              "type" : "tagged",
              "tag" : "root",
              "path" : "/root[1]"
            }""".replace ( "\n", "\r\n" ), jsonString );
    }
}
