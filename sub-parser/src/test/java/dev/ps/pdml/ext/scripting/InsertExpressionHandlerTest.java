package dev.ps.pdml.ext.scripting;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsertExpressionHandlerTest {

    @Test
    void testHandleNode() throws Exception {

        TaggedNode rootNode = ParseASTUtil.parseString ( "[root ^[ins_exp 1 + 1]]" );
        assertEquals ( "2", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( "[root ^[ins_exp [code 1 + 1]]]" );
        assertEquals ( "2", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins_exp "2 + 2"]]""" );
        assertEquals ( "4", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( "[root ^[ins_exp ~|3+3|~]]" );
        assertEquals ( "6", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins_exp ""\"
                4 + 4
                ""\"
                ]]
            """ );
        assertEquals ( "8", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( "[root a ^[ins_exp 5 + 5] b]" );
        assertEquals ( "a 10 b", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins_exp [code "\\[\\]"] [escape_text yes]]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins_exp ~|"[]"|~ [escape_text yes]]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins_exp \\"\\[\\]\\" [escape_text yes]]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins_exp [escape_text yes] 1+1]
            ]
            """ );
        assertEquals ( "2\n", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins_exp [escape_text yes] ~|"[]"|~]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.toText() );

        assertThrows ( PdmlException.class, () -> ParseASTUtil.parseString ( "[root ^[ins_exp]]" ) );
    }

/* Old version with JavaScript
    @Test
    void testHandleNode() throws Exception {

        String code = "[s:exp 1 + 1]end";
        // ExtensionNodeHandlerContext context = contextForTests ( code );
        ExtensionNodeHandlerContext context = ExtensionNodeHandlerContext.createForTests ( code );
        NodeName nodeName = context.getPdmlParser().requireBranchNodeStartAndNameAndSeparator().getName();
        assertNotNull ( nodeName );
        assertEquals ( "s:exp", nodeName.qualifiedName() );
        ExpressionHandler handler = ExpressionHandler.INSTANCE;

        handler.handleNode ( context, nodeName );
        PdmlReader reader = context.getPdmlReader();
        assertEquals ( '2', reader.currentChar() );
        reader.advanceChar();
        assert ( reader.isAtString ( "end" ) );
    }
 */
}
