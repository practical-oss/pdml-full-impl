package dev.ps.pdml.data.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public class WhitespaceUtil {

    public static boolean isWhitespaceChar ( char c ) {

        return c == ' '
            || c == '\n'
            || c == '\r'
            || c == '\t'
            || c == '\f';
    }

    public static boolean isNotWhitespaceChar ( char c ) {
        return ! isWhitespaceChar ( c );
    }

    public static boolean isWhitespaceString ( @NotNull String string ) {

        for ( int i = 0; i < string.length(); i++) {
            char c = string.charAt ( i );
            if ( isNotWhitespaceChar ( c ) ) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotWhitespaceString ( @NotNull String string ) {
        return ! isWhitespaceString ( string );
    }

    public static @Nullable String trim ( @NotNull String string ) {

        String result = trimLeft ( string );
        if ( result == null ) return null;
        return trimRight ( result );
    }

    public static @Nullable String trimLeft ( @NotNull String string ) {

        int firstNonWSIndex = -1;
        for ( int i = 0; i < string.length(); i++) {
            char c = string.charAt ( i );
            if ( isNotWhitespaceChar ( c ) ) {
                firstNonWSIndex = i;
                break;
            }
        }

        if ( firstNonWSIndex == 0 ) {
            return string;
        } else if ( firstNonWSIndex == -1 ) {
            return null;
        } else {
            return string.substring ( firstNonWSIndex );
        }
    }

    public static @Nullable String trimRight ( @NotNull String string ) {

        int lastNonWSIndex = -1;
        int maxIndex = string.length() - 1;
        for ( int i = maxIndex; i >= 0; i--) {
            char c = string.charAt ( i );
            if ( isNotWhitespaceChar ( c ) ) {
                lastNonWSIndex = i;
                break;
            }
        }

        if ( lastNonWSIndex == maxIndex ) {
            return string;
        } else if ( lastNonWSIndex == -1 ) {
            return null;
        } else {
            return string.substring ( 0, lastNonWSIndex + 1 );
        }
    }
}
