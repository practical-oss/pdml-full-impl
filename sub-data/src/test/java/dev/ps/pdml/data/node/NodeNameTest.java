package dev.ps.pdml.data.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeNameTest {

    @Test
    public void create() {

        NodeTag name = NodeTag.create ( "tag" );
        assertEquals ( "tag", name.tag () );
        assertNull ( name.namespacePrefix() );

        name = NodeTag.create ( "ns|tag" );
        assertEquals ( "tag", name.tag () );
        assertEquals ( "ns", name.namespacePrefix() );

        name = NodeTag.create ( "ns|foo:bar" );
        assertEquals ( "foo:bar", name.tag () );
        assertEquals ( "ns", name.namespacePrefix() );

        name = NodeTag.create ( "ns|foo|bar" );
        assertEquals ( "foo|bar", name.tag () );
        assertEquals ( "ns", name.namespacePrefix() );

        name = NodeTag.create ( ":tag" );
        assertEquals ( ":tag", name.tag () );
        assertNull ( name.namespacePrefix() );

        name = NodeTag.create ( "|tag" );
        assertEquals ( "|tag", name.tag () );
        assertNull ( name.namespacePrefix() );

        name = NodeTag.create ( "tag:" );
        assertEquals ( "tag:", name.tag () );
        assertNull ( name.namespacePrefix() );

        name = NodeTag.create ( "tag|" );
        assertEquals ( "tag|", name.tag () );
        assertNull ( name.namespacePrefix() );
    }
}
