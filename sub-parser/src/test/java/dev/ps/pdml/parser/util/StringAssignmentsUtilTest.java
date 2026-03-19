package dev.ps.pdml.parser.util;

import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.prt.type.CommonTypes;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StringAssignmentsUtilTest {

    @Test
    void parseScalarMap1() throws Exception {

        String code = """
                n1 = 100
                n2=""
                n3 =    ~|300|~
            ]
            """;
        Map<String,Integer> expected = new HashMap<>();
        expected.put ( "n1", 100 );
        expected.put ( "n2", null );
        expected.put ( "n3", 300 );

        try ( StringReader reader = new StringReader ( code ) ) {
            PdmlParser pdmlParser = PdmlParser.create ( reader, new StringReaderResource ( code ), PdmlParserConfig.defaultConfig () );
            assertEquals ( expected, StringAssignmentsUtil.parseAsScalarMap (
                pdmlParser, CommonTypes.INT32, true ) );
        }

        try ( StringReader reader = new StringReader ( code ) ) {
            PdmlParser pdmlParser = PdmlParser.create ( reader, new StringReaderResource ( code ), PdmlParserConfig.defaultConfig () );
            assertThrows ( InvalidDataException.class, () -> StringAssignmentsUtil.parseAsScalarMap (
                pdmlParser, CommonTypes.INT32, false ) );
        }
    }

    @Test
    void parseScalarMap2() throws Exception {

        String code = """

            [root
                n1 = 100
                n2=""
                n3 =    ~|300|~
            ]

            """;
        Map<String,Integer> expected = new HashMap<>();
        expected.put ( "n1", 100 );
        expected.put ( "n2", null );
        expected.put ( "n3", 300 );
        assertEquals ( expected, StringAssignmentsUtil.parseAsScalarMap (
            new StringReaderResource ( code ),
            PdmlParserConfig.defaultConfig(),
            CommonTypes.INT32,
            true, true ) );

        // throw because null not allowed
        assertThrows ( InvalidDataException.class, () -> StringAssignmentsUtil.parseAsScalarMap (
            new StringReaderResource ( code ),
            PdmlParserConfig.defaultConfig(),
            CommonTypes.INT32,
            false, true ) );

        // throw because no root node
        assertThrows ( InvalidDataException.class, () -> StringAssignmentsUtil.parseAsScalarMap (
            new StringReaderResource ( code ),
            PdmlParserConfig.defaultConfig(),
            CommonTypes.INT32,
            true, false ) );
    }

    @Test
    void parseAsStringMap() throws Exception {

        String code = "a=b c=d";
        var expected = Map.of (
            "a", "b",
            "c", "d" );
        assertEquals ( expected, StringAssignmentsUtil.parseAsStringMap (
            new StringReaderResource ( code ),
            PdmlParserConfig.defaultConfig(),
            false, false ) );

        code = """
            k1 = v1 ^// bare
            "k 2" = "v 2" ^// quoted
            ^/* multi-line */
            \"\"\"
            k
            3
            \"\"\" =
            \"\"\"
            v
            3
            \"\"\"
            ^// raw
            ~|k4|~ = ~|v4|~
            """;
        expected = Map.of (
            "k1", "v1",
            "k 2", "v 2",
            "k\n3", "v\n3",
            "k4", "v4" );
        assertEquals ( expected, StringAssignmentsUtil.parseAsStringMap (
            new StringReaderResource ( code ),
            PdmlParserConfig.defaultConfig(),
            false, false ) );
    }
}
