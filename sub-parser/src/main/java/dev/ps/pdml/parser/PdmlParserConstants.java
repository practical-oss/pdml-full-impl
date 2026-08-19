package dev.ps.pdml.parser;

import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.shared.basics.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dev.ps.pdml.data.PdmlExtensionsConstants.*;

public class PdmlParserConstants {

    static final char LINE_OR_BLOCK_COMMENT_START_CHAR = '/';

    static final @NotNull Set<Character> SHARED_BARE_STRING_END_CHARS =
        Set.of ( EXTENSION_START_CHAR, ' ', '\t', '\n', '\r' );

    static final @NotNull Set<Character> SHARED_BARE_STRING_INVALID_CHARS =
        Set.of ( QUOTED_STRING_LITERAL_DELIMITER_CHAR, RAW_STRING_LITERAL_DELIMITER_CHAR );

    record ScopeConfig (
        @NotNull Set<Character> bareStringEndChars,
        @NotNull Set<Character> bareStringInvalidChars,
        @NotNull Map<@NotNull Integer, @NotNull Integer> codePointEscapes,
        boolean commentsAllowed,
        boolean stringLiteralsAllowed ) {}

    static final @NotNull ScopeConfig DOCUMENT_START_CONFIG = new ScopeConfig (
        SHARED_BARE_STRING_END_CHARS,
        SHARED_BARE_STRING_INVALID_CHARS,
        CorePdmlConstants.CODE_POINT_ESCAPES,
        true, false );

    static final @NotNull ScopeConfig TAG_CONFIG = new ScopeConfig (
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            CorePdmlConstants.NODE_END_CHAR,
            NAMESPACE_SEPARATOR_CHAR, NAMESPACE_SEPARATOR_CHAR_FUTURE_OPTION ),
        addToCharSet ( SHARED_BARE_STRING_INVALID_CHARS,
            CorePdmlConstants.NODE_START_CHAR ),
        CorePdmlConstants.CODE_POINT_ESCAPES,
        false, false );

    static final @NotNull ScopeConfig TEXT_LEAF_CONFIG = new ScopeConfig (
        addToCharSet ( CorePdmlConstants.TEXT_LEAF_END_CHARS, // [ ]
            EXTENSION_START_CHAR ),
        Set.of(),
        CorePdmlConstants.CODE_POINT_ESCAPES,
        true, true );

    static final @NotNull ScopeConfig BARE_STRING_LITERAL_CONFIG = new ScopeConfig (
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            CorePdmlConstants.NODE_START_CHAR,
            CorePdmlConstants.NODE_END_CHAR ),
        SHARED_BARE_STRING_INVALID_CHARS,
        CorePdmlConstants.CODE_POINT_ESCAPES,
        false, false );

    static final @NotNull ScopeConfig QUOTED_STRING_LITERAL_CONFIG = new ScopeConfig (
        Set.of ( EXTENSION_START_CHAR, QUOTED_STRING_LITERAL_DELIMITER_CHAR ),
        Set.of ( '\t', '\r', '\n', '\f' ),
        CorePdmlConstants.CODE_POINT_ESCAPES,
        false, false );

    static final @NotNull ScopeConfig ATTRIBUTE_NAME_CONFIG = new ScopeConfig (
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            ATTRIBUTE_ASSIGN_CHAR ),
        addToCharSet ( SHARED_BARE_STRING_INVALID_CHARS,
            CorePdmlConstants.NODE_START_CHAR,
            CorePdmlConstants.NODE_END_CHAR,
            NAMESPACE_SEPARATOR_CHAR ), // "ns|n = value" is not allowed because it looks like using a namespace in an attribute name
        CorePdmlConstants.CODE_POINT_ESCAPES,
        false, false );

    static final @NotNull ScopeConfig ATTRIBUTE_VALUE_CONFIG = new ScopeConfig (
        addToCharSet ( SHARED_BARE_STRING_END_CHARS,
            ATTRIBUTES_END_CHAR,
            CorePdmlConstants.NODE_END_CHAR ),
        addToCharSet ( SHARED_BARE_STRING_INVALID_CHARS,
            CorePdmlConstants.NODE_START_CHAR,
            CorePdmlConstants.NODE_END_CHAR ),
        CorePdmlConstants.CODE_POINT_ESCAPES,
        false, false );

    private static @NotNull Set<Character> addToCharSet (
        @NotNull Set<Character> charSet,
        char... chars ) {

        Set<Character> result = new HashSet<> ( charSet );
        for ( char c : chars ) {
            result.add ( c );
        }
        return Collections.unmodifiableSet ( result );
    }
}
