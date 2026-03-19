package dev.ps.pdml.data.node.leaf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentLeafTest {

    @Test
    void removeDelimiters () {

        assertEquals ( "foo", CommentLeaf.removeDelimiters ( "^/*foo*/" ) );
        assertEquals ( " ", CommentLeaf.removeDelimiters ( "^/* */" ) );
        assertEquals ( " foo ", CommentLeaf.removeDelimiters ( "^/** foo **/" ) );
        assertEquals ( " ", CommentLeaf.removeDelimiters ( "^/*** ***/" ) );
        assertEquals ( "foo", CommentLeaf.removeDelimiters ( "foo" ) );
    }
}
