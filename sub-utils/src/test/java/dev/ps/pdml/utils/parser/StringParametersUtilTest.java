package dev.ps.pdml.utils.parser;

/*
import dev.pp.core.parameters.parameters.Parameters;
import dev.pp.pdml.utils.parser.StringParametersUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
 */

@Deprecated
class StringParametersUtilTest {

/*
    @Test
    void parseString() throws Exception {

        String code = """
            [config
                [n1 v1]
                [n2]
                [n3 v3 v3]
            ]
            """;

        Parameters<String> stringParameters = StringParametersUtil.parseString (
            code, true );

        assertNotNull ( stringParameters );
        assertEquals ( 3, stringParameters.count() );
        assertEquals ( "v1", stringParameters.value ( "n1" ) );
        assertNull ( stringParameters.value ( "n2" ) );
        assertEquals ( "v3 v3", stringParameters.value ( "n3" ) );

        code = """
            [n1 v1]
            [n2]
            [n3 v3 v3]
            """;

        stringParameters = StringParametersUtil.parseString (
            code, false );
        assertNotNull ( stringParameters );
        assertEquals ( 3, stringParameters.count() );
        assertEquals ( "v1", stringParameters.value ( "n1" ) );
        assertNull ( stringParameters.value ( "n2" ) );
        assertEquals ( "v3 v3", stringParameters.value ( "n3" ) );
    }
 */
}
