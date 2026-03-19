package dev.ps.pdml.data.util;

import dev.ps.pdml.data.node.Node;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.shared.basics.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeIteratorTest {

    @Test
    public void test() throws Exception {

        // [root]
        TaggedNode rootNode = new TaggedNode ( "root" );
        runTest ( rootNode, "root", "", true );
        runTest ( rootNode, "", "", false );

        // [root text text]
        rootNode = new TaggedNode ( "root" )
            .appendText ( "text text" );
        runTest ( rootNode, "root", "text text", true );

        // [root text 1[child text 2]text 3]
        rootNode = new TaggedNode ( "root" )
            .appendText ( "text 1" )
            .appendTextChild ( "child", "text 2" )
            .appendText ( "text 3" );
        runTest ( rootNode, "root, child", "text 1, text 2, text 3", true );
        runTest ( rootNode, "child", "text 1, text 2, text 3", false );
    }

    private void runTest (
        @NotNull TaggedNode rootNode,
        @NotNull String expectedNames,
        @NotNull String expectedTexts,
        boolean includeRootNode ) {

        StringBuilder names = new StringBuilder();
        StringBuilder texts = new StringBuilder();
        // for ( Node node : rootNode ) {
        TreeIterator iterator = new TreeIterator ( rootNode, includeRootNode );
        while ( iterator.hasNext() ) {
            Node node = iterator.next();

            if ( node instanceof TaggedNode taggedNode ) {
                if ( ! names.isEmpty() ) names.append ( ", " );
                names.append ( taggedNode.getTag () );
            }
            else if ( node instanceof TextLeaf textNode ) {
                if ( ! texts.isEmpty() ) texts.append ( ", " );
                texts.append ( textNode.getText() );
            }
        }
        assertEquals ( expectedNames, names.toString() );
        assertEquals ( expectedTexts, texts.toString() );
    }

}
