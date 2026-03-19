package dev.ps.pdml.data.node;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    public void isXXX() {

        TaggedNode rootNode = new TaggedNode ( "root" );
        assertTrue ( rootNode.isRootNode() );
        assertTrue ( rootNode.isTaggedNode () );
        assertTrue ( rootNode.isLeafNode() );
        assertFalse ( rootNode.isTextLeaf () );
        assertFalse ( rootNode.isCommentLeaf () );

        // BranchNode branchNode = new BranchNode ( "child", rootNode );
        TaggedNode taggedNode = new TaggedNode ( "child" );
        rootNode.appendChild ( taggedNode );
        assertFalse ( taggedNode.isRootNode() );
        assertTrue ( taggedNode.isTaggedNode () );
        assertTrue ( taggedNode.isLeafNode() );
        assertFalse ( taggedNode.isTextLeaf () );
        assertFalse ( taggedNode.isCommentLeaf () );

        // TextNode textNode = new TextNode ( "text", branchNode );
        TextLeaf textNode = new TextLeaf ( "text", null );
        taggedNode.appendChild ( textNode );
        assertFalse ( textNode.isRootNode() );
        assertFalse ( textNode.isTaggedNode () );
        assertTrue ( textNode.isLeafNode() );
        assertTrue ( textNode.isTextLeaf () );
        assertFalse ( textNode.isCommentLeaf () );

        // CommentNode commentNode = new CommentNode ( "comment", branchNode );
        CommentLeaf commentNode = new CommentLeaf ( "comment", null );
        taggedNode.appendChild ( commentNode );
        assertFalse ( commentNode.isRootNode() );
        assertFalse ( commentNode.isTaggedNode () );
        assertTrue ( commentNode.isLeafNode() );
        assertFalse ( commentNode.isTextLeaf () );
        assertTrue ( commentNode.isCommentLeaf () );
    }

    @Test
    public void testSiblings() {

        TaggedNode rootNode = new TaggedNode ( "root" );
        TaggedNode child1 = new TaggedNode ( "child1" );
        TaggedNode child2 = new TaggedNode ( "child2" );
        TaggedNode child3 = new TaggedNode ( "child3" );
        TaggedNode child4 = new TaggedNode ( "child4" );
        TaggedNode child5 = new TaggedNode ( "child5" );
        assertNull ( child3.firstSibling() );
        assertNull ( child3.previousSibling() );
        assertNull ( child3.nextSibling() );
        assertNull ( child3.lastSibling() );
        assertNull ( child3.childIndex() );
        assertNull ( child3.childIndexForHumans() );
        rootNode
            .appendChild ( child1 )
            .appendChild ( child2 )
            .appendChild ( child3 )
            .appendChild ( child4 )
            .appendChild ( child5 );
        assertSame ( child1, child3.firstSibling() );
        assertSame ( child2, child3.previousSibling() );
        assertSame ( child4, child3.nextSibling() );
        assertSame ( child5, child3.lastSibling() );
        assertEquals ( 2, child3.childIndex() );
        assertEquals ( 3, child3.childIndexForHumans() );
    }
}
