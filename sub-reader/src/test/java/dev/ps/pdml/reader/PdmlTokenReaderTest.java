package dev.ps.pdml.reader;

import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.shared.basics.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdmlTokenReaderTest {

    @Test
    void isAtNodeStart() throws IOException {

        PdmlTokenReader reader = createReader ( "[" );
        assertTrue ( reader.isAtNodeStart() );

        reader = createReader ( "[tag" );
        assertTrue ( reader.isAtNodeStart() );

        reader = createReader ( "foo" );
        assertFalse ( reader.isAtNodeStart() );
    }

    @Test
    void readBlockComment() throws IOException, PdmlException {

        PdmlTokenReader reader = createReader ( "/* comment */" );
        assertEquals ("/* comment */", reader.readBlockComment() );

        reader = createReader ( "/*comment*/" );
        assertEquals ("/*comment*/", reader.readBlockComment() );

        reader = createReader ( "/**comment**/" );
        assertEquals ("/**comment**/", reader.readBlockComment() );

        reader = createReader ( "/** */ **/" );
        assertEquals ("/** */ **/", reader.readBlockComment() );

        reader = createReader ( "/* ^/* nested */ */" );
        assertEquals ("/* ^/* nested */ */", reader.readBlockComment() );

        reader = createReader ( "/** ^/* nested */ */ **/" );
        assertEquals ("/** ^/* nested */ */ **/", reader.readBlockComment() );

        reader = createReader ( "/* ^/* nested 1 ^/* nested 2 */ */ end */" );
        assertEquals ("/* ^/* nested 1 ^/* nested 2 */ */ end */", reader.readBlockComment() );

        // Invalid

        reader = createReader ( "/* comment ]" );
        assertThrows ( PdmlException.class, reader::readBlockComment );
    }

/*
    @Test
    public void readAttributeValue() throws Exception {

        PdmlReader reader = createReader ( "a ab \"abc\"" );
        assertEquals ("a", reader.readAttributeValue() );
        assertNull ( reader.readAttributeValue() );
        reader.advanceChar ();
        assertEquals ("ab", reader.readAttributeValue() );
        reader.advanceChar ();
        assertEquals ("abc", reader.readAttributeValue() );
        // assertNull ( r.readAttributeValue() );
        assertTrue ( reader.isAtEnd() );
    }
 */

/*
    @Test
    void testSetMark() throws IOException, PdmlException {

        String code = "[root    ^\"a\"]";
        PdmlTokenReader pr = createReader ( code );
        assertNotNull ( pr.readNodeStart() );
        pr.readTagAsString();
        assertEquals ( " ", pr.readSeparator() );
    }
 */

    private @NotNull PdmlTokenReader createReader ( @NotNull String code ) throws IOException {
        return new PdmlTokenReader ( new StringReaderResource ( code ) );
    }
}
