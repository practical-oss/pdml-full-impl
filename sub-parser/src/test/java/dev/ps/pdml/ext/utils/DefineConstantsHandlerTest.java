package dev.ps.pdml.ext.utils;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DefineConstantsHandlerTest {

    @Test
    void handleNode() throws IOException, PdmlException {

        String code = "[root ^[const c1=foo]]";
        @NotNull TaggedNode rootNode = ParseASTUtil.parseString ( code );
        assertTrue ( rootNode.isLeafNode() );

        code = "[root ^[const c1=foo] ^[const c2=v2 c3 = \"v 3\"]text]";
        rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( " text", rootNode.toText() );

        // Invalid, c1 defied twice
        String code2 = "[root ^[const c1=foo] ^[const c1=bar]]";
        assertThrows ( PdmlException.class, () -> ParseASTUtil.parseString ( code2 ) );
    }
}
