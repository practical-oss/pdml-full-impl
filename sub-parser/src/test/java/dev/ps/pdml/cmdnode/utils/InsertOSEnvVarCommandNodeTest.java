package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.os.OSName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InsertOSEnvVarCommandNodeTest {

    @Test
    void handleNode() throws IOException, PdmlException {

        boolean isWindowsOS = OSName.isWindowsOS();
        String varName = isWindowsOS ? "TEMP" : "TMPDIR";
        String pdmlCode = "[root ^[ins-env " + varName + " [escape-text yes]]]";
        @NotNull TaggedNode rootNode = ParseASTUtil.parseString ( pdmlCode );
        // TODO not portable
        assertTrue ( rootNode.toText().endsWith ( isWindowsOS ? "Temp" : "tmp" ) );

        // Use default value
        pdmlCode = "[root ^[ins-env INVALID_VAR_NAME [default foo]]]";
        rootNode = ParseASTUtil.parseString ( pdmlCode );
        assertEquals ( "foo", rootNode.toText() );

        /* TODO
        // pdmlCode = "[root ^[ins-env [var INVALID_VAR_NAME] [default ^\"\\[\\]\"]]]";
        // pdmlCode = "[root ^[ins-env [var INVALID_VAR_NAME] [default \\[\\]]]]";
        pdmlCode = "[root ^[ins-env [var INVALID_VAR_NAME] [default ^~[]~]]]";
        rootNode = ParseASTUtil.parseString ( pdmlCode );
        assertEquals ( "[]", rootNode.toText() );
         */

        String pdmlCode2 = "[root ^[ins-env INVALID_VAR_NAME]]";
        assertThrows ( PdmlException.class, () -> ParseASTUtil.parseString ( pdmlCode2 ) );
    }
}
