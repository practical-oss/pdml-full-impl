package dev.ps.pdml.cmdnode.utils;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DefineConstantsCommandNodeTest {

    @Test
    void handleNode() throws IOException, PdmlException {

        // String code = "[root ^[const c1=foo]]";
        String code = "[root ^[const [c1 foo]]]";
        @NotNull TaggedNode rootNode = ParseASTUtil.parseString ( code );
        assertTrue ( rootNode.isLeafNode() );

        code = "[root ^[const [c1 foo]] ^[ins-const c1]]";
        rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( " foo", rootNode.concatenateTreeTexts() );

        code = "[root ^[const c1=foo] ^[ins-const c1]]";
        rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( " foo", rootNode.concatenateTreeTexts() );

        code = """
               [root ^[const [c1 foo]]^[const
                   [c2]
                   ^/* comment */
                   [c3 v 3]
               ] ^[ins-const c1] ^[ins-const c2] ^[ins-const c3]]""";
        rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( " foo  v 3", rootNode.concatenateTreeTexts() );

        code = "[root ^[const c1=foo] ^[const c2=v2 c3 = \"v 3\"]text]";
        rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( " text", rootNode.concatenateTreeTexts() );

        // Invalid, c1 defied twice

        String code2 = "[root ^[const [c1 foo]] ^[const [c1 bar]]]";
        assertThrows ( PdmlException.class, () -> ParseASTUtil.parseString ( code2 ) );

        String code3 = "[root ^[const c1=foo] ^[const c1=bar]]";
        assertThrows ( PdmlException.class, () -> ParseASTUtil.parseString ( code3 ) );
    }
}
