package dev.ps.pdml.ext.utils;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class InsertURLHandlerTest {

    @Test
    void handleNode() throws IOException, PdmlException {

        Path tempDir = Files.createTempDirectory ( null );
        tempDir.toFile().deleteOnExit();

        Path pdmlFile = tempDir.resolve ( "test.pdml" );
        // DebugUtils.writeNameValue ( "pdmlFile", pdmlFile );
        TextFileWriterUtil.writeStringToUTF8File (
            "[root ^[ins_url https://pdml-lang.dev/index.html [escape_text yes]]]", pdmlFile, false );

        @NotNull TaggedNode rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertTrue ( rootNode.toText().contains ( "<html>" ) );
    }
}
