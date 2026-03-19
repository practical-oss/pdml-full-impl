package dev.ps.pdml.data.node.tagged;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaggedNodeTest {

    @Test
    void removeWhitespaceTextLeafsInTree() {

        TaggedNode taggedNode = new TaggedNode ( "test" )
            .appendText ( "qwe" )
            .appendChild ( new TaggedNode ( "child" ) );
        assertFalse ( taggedNode.removeWhitespaceTextLeafsInTree ( true ) );
        assertEquals ( 2, taggedNode.getChildNodes().size() );
        taggedNode
            .appendText ( "   " )
            .appendText ( " \r\n\t" );
        assertEquals ( 4, taggedNode.getChildNodes().size() );
        assertTrue ( taggedNode.removeWhitespaceTextLeafsInTree ( true ) );
        assertEquals ( 2, taggedNode.getChildNodes().size() );
    }
}
