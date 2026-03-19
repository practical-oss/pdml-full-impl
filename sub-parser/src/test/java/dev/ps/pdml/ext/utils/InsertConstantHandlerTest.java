package dev.ps.pdml.ext.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InsertConstantHandlerTest {

    @Test
    void handleNode() throws IOException, PdmlException {

        String code = "[root ^[const c1=foo]^[ins_const c1]]";
        @NotNull TaggedNode rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "foo", rootNode.toText() );

        code = "[root ^[const c1=foo]^[const c2=^[ins_const c1]bar]^[ins_const c2]]";
        rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "foobar", rootNode.toText() );

        code = """
            [root
                ^[const c1=foo]
                ^[const c2="^[ins_const c1] bar"]
                ^[const c3=v3 c4=" v 4"]
                [child start ^[ins_const c2] ^[ins_const c3]^[ins_const c4] end]
            ]
            """;
        rootNode = ParseASTUtil.parseString ( code );
        TaggedNode childNode = rootNode.child ( "child" );
        assertEquals ( "start foo bar v3 v 4 end", childNode.toText() );
    }
}
