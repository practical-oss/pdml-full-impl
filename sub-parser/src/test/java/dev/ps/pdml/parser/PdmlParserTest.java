package dev.ps.pdml.parser;

import dev.ps.pdml.data.util.DemoDocs;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.shared.basics.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdmlParserTest {

    @Test
    void parseCorePdmlDocument() throws IOException {

        String pdmlDoc = DemoDocs.corePdmlDemoDoc();
        PdmlParser parser = createParser ( pdmlDoc );
        assertDoesNotThrow ( parser::requireDocument );
    }

    @Test
    void parseDocumentWithExtensions() throws IOException {

        String pdmlDoc = DemoDocs.pdmlExtensionsDemoDoc();
        PdmlParser parser = createParser ( pdmlDoc );
        assertDoesNotThrow ( parser::requireDocument );
    }

    @ParameterizedTest
    @CsvSource ( {
        "root]",
        "[root",
        "[]",
        "[[root]",
        "[root]]",
        // "[a:b]",
        // "[a|b]",
        "[a\1b]" } )
    void parseInvalidPdmlDocument ( String pdmlDoc ) throws IOException {

        PdmlParser parser = createParser ( pdmlDoc );
        assertThrows ( PdmlException.class, parser::requireDocument );
    }

    @Test
    void requireDocument() throws IOException, PdmlException {

        PdmlParser parser = createParser ( "[root]" );
        TaggedNode rootNode = parser.requireDocument();
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
    void documentStart() throws IOException, PdmlException {

        // Whitespace before doc start is allowed
        PdmlParser parser = createParser ( "\r\n\n\t [root]\n" );
        TaggedNode rootNode = parser.requireDocument();
        assertEquals ( "root", rootNode.getTag ().qualifiedTag () );
        assertTrue ( rootNode.isEmpty () );

        // Comments before doc start is allowed
        parser = createParser ( """
            ^// comment

            ^/*
                comment
            */
            [root]""" );
        rootNode = parser.requireDocument();
        assertEquals ( "root", rootNode.getTag().qualifiedTag() );
        assertTrue ( rootNode.isEmpty () );

        // Extensions before root node are allowed
        parser = createParser ( "^[const [c \\[root\\]]]^[ins-const c]" );
        rootNode = parser.requireDocument();
        assertEquals ( "root", rootNode.getTag().qualifiedTag() );
        assertTrue ( rootNode.isEmpty () );

        // Invalid
        parser = createParser ( "^[const [c \\[root\\]]]" );
        assertThrows ( PdmlException.class, parser::requireDocument );
    }


    @Test
    void documentEnd() throws IOException, PdmlException {

        // Whitespace after doc end is allowed
        PdmlParser parser = createParser ( "[root]\r\n\n\t " );
        TaggedNode rootNode = parser.requireDocument();
        assertEquals ( "root", rootNode.getTag ().qualifiedTag () );
        assertTrue ( rootNode.isEmpty () );

        // Comments after doc end are allowed
        parser = createParser ( """
            [root]
            ^// comment

            ^/*
                comment
            */
            """ );
        rootNode = parser.requireDocument();
        assertEquals ( "root", rootNode.getTag().qualifiedTag() );
        assertTrue ( rootNode.isEmpty () );
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
        String getSet = "start^[const c1=CCC]^[ins-const c1]end";
        expected = "startCCCend";
        checkStringLiteral ( getSet, expected );
        checkStringLiteral ( "\"" + getSet + "\"", expected );
        checkStringLiteral ( "~|" + getSet + "|~", getSet );
        checkStringLiteral ( "\"\"\"\n" + getSet + "\n\"\"\"",getSet );
        // checkStringLiteral ( "\"\"\"e\n" + getSet + "\n\"\"\"",expected );
    }

    private void checkStringLiteral ( String pdmlCode, String expected ) throws IOException, PdmlException {

        PdmlParser parser = createParser ( pdmlCode );
        // @Nullable String string = parser.parseTextLeafAsStringLiteral ();
        @Nullable String string = parser.parseStringLiteralOrNullInTextLeaf();
        if ( expected != null ) {
            assertEquals ( expected, string );
        } else {
            assertNull ( string );
        }
    }


    private @NotNull PdmlParser createParser ( @NotNull String code ) throws IOException {
        return PdmlParser.create ( new StringReaderResource ( code ), PdmlParserConfig.defaultConfig() );
    }
}
