package dev.ps.pdml.core.reader;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.reader.util.ParsedString;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.exception.PdmlException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CorePdmlTokenReaderTest {

    @Test
    void generalTest() throws IOException, PdmlException {

        CorePdmlTokenReader reader = createReader ( "[root [child\r\nfoo bar]]" );
        assertTrue ( reader.skipNodeStart() );
        assertEquals ( "root", reader.readTag() );
        assertEquals ( " ", reader.readSeparator() );
        assertTrue ( reader.skipNodeStart() );
        assertEquals ( "child", reader.readTag() );
        assertEquals ( "\r\n", reader.readSeparator() );
        assertEquals ( "foo bar", reader.readTextLeaf() );
        assertTrue ( reader.skipNodeEnd() );
        assertTrue ( reader.skipNodeEnd() );
        assertTrue ( reader.isAtEnd() );
    }


    // Node Start/End

    @Test
    void nodeStart() throws IOException, MalformedPdmlException {

        CorePdmlTokenReader reader = createReader ( "[[a]" );
        assertTrue ( reader.isAtNodeStart() );
        assertEquals ( "[", reader.readNodeStart() );
        assertTrue ( reader.skipNodeStart() );

        assertNull ( reader.readNodeStart() );
        assertFalse ( reader.isAtNodeStart() );
        assertFalse ( reader.skipNodeStart() );

        reader.readTextLeaf();
        assertNull ( reader.readNodeStart() );
        assertFalse ( reader.isAtNodeStart() );
        assertFalse ( reader.skipNodeStart() );
    }

    @Test
    void nodeEnd() throws IOException, MalformedPdmlException {

        CorePdmlTokenReader reader = createReader ( "]]a[" );
        assertTrue ( reader.isAtNodeEnd() );
        assertEquals ( "]", reader.readNodeEnd() );
        assertTrue ( reader.skipNodeEnd() );

        assertNull ( reader.readNodeEnd() );
        assertFalse ( reader.isAtNodeEnd() );
        assertFalse ( reader.skipNodeEnd() );

        reader.readTextLeaf();
        assertNull ( reader.readNodeEnd() );
        assertFalse ( reader.isAtNodeEnd() );
        assertFalse ( reader.skipNodeEnd() );
    }


    // Tag

    @ParameterizedTest
    @CsvSource ( {
        "tag, tag",
        "tag_2], tag_2",
        "_], _",
        "2-.?], 2-.?",
        "คุณภาพ], คุณภาพ",

        // CP > U+FFFF
        "😀2😀4😀😀7, 😀2😀4😀😀7",

        // Escape sequences
        "tag_3\\]], tag_3]",
        "tag\\s4], 'tag 4'",
        "\\[\\]\\s\\t\\n\\r\\f\\\\], '[] \t\n\r\f\\'",
        "'\\^\\(\\)\\=\\~\\|\\:\\,\\`\\!\\$]', '^()=~|:,`!$'",
        "\\\", \"",

        // Empty
        "]," } )

    void readTag (
        String pdmlCode,
        String expectedTag ) throws IOException, MalformedPdmlException {

        CorePdmlTokenReader reader = createReader ( pdmlCode );
        assertEquals ( expectedTag, reader.readTag() );
    }

    @ParameterizedTest
    @CsvSource ( {
        "tag|", // invalid char |
        "tag\\m]", // invalid escape char \m

        // Invalid Unicode control code points
        "tag\u0000]",
        "tag\u001F]",
        "tag\u0080]",
        "tag\u009F]" } )

    void readInvalidTag ( String pdmlCode ) throws IOException {

        CorePdmlTokenReader reader = createReader ( pdmlCode );
        // assertThrows ( MalformedPdmlException.class, reader::readTagWithTextRange );
        // assertThrows ( MalformedPdmlException.class, () -> reader.readWithTextRange ( CorePdmlTokenReader::readTag ) );
        assertThrows ( MalformedPdmlException.class, reader::readTag );
    }


    // TextLeaf

    @ParameterizedTest
    @CsvSource ( {
        "text, text",
        "text1[, text1",
        "text 2], text 2",
        "'123 _.- คุณภาพ]', '123 _.- คุณภาพ'",

        // CP > U+FFFF
        "😀2😀4😀😀7, 😀2😀4😀😀7",

        // Escape sequences
        "text_3\\]], text_3]",
        "text\\s4], 'text 4'",
        "\\[\\] \\s\\t\\n\\r\\f\\\\], '[]  \t\n\r\f\\'",
        "'\\^\\(\\)\\=\\~\\|\\:\\,\\`\\!\\$]', '^()=~|:,`!$'",
        "\\\", \"",

        // Empty
        "]," } )

    void readTextLeaf (
        String pdmlCode,
        String expectedTag ) throws IOException, MalformedPdmlException {

        CorePdmlTokenReader reader = createReader ( pdmlCode );
        assertEquals ( expectedTag, reader.readTextLeaf() );
    }

    @ParameterizedTest
    @CsvSource ( {
        "text\\m]", // invalid escape char \m

        // Invalid Unicode control code points
        "text\u0000]",
        "text\u001F]",
        "text\u0080]",
        "text\u009F]" } )

    void readInvalidTextLeaf ( String pdmlCode ) throws IOException {

        CorePdmlTokenReader reader = createReader ( pdmlCode );
        assertThrows ( MalformedPdmlException.class, reader::readTextLeaf );
    }

    @ParameterizedTest
    @CsvSource ( {
        "start\\u{41}\\u{42 43}end, startABCend",
        } )

    void readTextFragment (
        String pdmlCode,
        String expectedText ) throws IOException, MalformedPdmlException {

        CorePdmlTokenReader reader = createReader ( pdmlCode );
        assertEquals ( expectedText, reader.readTextFragment (
            CorePdmlConstants.TEXT_LEAF_END_CHARS, CorePdmlConstants.INVALID_TEXT_LEAF_CHARS,
            CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CODE_POINTS, true ) );
    }


    // Separator

    @Test
    void readSeparator() throws IOException, PdmlException {

        expectSeparator ( " ", " " );
        expectSeparator ( "\t", "\t" );
        expectSeparator ( "\n", "\n" );
        expectSeparator ( "\r\n", "\r\n" );

        expectSeparator ( "  ", " " );
        expectSeparator ( "\r\n\n", "\r\n" );

        expectSeparator ( "a", null );
        expectSeparator ( "\\s", null );
    }

    private void expectSeparator ( String code, String expectedResult ) throws IOException {

        CorePdmlTokenReader reader = createReader ( code );
        assertEquals ( expectedResult, reader.readSeparator () );
    }


    @Test
    void readWithTextRange() throws IOException, MalformedPdmlException {

        CorePdmlTokenReader reader = createReader ( "12\\t\n\\[\\]" );
        // ParsedString<?> parsedString = reader.readTextLeafWithTextRange ();
        ParsedString<?> parsedString = reader.readWithTextRange ( CorePdmlTokenReader::readTextLeaf );
        assertNotNull ( parsedString );
        assertEquals ( "12\t\n[]", parsedString.string() );
        assertEquals ( "1:1..2:4 12\\t\n\\[\\]", parsedString.source().toDebugString() );
    }

    @Test
    void readWithTextPosition() throws IOException, MalformedPdmlException {

        CorePdmlTokenReader reader = createReader ( "12\\t\n\\[\\]" );
        // ParsedString<?> parsedString = reader.readTextLeafWithTextRange ();
        ParsedString<?> parsedString = reader.readWithTextPosition ( CorePdmlTokenReader::readTextLeaf );
        assertNotNull ( parsedString );
        assertEquals ( "12\t\n[]", parsedString.string() );
        assertEquals ( "1:1.._:_ null", parsedString.source().toDebugString() );
    }


    private @NotNull CorePdmlTokenReader createReader ( @NotNull String code ) throws IOException {
        return new CorePdmlTokenReader ( new StringReaderResource ( code ) );
    }
}
