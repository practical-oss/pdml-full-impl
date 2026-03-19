package dev.ps.pdml.utils.parser;

/*
import dev.pp.pdml.data.exception.PdmlException;
import dev.pp.pdml.utils.parser.StringMapUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
 */

class StringMapUtilTest {

/*
    @Test
    void parseString () throws Exception {

        String code = """
            [config
                [a1 v1]
                [a2 v2 v2
                    line 2
                    end]

                ^// comment a3
                [a3]

                [a4  ]


 */
//                ^/* comment a5 */
/*
                [a5]
            ]
            """;
        Map<String, String> stringMap = StringMapUtil.parseString ( code, true );
        assertNotNull ( stringMap );
        assertEquals ( 5, stringMap.size() );
        assertEquals ( "v1", stringMap.get ( "a1" ) );
        String v2 = stringMap.get ( "a2" );
        assertEquals ( """
        v2 v2
                line 2
                end""", stringMap.get ( "a2" ) );
        assertTrue ( stringMap.containsKey ( "a3" ) );
        assertNull ( stringMap.get ( "a3" ) );
        assertEquals ( " ", stringMap.get ( "a4" ) );
        assertNull ( stringMap.get ( "a5" ) );

        code = """
            [a1 v1]
            [a2 v2]
            """;
        stringMap = StringMapUtil.parseString ( code, false );
        assertNotNull ( stringMap );
        assertEquals ( 2, stringMap.size() );
        assertEquals ( "v1", stringMap.get ( "a1" ) );
        assertEquals ( "v2", stringMap.get ( "a2" ) );

        assertNull ( StringMapUtil.parseString ( "[config]", true ) );
 */

//        assertNull ( StringMapUtil.parseString ( "[config   ^/* comment*/ ]", true ) );

/*
        stringMap = StringMapUtil.parseString ( "[config [a1]]", true );
        assertNotNull ( stringMap );
        assertTrue ( stringMap.containsKey ( "a1" ) );
        assertNull ( stringMap.get ( "a1" ) );

        stringMap = StringMapUtil.parseString ( "[a1]", false );
        assertNotNull ( stringMap );
        assertTrue ( stringMap.containsKey ( "a1" ) );
        assertNull ( stringMap.get ( "a1" ) );

        // Invalid code

        // key defined twice
        final String code_2 = """
            [config
                [a1 v1]
                [a1 v2]
            ]
            """;
        assertThrows ( PdmlException.class, () -> StringMapUtil.parseString ( code_2, true ) );

        // child nodes not allowed
        final String code_3 = """
            [config
                [size [width=10]
            ]""";
        assertThrows ( PdmlException.class, () -> StringMapUtil.parseString ( code_3, true ) );

        // attributes not allowed
        final String code_4 = """
            [config
                [length [@ unit=cm] 100]
            ]""";
        assertThrows ( PdmlException.class, () -> StringMapUtil.parseString ( code_4, true ) );

        // text in root node not allowed
        final String code_5 = """
            [config
                text
                [a1 v1]
            """;
        assertThrows ( PdmlException.class, () -> StringMapUtil.parseString ( code_5, true ) );
    }
 */
}
