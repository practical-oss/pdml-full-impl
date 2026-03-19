package dev.ps.pdml.core.util;

import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.shared.basics.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;

public class PdmlEscapeUtil {


    // Node text

    public static boolean mustEscapeInNodeText ( char c ) {

        return switch ( c ) {
            case CorePdmlConstants.NODE_START_CHAR,
                CorePdmlConstants.NODE_END_CHAR,
                CorePdmlConstants.ESCAPE_CHAR,
                PdmlExtensionsConstants.EXTENSION_START_CHAR -> true;
            default -> false;
        };
    }

    public static @NotNull String escapeNodeText ( @NotNull CharSequence unescapedNodeText ) {

        try ( StringWriter writer = new StringWriter() ) {
            PdmlWriterUtil.escapeAndWriteNodeText ( unescapedNodeText, writer );
            return writer.toString();
        } catch ( IOException e ) {
            throw new RuntimeException ( e );
        }
    }


    // Double-quoted string literal

    public static boolean mustEscapeInDoubleQuotedStringLiteral ( char c ) {

        return switch ( c ) {
            // case PdmlExtensionsConstants.STRING_LITERAL_DOUBLE_QUOTE, CorePdmlConstants.ESCAPE_CHARACTER -> true;
            case '"', CorePdmlConstants.ESCAPE_CHAR -> true;
            default -> false;
        };
    }

    public static @NotNull String escapeDoubleQuotedStringLiteral ( @NotNull CharSequence unescapedValue ) {

        try ( StringWriter writer = new StringWriter() ) {
            PdmlWriterUtil.writeDoubleQuotedStringLiteral ( unescapedValue, writer );
            return writer.toString();
        } catch ( IOException e ) {
            throw new RuntimeException ( e );
        }
    }


    // Other

    public static boolean isEscapeCharacter ( char c ) {
        return c == CorePdmlConstants.ESCAPE_CHAR;
    }
}
