package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InsertConstantCommandNodeTest {

    @Test
    void handleNode() throws IOException, PdmlException {

        String code = "[root ^[const c1=foo]^[ins-const c1]]";
        @NotNull TaggedNode rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "foo", rootNode.toText() );

        code = "[root ^[const c1=foo]^[const c2=^[ins-const c1]bar]^[ins-const c2]]";
        rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "foobar", rootNode.concatenateTreeTexts() );

        code = """
            [root
                ^[const c1=foo]
                ^[const c2="^[ins-const c1] bar"]
                ^[const c3=v3 c4=" v 4"]
                [child start ^[ins-const c2] ^[ins-const c3]^[ins-const c4] end]
            ]
            """;
        rootNode = ParseASTUtil.parseString ( code );
        TaggedNode childNode = rootNode.child ( "child" );
        assertEquals ( "start foo bar v3 v 4 end", childNode.concatenateTreeTexts() );
    }
}
