package dev.ps.pdml.core.util;

import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;

public class PdmlWriterUtil {

    // Node Text

    public static void escapeAndWriteNodeText ( @NotNull CharSequence unescapedNodeText, Writer writer ) throws IOException {

        for ( int i = 0; i < unescapedNodeText.length(); i++ ) {
            char c = unescapedNodeText.charAt ( i );
            if ( PdmlEscapeUtil.mustEscapeInNodeText ( c ) ) {
                writer.write ( CorePdmlConstants.ESCAPE_CHAR );
            }
            writer.write ( c );
        }
    }


    // String Literal

    public static void writeDoubleQuotedStringLiteral (
        @NotNull CharSequence unescapedString,
        @NotNull Writer writer ) throws IOException {

        writer.write ( '"' );

        for ( int i = 0; i < unescapedString.length(); i++ ) {
            char c = unescapedString.charAt ( i );
            if ( PdmlEscapeUtil.mustEscapeInDoubleQuotedStringLiteral ( c ) ) {
                writer.write ( CorePdmlConstants.ESCAPE_CHAR );
            }
            writer.write ( c );
            /*
            switch ( c ) {
                case '"' -> writer.write ( "\\\"" );  // " -> \"
                case '\\' -> writer.write ( "\\\\" ); // \ -> \\
                default -> writer.write ( c );
            }
             */
        }

        writer.write ( '"' );
    }

    public static void writeNullableDoubleQuotedStringLiteral (
        @Nullable CharSequence unescapedString,
        @NotNull Writer writer ) throws IOException {

        if ( unescapedString != null ) {
            writeDoubleQuotedStringLiteral ( unescapedString, writer );
        } else {
            writer.write ( "\"\"");
        }
    }

    public static void writeUnquotedStringLiteral (
        @NotNull CharSequence unescapedString,
        @NotNull Writer writer ) throws IOException {

        // TODO adapt to implement new rule for unquotedStringLiteral
        writer.write ( unescapedString.toString() );
    }
}
