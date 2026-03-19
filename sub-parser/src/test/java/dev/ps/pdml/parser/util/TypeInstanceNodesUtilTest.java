package dev.ps.pdml.parser.util;

import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.prt.type.CommonTypes;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TypeInstanceNodesUtilTest {

    @Test
    void parseObjects() throws Exception {

        String code = """
            [root
                [el 1]
                [el]
                [el 3]
            ]
            """;
        List<Integer> expected = new ArrayList<>();
        expected.add ( 1 );
        expected.add ( null );
        expected.add ( 3 );
        assertEquals ( expected, TypeInstanceNodesUtil.parseObjects (
            new StringReaderResource ( code ),
            PdmlParserConfig.defaultConfig(),
            CommonTypes.INT32_OR_NULL,
            true, true ) );
    }


    @Test
    void parseObjectsMap() throws Exception {

        String code = """
            [root
                [el1 1]
                [el2]
                [el3 3]
            ]
            """;
        Map<String,Integer> expected = new HashMap<>();
        expected.put ( "el1", 1 );
        expected.put ( "el2", null );
        expected.put ( "el3", 3 );
        assertEquals ( expected, TypeInstanceNodesUtil.parseObjectsMap (
            new StringReaderResource ( code ),
            PdmlParserConfig.defaultConfig(),
            CommonTypes.INT32_OR_NULL,
            true, true ) );
    }
}
