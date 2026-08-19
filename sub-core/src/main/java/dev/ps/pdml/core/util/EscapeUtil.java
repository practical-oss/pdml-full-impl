package dev.ps.pdml.core.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static dev.ps.pdml.data.CorePdmlConstants.*;
import static dev.ps.pdml.data.PdmlExtensionsConstants.EXTENSION_START_CHAR;
import static dev.ps.pdml.data.PdmlExtensionsConstants.QUOTED_STRING_LITERAL_DELIMITER_CHAR;

public class EscapeUtil {

    public static final @NotNull Map<Character, String> TEXT_ESCAPES = Map.of (
        NODE_START_CHAR, "\\[",
        NODE_END_CHAR, "\\]",
        ESCAPE_CHAR, "\\\\",
        EXTENSION_START_CHAR, "\\^" );

    public static final @NotNull Map<Character, String> QUOTED_STRING_LITERAL_ESCAPES = Map.of (
        QUOTED_STRING_LITERAL_DELIMITER_CHAR, "\\\"",
        ESCAPE_CHAR, "\\\\",
        EXTENSION_START_CHAR, "\\^" );

    public static final @NotNull Map<Character, String> BARE_STRING_LITERAL_ESCAPES = bareStringLiteralEscapes();

    private static @NotNull Map<Character, String> bareStringLiteralEscapes() {

        Map<Character, String> map = new HashMap<>();

        map.put ( NODE_START_CHAR, "\\[" );
        map.put ( NODE_END_CHAR, "\\]" );
        map.put ( ESCAPE_CHAR, "\\\\" );
        map.put ( EXTENSION_START_CHAR, "\\^" );

        // whitespace
        map.put ( ' ', "\\s" );
        map.put ( '\n', "\\n" );
        map.put ( '\r', "\\r" );
        map.put ( '\t', "\\t" );
        map.put ( '\f', "\\f" );

        // reserved
        map.put ( '"', "\\f" );
        map.put ( '~', "\\f" );
        map.put ( '|', "\\f" );
        map.put ( ':', "\\f" );

        return Collections.unmodifiableMap ( map );
    }


    public static void writeTag (
        @NotNull CharSequence unescapedTag,
        @NotNull Writer writer ) throws IOException {

        writeBareStringLiteral ( unescapedTag, writer );
    }

    public static void writeText (
        @NotNull CharSequence unescapedText,
        @NotNull Writer writer ) throws IOException {

        writeAsEscaped ( unescapedText, writer, TEXT_ESCAPES );
    }

    public static void writeBareStringLiteral (
        @NotNull CharSequence unescapedString,
        @NotNull Writer writer ) throws IOException {

        writeAsEscaped ( unescapedString, writer, BARE_STRING_LITERAL_ESCAPES );
    }

    public static void writeQuotedStringLiteral (
        @Nullable CharSequence unescapedString,
        @NotNull Writer writer ) throws IOException {

        writer.write ( '"' );
        writeAsEscaped ( unescapedString, writer, QUOTED_STRING_LITERAL_ESCAPES );
        writer.write ( '"' );
    }


    public static @NotNull String toTag ( @NotNull CharSequence unescapedTag ) {
        return toBareStringLiteral ( unescapedTag );
    }

    public static @NotNull String toText ( @NotNull CharSequence unescapedText ) {
        return toEscapedString ( unescapedText, TEXT_ESCAPES );
    }

    public static @NotNull String toBareStringLiteral ( @NotNull CharSequence unescapedString ) {
        return toEscapedString ( unescapedString, BARE_STRING_LITERAL_ESCAPES );
    }

    public static @NotNull String toQuotedStringLiteral ( @NotNull CharSequence unescapedString ) {
        return toEscapedString ( unescapedString, QUOTED_STRING_LITERAL_ESCAPES );
    }


    private static void writeAsEscaped (
        @Nullable CharSequence unescapedString,
        @NotNull Writer writer,
        @NotNull Map<Character, String> escapeMap ) throws IOException {

        if ( unescapedString == null || unescapedString.isEmpty() ) return;

        for ( int i = 0; i < unescapedString.length(); i++ ) {
            char ch = unescapedString.charAt ( i );

            String escapedChar = escapeMap.get ( ch );
            if ( escapedChar == null ) {
                writer.write ( ch );
            } else {
                writer.write ( escapedChar );
            }
        }
    }

    private static @NotNull String toEscapedString (
        @NotNull CharSequence unescapedString,
        @NotNull Map<Character, String> escapeMap ) {

        try ( StringWriter stringWriter = new StringWriter() ) {
            writeAsEscaped ( unescapedString, stringWriter, escapeMap );
            return stringWriter.toString();
        } catch ( IOException e ) {
            throw new UncheckedIOException ( e );
        }
    }
}
