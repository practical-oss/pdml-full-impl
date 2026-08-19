package dev.ps.pdml.data;

import dev.ps.shared.basics.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static dev.ps.pdml.data.PdmlExtensionsConstants.*;

public class CorePdmlConstants {

    public static final char NODE_START_CHAR = '[';
    public static final @NotNull String NODE_START_STRING = String.valueOf ( NODE_START_CHAR );
    public static final char NODE_END_CHAR = ']';
    public static final @NotNull String NODE_END_STRING = String.valueOf ( NODE_END_CHAR );

    public static final char ESCAPE_CHAR = '\\';

    public static final @NotNull Set<Character> TAG_END_CHARS =
        Set.of ( NODE_END_CHAR, ' ', '\t', '\n', '\r' );

    public static final @NotNull Set<Character> TEXT_LEAF_END_CHARS =
        Set.of ( NODE_START_CHAR, NODE_END_CHAR );

    public static final @NotNull Set<Character> INVALID_TAG_CHARS =
        Set.of (
            NODE_START_CHAR,
            EXTENSION_START_CHAR,
            QUOTED_STRING_LITERAL_DELIMITER_CHAR, RAW_STRING_LITERAL_DELIMITER_CHAR,
            NAMESPACE_SEPARATOR_CHAR );

    public static final @NotNull Set<Character> INVALID_TEXT_LEAF_CHARS =
        Set.of ( EXTENSION_START_CHAR );

    public static final @NotNull Map<@NotNull Integer, @NotNull Integer> CODE_POINT_ESCAPES = createCodePointEscapes();

    private static @NotNull Map<@NotNull Integer, @NotNull Integer> createCodePointEscapes() {

        Map<@NotNull Integer, @NotNull Integer> map = new HashMap<>();

        map.put ( (int) ESCAPE_CHAR, (int) ESCAPE_CHAR );

        map.put ( (int) NODE_START_CHAR, (int) NODE_START_CHAR );
        map.put ( (int) NODE_END_CHAR, (int) NODE_END_CHAR );

        map.put ( (int) 's', (int) ' ' );
        map.put ( (int) 't', (int) '\t' );
        map.put ( (int) 'n', (int) '\n' );
        map.put ( (int) 'r', (int) '\r' );
        map.put ( (int) 'f', (int) '\f' );

        map.put ( (int) EXTENSION_START_CHAR, (int) EXTENSION_START_CHAR );

        map.put ( (int) ATTRIBUTES_START_CHAR, (int) ATTRIBUTES_START_CHAR );
        map.put ( (int) ATTRIBUTES_END_CHAR, (int) ATTRIBUTES_END_CHAR );
        map.put ( (int) ATTRIBUTE_ASSIGN_CHAR, (int) ATTRIBUTE_ASSIGN_CHAR );

        map.put ( (int) QUOTED_STRING_LITERAL_DELIMITER_CHAR, (int) QUOTED_STRING_LITERAL_DELIMITER_CHAR );
        map.put ( (int) RAW_STRING_LITERAL_DELIMITER_CHAR, (int) RAW_STRING_LITERAL_DELIMITER_CHAR );

        map.put ( (int) NAMESPACE_SEPARATOR_CHAR, (int) NAMESPACE_SEPARATOR_CHAR );

        // Reserved chars
        map.put ( (int) ':', (int) ':' );
        map.put ( (int) ',', (int) ',' );
        map.put ( (int) '`', (int) '`' );
        map.put ( (int) '!', (int) '!' );
        map.put ( (int) '$', (int) '$' );

        return Collections.unmodifiableMap ( map );
    }

    public static final char PATH_SEPARATOR = '/';

    public static final @NotNull String PDML_FILE_NAME_EXTENSION = "pdml";
}
