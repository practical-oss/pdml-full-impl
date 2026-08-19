package dev.ps.pdml.cmdnode.scripting;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsertExpressionCommandNodeTest {

    @Test
    void testHandleNode() throws Exception {

        // This works if ExtensionNodeHandlerContext.parseExtensionNodeArguments_OLD is used
        // TaggedNode rootNode = ParseASTUtil.parseString ( "[root ^[ins-exp 1 + 1]]" );
        // assertEquals ( "2", rootNode.toText() );

        TaggedNode rootNode = ParseASTUtil.parseString ( "[root ^[ins-exp [code 1 + 1]]]" );
        assertEquals ( "2", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins-exp "2 + 2"]]""" );
        assertEquals ( "4", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( "[root ^[ins-exp ~3+3~]]" );
        assertEquals ( "6", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins-exp ""\"
                4 + 4
                ""\"
                ]]
            """ );
        assertEquals ( "8", rootNode.toText() );

        // rootNode = ParseASTUtil.parseString ( "[root a ^[ins-exp 5 + 5] b]" );
        rootNode = ParseASTUtil.parseString ( "[root a ^[ins-exp \"5 + 5\"] b]" );
        assertEquals ( "a 10 b", rootNode.concatenateTreeTexts() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins-exp [code "\\[\\]"] [escape-text yes]]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.concatenateTreeTexts() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins-exp ~|"[]"|~ [escape-text yes]]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.concatenateTreeTexts() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins-exp \\"\\[\\]\\" [escape-text yes]]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.concatenateTreeTexts() );

        // rootNode = ParseASTUtil.parseString ( """
        //    [root ^[ins-exp [escape-text yes] 1+1]
        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins-exp [escape-text yes] [code 1+1]]
            ]
            """ );
        assertEquals ( "2\n", rootNode.concatenateTreeTexts() );

        // rootNode = ParseASTUtil.parseString ( """
        //    [root ^[ins-exp [escape-text yes] ~|"[]"|~]
        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins-exp ~|"[]"|~ [escape-text yes]]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.concatenateTreeTexts() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^[ins-exp ~"[]"~ yes]
            ]
            """ );
        assertEquals ( "[]\n", rootNode.concatenateTreeTexts() );

        assertThrows ( PdmlException.class, () -> ParseASTUtil.parseString ( "[root ^[ins-exp]]" ) );
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
