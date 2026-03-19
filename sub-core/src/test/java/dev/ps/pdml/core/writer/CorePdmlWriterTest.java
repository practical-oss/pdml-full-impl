package dev.ps.pdml.core.writer;

import dev.ps.shared.text.writer.LineBreakKind;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class CorePdmlWriterTest {

    @Test
    public void testBasicTokens() throws IOException {

        try ( StringWriter sw = new StringWriter();
            CorePdmlWriter writer = new CorePdmlWriter ( sw ) ) {

            writer
                .writeNodeStartChar()
                .writeNodeName ( "foo" )
                .writeSpaceSeparator()
                .writeText ( "1[2]3\\4" )
                .writeNodeEndChar();
            assertEquals ( "[foo 1\\[2\\]3\\\\4]", sw.toString() );
        }
    }

    @Test
    public void testLineMode() throws IOException {

        PdmlWriterConfig config = new PdmlWriterConfig ( 4, false, LineBreakKind.UNIX );
        try ( StringWriter sw = new StringWriter();
            CorePdmlWriter writer = new CorePdmlWriter ( sw, config ) ) {

            writer
                .writeNodeStartLine ( "config", true )
                .writeTextNodeLine ( "color", "green" )
                .writeTextNodeLine ( "text", "1[2]3\\4" )
                .writeNodeEndLine ( true );
            assertEquals ( """
                [config
                    [color green]
                    [text 1\\[2\\]3\\\\4]
                ]
                """, sw.toString() );
        }
    }
}
