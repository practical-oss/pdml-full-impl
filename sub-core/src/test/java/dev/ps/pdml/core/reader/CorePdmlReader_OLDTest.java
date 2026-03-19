package dev.ps.pdml.core.reader;

import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.shared.basics.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class CorePdmlReader_OLDTest {

    @Test
    void generalTest() throws IOException, PdmlException {

        CorePdmlReader_OLD reader = createReader ( "[root [child\r\nfoo bar]]" );
        assertTrue ( reader.readNodeStart() );
        assertEquals ( "root", reader.readTagAsString () );
        assertEquals ( " ", reader.readSeparator() );
        assertTrue ( reader.readNodeStart() );
        assertEquals ( "child", reader.readTagAsString () );
        assertEquals ( "\r\n", reader.readSeparator() );
        assertEquals ( "foo bar", reader.readTextAsString () );
        assertTrue ( reader.readNodeEnd() );
        assertTrue ( reader.readNodeEnd() );
        assertTrue ( reader.isAtEnd() );
    }

    @Test
    void readTag() throws IOException, PdmlException {

        expectTag ( "tag1 ", "tag1" );
        expectTag ( "tag_2] ", "tag_2" );
        expectTag ( "2025-01-07] ", "2025-01-07" );
        expectTag ( "tag_.-] ", "tag_.-" );
        expectTag ( "_]", "_" );
        expectTag ( "คุณภาพ]", "คุณภาพ" );


        // Escape sequences
        expectTag ( "tag_3\\]] ", "tag_3]" );
        expectTag ( "tag\\s4] ", "tag 4" );
        expectTag (
            "\\[\\]\\s\\t\\n\\r\\f\\^\\(\\)\\=\\\"\\~\\|\\:\\,\\`\\!\\$\\\\]",
            "[] \t\n\r\f^()=\"~|:,`!$\\" );

        CorePdmlReader_OLD reader = createReader ( "]" );
        assertNull ( reader.readTagAsString () );

        // Invalid
        expectInvalidTag ( "tag|" ); // invalid char |
        expectInvalidTag ( "tag\\m]" ); // invalid escape char \m

        // Invalid Unicode control code points
        expectInvalidTag ( "tag\u0000]" );
        expectInvalidTag ( "tag\u001F]" );
        expectInvalidTag ( "tag\u0080]" );
        expectInvalidTag ( "tag\u009F]" );

        // Invalid Unicode surrogate code points
        // expectInvalidTag ( "tag\uD800]" );
        // expectInvalidTag ( "tag\uDFFF]" );
    }

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

    @Test
    void readTextAsString () throws IOException, PdmlException {

        expectText ( "text1[", "text1" );
        expectText ( "text2]", "text2" );
        expectText ( "123 _.- คุณภาพ]", "123 _.- คุณภาพ" );

        expectText (
            "\\[\\] \t\n\r\f\\^()=\"~|:,`!$\\\\]",
            "[] \t\n\r\f^()=\"~|:,`!$\\" );

        // Escape sequences
        expectText ( "text_3\\]] ", "text_3]" );
        expectText ( "text\\s4] ", "text 4" );
        expectText (
            "\\[\\]\\s\\t\\n\\r\\f\\^\\(\\)\\=\\\"\\~\\|\\:\\,\\`\\!\\$\\\\]",
            "[] \t\n\r\f^()=\"~|:,`!$\\" );

        CorePdmlReader_OLD reader = createReader ( "]" );
        assertNull ( reader.readTextAsString () );

        // Invalid
        expectInvalidText ( "text\\m]" ); // invalid escape char \m

        // Invalid Unicode control code points
        expectInvalidText ( "text\u0000]" );
        expectInvalidText ( "text\u001F]" );
        expectInvalidText ( "text\u0080]" );
        expectInvalidText ( "text\u009F]" );

        // Invalid Unicode surrogate code points
        // expectInvalidText ( "text\uD800]" );
        // expectInvalidText ( "text\uDFFF]" );
    }

    // Helpers

    private void expectTag ( String code, String expectedTag ) throws IOException, PdmlException {

        CorePdmlReader_OLD reader = createReader ( code );
        assertEquals ( expectedTag, reader.readTagAsString () );
    }

    private void expectSeparator ( String code, String expectedResult ) throws IOException, PdmlException {

        CorePdmlReader_OLD reader = createReader ( code );
        assertEquals ( expectedResult, reader.readSeparator() );
    }

    private void expectText ( String code, String expectedText ) throws IOException, PdmlException {

        CorePdmlReader_OLD reader = createReader ( code );
        assertEquals ( expectedText, reader.readTextAsString () );
    }

    private void expectInvalidTag ( String code ) throws IOException {

        CorePdmlReader_OLD reader = createReader ( code );
        assertThrows ( MalformedPdmlException.class, reader::readTagAsString );
    }

    private void expectInvalidText ( String code ) throws IOException {

        CorePdmlReader_OLD reader = createReader ( code );
        assertThrows ( MalformedPdmlException.class, reader::readTextAsString );
    }

    private @NotNull CorePdmlReader_OLD createReader ( @NotNull String code ) throws IOException {

        StringReader stringReader = new StringReader ( code );
        // return new CorePdmlReader ( stringReader, null, 0, 0 );
        return new CorePdmlReader_OLD (
            stringReader, new StringReaderResource ( code ) );
    }
}
