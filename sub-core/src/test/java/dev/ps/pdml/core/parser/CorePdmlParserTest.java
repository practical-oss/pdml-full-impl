package dev.ps.pdml.core.parser;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.util.NullableTextNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CorePdmlParserTest {


    @Test
    public void test() throws Exception {

        CorePdmlParser parser = createParser ( "\r\n\t [root]\n" );
        TaggedNode rootNode = parser.requireDocument ();
        assertEquals ( "root", rootNode.getTag().qualifiedTag() );
        assertTrue ( rootNode.isEmpty () );

        parser = createParser ( "[root [child foo bar]]" );
        rootNode = parser.requireDocument ();
        assertEquals ( "root", rootNode.getTag ().qualifiedTag () );
        assertFalse ( rootNode.isEmpty () );

        assertEquals ( 1, rootNode.getChildNodes().size() );
        TaggedNode childNode = (TaggedNode) rootNode.getChildNodes().get ( 0 );
        assertEquals ( "child", childNode.getTag ().qualifiedTag () );
        assertFalse ( childNode.isEmpty() );

        TextLeaf textLeaf = (TextLeaf) childNode.getChildNodes().get ( 0 );
        assertEquals ( "foo bar", textLeaf.getText() );

        // Invalid
        parser = createParser ( "[root" );
        assertThrows ( PdmlException.class, parser::parseDocument );
        parser = createParser ( "[root]]" );
        assertThrows ( PdmlException.class, parser::parseDocument );
        parser = createParser ( "[root^]" );
        assertThrows ( PdmlException.class, parser::parseDocument );
        parser = createParser ( "[ root]" );
        assertThrows ( PdmlException.class, parser::parseDocument );
        parser = createParser ( "[root ]" );
        assertThrows ( PdmlException.class, parser::parseDocument );
        parser = createParser ( "[root[child]]" );
        assertThrows ( PdmlException.class, parser::parseDocument );
    }

    @Test
    public void requireTextNode() throws Exception {

        CorePdmlParser parser = createParser ( "[name1 text1][tag\\[2\\] text2\\\\\\[\\] end\\n]" );

        NullableTextNode textNode = parser.requireTextNode();
        assertEquals ( "name1", textNode.tag().qualifiedTag() );
        assertEquals ( "text1", textNode.text () );

        textNode = parser.requireTextNode();
        assertEquals ( "tag[2]", textNode.tag().qualifiedTag() );
        assertEquals ( "text2\\[] end\n", textNode.text () );
    }

    @Test
    public void requireTaggedLeafNode() throws Exception {

        CorePdmlParser parser = createParser ( "[name1][tag\\[2\\]]" );

        TaggedNode emptyNode = parser.requireTaggedLeafNode ();
        assertEquals ( "name1", emptyNode.getTag ().qualifiedTag () );
        assertTrue ( emptyNode.isEmpty() );

        emptyNode = parser.requireTaggedLeafNode ();
        assertEquals ( "tag[2]", emptyNode.getTag ().qualifiedTag () );
        assertTrue ( emptyNode.isEmpty() );
    }

    private @NotNull CorePdmlParser createParser ( @NotNull String code ) throws IOException {
        return new CorePdmlParser (
            new StringReaderResource ( code ), new CorePdmlParserConfig() );
    }
}
