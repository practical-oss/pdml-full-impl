package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.directory.TempDirectoryUtil;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;
import dev.ps.pdml.data.exception.PdmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InsertURLCommandNodeTest {

    @Test
    void handleNode() throws IOException, PdmlException {

        Path tempDir = TempDirectoryUtil.createTempDirectory ( null, true );

        Path pdmlFile = tempDir.resolve ( "test.pdml" );
        // DebugUtils.writeNameValue ( "pdmlFile", pdmlFile );
        TextFileWriterUtil.writeStringToUTF8File (
            // "[root ^[ins-url \"https://pdml-lang.dev/index.html\" [escape-text yes]]]", pdmlFile, false );
            "[root ^[ins-url https://pdml-lang.dev/index.html [escape-text yes]]]", pdmlFile, false );

        System.err.println ( "InsertURLHandlerTest temporary disabled for efficiency");
        /*
        @NotNull TaggedNode rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertTrue ( rootNode.toText().contains ( "<html>" ) );
         */
    }
}
