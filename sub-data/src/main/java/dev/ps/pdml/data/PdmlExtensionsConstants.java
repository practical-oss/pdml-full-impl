package dev.ps.pdml.data;

import dev.ps.shared.basics.annotations.NotNull;

public class PdmlExtensionsConstants {

    public static final char EXTENSION_START_CHAR = '^';

    // Comments
    public static final @NotNull String LINE_COMMENT_START = "//";
    public static final @NotNull String BLOCK_COMMENT_START = "/*";
    public static final @NotNull String BLOCK_COMMENT_END = "*/";
    public static final @NotNull String LINE_COMMENT_EXTENSION_START =
        EXTENSION_START_CHAR + LINE_COMMENT_START;
    public static final @NotNull String BLOCK_COMMENT_EXTENSION_START =
        EXTENSION_START_CHAR + BLOCK_COMMENT_START;
    public static final char BLOCK_COMMENT_STAR_CHAR = BLOCK_COMMENT_START.charAt ( 1 );

    // String Literals
    public static final char QUOTED_STRING_LITERAL_DELIMITER_CHAR = '\"';
    public static final @NotNull String MULTILINE_STRING_LITERAL_DELIMITER = "\"\"\"";
    public static final char RAW_STRING_LITERAL_DELIMITER_CHAR = '~';

    // Attributes
    public static final char ATTRIBUTES_START_CHAR = '(';
    public static final char ATTRIBUTES_END_CHAR = ')';
    public static final char ATTRIBUTE_ASSIGN_CHAR = '=';
    public static final @NotNull String ATTRIBUTES_EXTENSION_START =
        String.valueOf ( EXTENSION_START_CHAR ) + ATTRIBUTES_START_CHAR;

    // Namespace
    public static final char NAMESPACE_SEPARATOR_CHAR = '|';
    public static final char NAMESPACE_SEPARATOR_CHAR_FUTURE_OPTION = ':';
    public static final @NotNull String NAMESPACE_DECLARATIONS_EXTENSION_START =
        EXTENSION_START_CHAR + "ns" + ATTRIBUTES_START_CHAR;
    public static final char NAMESPACE_DECLARATIONS_END_CHAR = ATTRIBUTES_END_CHAR;
}
