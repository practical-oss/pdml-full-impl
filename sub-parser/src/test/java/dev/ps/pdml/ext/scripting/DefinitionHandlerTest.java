package dev.ps.pdml.ext.scripting;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefinitionHandlerTest {

    @Test
    void testHandleNode() throws Exception {

        String code = """
            [root ^[def
                ""\"
                package test;

                public class Util {
                    public static int foo() {
                        return 123;
                    }
                }
                ""\"
            ]^[ins_exp test.Util.foo()]]""";

        TaggedNode rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "123", rootNode.toText() );
    }

/* Old version using JavaScript

    @Test
    void testHandleNode() throws Exception {

        String code = "[s:def function foo() { return '123'; } ][s:exp foo()]";
        // ExtensionNodeHandlerContext context = Expression_ExtensionHandlerTest.contextForTests ( code );
        ExtensionNodeHandlerContext context = ExtensionNodeHandlerContext.createForTests ( code );
        NodeName nodeName = context.getPdmlParser().requireBranchNodeStartAndNameAndSeparator().getName();
        assertNotNull ( nodeName );
        assertEquals ( "s:def", nodeName.qualifiedName() );

        ExtensionNodeHandlerDelegate handler = DefinitionHandler.INSTANCE;
        handler.handleNode ( context, nodeName );

        nodeName = context.getPdmlParser ().requireBranchNodeStartAndNameAndSeparator().getName();
        assertNotNull ( nodeName );
        assertEquals ( "s:exp", nodeName.qualifiedName() );

        handler = ExpressionHandler.INSTANCE;
        handler.handleNode ( context, nodeName );

        PdmlReader reader = context.getPdmlReader();
        assertEquals ( "123", reader.readRemaining() );
    }
*/
}
