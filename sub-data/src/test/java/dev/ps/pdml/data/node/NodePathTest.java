package dev.ps.pdml.data.node;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodePathTest {

    @Test
    public void test() {

        TaggedNode rootNode = new TaggedNode ( "root" );
        NodePath path = rootNode.path();
        assertEquals ( 1, path.nodesCount() );
        assertSame ( path.rootNode(), rootNode );
        List<NodeTag> pathNames = path.tags ();
        assertEquals ( 1, pathNames.size() );
        assertEquals ( "root", pathNames.get ( 0 ).toString() );
        assertEquals ( "/root[1]", path.toString() );
        assertEquals ( 0, path.level() );

        TaggedNode child1 = new TaggedNode ( "child1" );
        rootNode.appendChild ( child1 );
        path = child1.path();
        assertEquals ( 2, path.nodesCount() );
        assertSame ( path.get(0), rootNode );
        assertSame ( path.get(1), child1 );
        pathNames = path.tags ();
        assertEquals ( 2, pathNames.size() );
        assertEquals ( "root", pathNames.get ( 0 ).toString() );
        assertEquals ( "child1", pathNames.get ( 1 ).toString() );
        assertEquals ( "/root[1]/child1[1]", path.toString() );
        assertEquals ( 1, path.level() );

        TaggedNode child11 = new TaggedNode ( "child11" );
        child1.appendChild ( child11 );
        TaggedNode child12 = new TaggedNode ( "child12" );
        child1.appendChild ( child12 );
        path = child12.path();
        assertEquals ( 3, path.nodesCount() );
        assertSame ( path.get(0), rootNode );
        assertSame ( path.get(1), child1 );
        assertSame ( path.get(2), child12 );
        pathNames = path.tags ();
        assertEquals ( 3, pathNames.size() );
        assertEquals ( "root", pathNames.get ( 0 ).toString() );
        assertEquals ( "child1", pathNames.get ( 1 ).toString() );
        assertEquals ( "child12", pathNames.get ( 2 ).toString() );
        assertEquals ( "/root[1]/child1[1]/child12[2]", path.toString() );
        assertEquals ( 2, path.level() );

        TextLeaf textNode = new TextLeaf ( "text", null );
        child12.appendChild ( textNode );
        path = textNode.path();
        assertEquals ( "/root[1]/child1[1]/child12[2]/[1]", path.toString() );
        pathNames = path.tags ();
        assertEquals ( 3, pathNames.size() );
        assertEquals ( 3, path.level() );

        CommentLeaf commentNode = new CommentLeaf ( "comment", null );
        child12.appendChild ( commentNode );
        path = commentNode.path();
        assertEquals ( "/root[1]/child1[1]/child12[2]/[2]", path.toString() );
        pathNames = path.tags ();
        assertEquals ( 3, pathNames.size() );
        assertEquals ( 3, path.level() );
    }

}
