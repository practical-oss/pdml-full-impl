package dev.ps.pdml.core.parser;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorePdmlParserUtilTest {

    @Test
    public void test() throws Exception {

        TaggedNode rootNode = CorePdmlParserUtil.parseString ( "[root]" );
        assertEquals ( "root", rootNode.getTag ().qualifiedTag () );
        assertTrue ( rootNode.isEmpty () );

        rootNode = CorePdmlParserUtil.parseString ( "[root [child foo bar]]" );
        assertEquals ( "root", rootNode.getTag ().qualifiedTag () );
        assertFalse ( rootNode.isEmpty () );

        assertEquals ( 1, rootNode.getChildNodes().size() );
        TaggedNode childNode = (TaggedNode) rootNode.getChildNodes().get ( 0 );
        assertEquals ( "child", childNode.getTag ().qualifiedTag () );
        assertFalse ( childNode.isEmpty() );

        TextLeaf textLeaf = (TextLeaf) childNode.getChildNodes().get ( 0 );
        assertEquals ( "foo bar", textLeaf.getText() );
    }
}
