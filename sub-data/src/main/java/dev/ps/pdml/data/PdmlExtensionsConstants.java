package dev.ps.pdml.data;

import dev.ps.shared.basics.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class PdmlExtensionsConstants {


    public static final char EXTENSION_START_CHAR = '^';


    // Comments

    public static final char SINGLE_OR_MULTI_LINE_COMMENT_START_CHAR = '/';

    public static final @NotNull String SINGLE_OR_MULTI_LINE_COMMENT_EXTENSION_START =
        String.valueOf ( EXTENSION_START_CHAR ) + SINGLE_OR_MULTI_LINE_COMMENT_START_CHAR;

    public static final @NotNull String SINGLE_LINE_COMMENT_WITH_2_SLASHES_EXTENSION_START =
        EXTENSION_START_CHAR + "//";
    public static final @NotNull String SINGLE_LINE_COMMENT_WITH_1_SLASH_EXTENSION_START =
        EXTENSION_START_CHAR + "/";

    public static final @NotNull String MULTI_LINE_COMMENT_EXTENSION_START =
        EXTENSION_START_CHAR + "/*";
    public static final @NotNull String MULTI_LINE_COMMENT_END = "*/";
    public static final char MULTI_LINE_COMMENT_STAR_CHAR = '*';
    public static final char MULTI_LINE_COMMENT_END_CHAR = '/';


    // String Literals

    public static final char QUOTED_STRING_LITERAL_DELIMITER_CHAR = '\"';
    public static final @NotNull Map<Character, Character> QUOTED_STRING_LITERAL_ESCAPE_MAP =
        CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CHARS;
    public static final @NotNull Map<Integer, Integer> QUOTED_STRING_LITERAL_ESCAPE_CODE_POINTS =
        CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CODE_POINTS;
    public static final @NotNull Set<Character> QUOTED_STRING_LITERAL_END_CHARS =
        Set.of ( '"' );
    public static final @NotNull Set<Character> QUOTED_STRING_LITERAL_INVALID_CHARS =
        Set.of ( EXTENSION_START_CHAR, '\t', '\r', '\n' );

    public static final @NotNull String MULTILINE_STRING_LITERAL_DELIMITER = "\"\"\"";

    public static final char RAW_STRING_LITERAL_START_CHAR = '~';


    // Attributes

    public static final char ATTRIBUTES_START_CHAR = '(';
    public static final char ATTRIBUTES_END_CHAR = ')';
    public static final char ATTRIBUTE_ASSIGN_CHAR = '=';
    public static final @NotNull String ATTRIBUTES_EXTENSION_START =
        String.valueOf ( EXTENSION_START_CHAR ) + ATTRIBUTES_START_CHAR;
    // public static final char ATTRIBUTES_EXTENSION_ID_CHAR = 'a';

    // Namespace
    // public static final char NAMESPACE_PREFIX_NAME_SEPARATOR = ':';
    public static final char NAMESPACE_SEPARATOR_CHAR = '|';
    // public static final String NAMESPACE_DECLARATIONS_START = ATTRIBUTES_START + "ns";
    // public static final String NAMESPACE_DECLARATIONS_START = "[@ns";
    public static final @NotNull String NAMESPACE_DECLARATIONS_EXTENSION_START =
        EXTENSION_START_CHAR + "ns" + ATTRIBUTES_START_CHAR;
    public static final char NAMESPACE_DECLARATIONS_END = ATTRIBUTES_END_CHAR;

/* NEW
    public static final String NAMESPACE_NAMESPACE = "ns";
 */
}
