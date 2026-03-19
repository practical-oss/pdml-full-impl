package dev.ps.pdml.parser;

import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.shared.basics.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class PdmlParserTest {

    @Test
    void requireRootNode() throws IOException, PdmlException {

        PdmlParser parser = createParser ( "\r\n\t [root]\n" );
        TaggedNode rootNode = parser.requireDocument ();
        assertEquals ( "root", rootNode.getTag ().qualifiedTag () );
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
    }

    @Test
    void parseTag () throws IOException, PdmlException {

        PdmlParser parser = createParser ( "tag]" );
        NodeTag tag = parser.parseTag();
        assertNotNull ( tag );
        assertEquals ( "tag", tag.tag() );
        assertNull ( tag.namespacePrefix() );
        assertEquals ( "tag", tag.qualifiedTag () );

        parser = createParser ( "ns|local]" );
        tag = parser.parseTag ();
        assertNotNull ( tag );
        assertEquals ( "ns", tag.namespacePrefix() );
        assertEquals ( "local", tag.tag () );
        assertEquals ( "ns|local", tag.qualifiedTag () );

        parser = createParser ( "\"foo bar\"]" );
        tag = parser.parseTag();
        assertNotNull ( tag );
        assertEquals ( "foo bar", tag.tag() );
        assertNull ( tag.namespacePrefix() );
        assertEquals ( "foo bar", tag.qualifiedTag () );
    }


    @Test
    void parseStringLiteral() throws IOException, PdmlException {

        // Simple cases
        checkStringLiteral ( "bare_string", "bare_string" );
        checkStringLiteral ( "\"quoted string\"", "quoted string" );
        checkStringLiteral ( "~|raw string|~", "raw string" );
        checkStringLiteral ( """
            \"\"\"
            multi
            line
            string
            \"\"\"
            """, "multi\nline\nstring" );

        // Empty/null string
        checkStringLiteral ( " ", null );
        checkStringLiteral ( "\"\"", null );
        checkStringLiteral ( "~||~", null );
        checkStringLiteral ( """
            \"\"\"
            \"\"\"
            """, null );

        // Escape Sequences
        String escapes = "start\\[\\]\\s\\t\\n\\r\\f\\^\\(\\)\\=\\\"\\~\\|\\:\\,\\`\\!\\$\\\\end";
        String expected = "start[] \t\n\r\f^()=\"~|:,`!$\\end";
        checkStringLiteral ( escapes, expected );
        checkStringLiteral ( "\"" + escapes + "\"", expected );
        checkStringLiteral ( "~|" + escapes + "|~", escapes );
        checkStringLiteral ( "\"\"\"\n" + escapes + "\n\"\"\"",escapes );
        // TODO checkStringLiteral ( "\"\"\"e\n" + escapes + "\n\"\"\"",expected );

        // Unicode Escape Sequences
        escapes = "start\\u{41}\\u{42 43}end";
        expected = "startABCend";
        checkStringLiteral ( escapes, expected );
        checkStringLiteral ( "\"" + escapes + "\"", expected );
        checkStringLiteral ( "~|" + escapes + "|~", escapes );
        checkStringLiteral ( "\"\"\"\n" + escapes + "\n\"\"\"",escapes );
        checkStringLiteral ( "\"\"\"e\n" + escapes + "\n\"\"\"",expected );

        // Extensions
        String getSet = "start^[const c1=CCC]^[ins_const c1]end";
        expected = "startCCCend";
        checkStringLiteral ( getSet, expected );
        checkStringLiteral ( "\"" + getSet + "\"", expected );
        checkStringLiteral ( "~|" + getSet + "|~", getSet );
        checkStringLiteral ( "\"\"\"\n" + getSet + "\n\"\"\"",getSet );
        // checkStringLiteral ( "\"\"\"e\n" + getSet + "\n\"\"\"",expected );
    }

    private void checkStringLiteral ( String pdmlCode, String expected ) throws IOException, PdmlException {

        PdmlParser parser = createParser ( pdmlCode );
        @Nullable String string = parser.parseTextLeafAsStringLiteral ();
        if ( expected != null ) {
            assertEquals ( expected, string );
        } else {
            assertNull ( string );
        }
    }


    private @NotNull PdmlParser createParser ( @NotNull String code ) throws IOException {
        StringReader stringReader = new StringReader ( code );
        return PdmlParser.create ( stringReader, new StringReaderResource ( code ), PdmlParserConfig.defaultConfig() );
    }
}
