package dev.ps.pdml.data.util;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.pdml.writer.node.PdmlNodeWriterUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WhitespaceUtilTest {

    @ParameterizedTest
    @CsvSource ( {
        "foo, false",
        "' f  ', false",
        "' ', true",
        "' \n\r\t\f', true" } )
    void isWhitespaceString ( String string, boolean expectedResult ) {
        assertEquals( expectedResult, WhitespaceUtil.isWhitespaceString( string ) );
    }

    @ParameterizedTest
    @CsvSource ( {
        "foo, foo",
        "' foo', foo",
        "'foo ', foo",
        "' foo ', foo",
        "' \n\r\t\ff \n\r\t\f', f",
        "' ',",
        "' \n\r\t\f'," } )
    void trim ( String string, String expectedResult ) {
        assertEquals( expectedResult, WhitespaceUtil.trim( string ) );
    }

    @ParameterizedTest
    @CsvSource ( {
        "foo, foo",
        "' foo', foo",
        "'foo ', 'foo '",
        "' foo ', 'foo '",
        "' \n\r\t\ff \n\r\t\f', 'f \n\r\t\f'",
        "' ',",
        "' \n\r\t\f'," } )
    void trimLeft ( String string, String expectedResult ) {
        assertEquals( expectedResult, WhitespaceUtil.trimLeft( string ) );
    }

    @ParameterizedTest
    @CsvSource ( {
        "foo, foo",
        "' foo', ' foo'",
        "'foo ', foo",
        "' foo ', ' foo'",
        "' \n\r\t\ff \n\r\t\f', ' \n\r\t\ff'",
        "' ',",
        "' \n\r\t\f'," } )
    void trimRight ( String string, String expectedResult ) {
        assertEquals( expectedResult, WhitespaceUtil.trimRight( string ) );
    }

    @Test
    void removePrettyPrintingWhitespaceInTree() throws IOException, PdmlException {

        String code = """
            [root
                [child text]
            ]
            """;
        String expected = "[root\n[child text]]";
        testRemovePrettyPrintingWhitespaceInTree ( code, expected );

        code = """
            [root\s
                [child text]
            ]
            """;
        expected = "[root [child text]]";
        testRemovePrettyPrintingWhitespaceInTree ( code, expected );

        code = """
            [root
                [ch1 text text]
                [ch2  text text ]
                [ch3   ]
                [ch4]
            ]
            """;
        expected = "[root\n[ch1 text text][ch2  text text ][ch3   ][ch4]]";
        testRemovePrettyPrintingWhitespaceInTree ( code, expected );
    }

    void testRemovePrettyPrintingWhitespaceInTree (
        String code, String expectedResult ) throws IOException, PdmlException {

        testRemoveWhitespaceInTree ( code, expectedResult, true, false, false );
    }

    @Test
    void removeWhitespaceTextLeavesInTextNodes() throws IOException, PdmlException {

        String code = """
            [root
                [ws     ]
                [child text]
                [ws
                \s\s\s
                ]
            ]""";
        String expected = """
            [root
                [ws]
                [child text]
                [ws]
            ]""";
        testRemoveWhitespaceInTree ( code, expected, false, true, false );
    }

    @Test
    void trimTextNodes() throws IOException, PdmlException {

        String code = """
            [root
                [x 100.0  ]
                [y   1.123]
                [z   3    ]
                [ws     ]
                [child text
                ]
                [child text]
            ]""";
        String expected = """
            [root
                [x 100.0]
                [y 1.123]
                [z 3]
                [ws     ]
                [child text]
                [child text]
            ]""";
        testRemoveWhitespaceInTree ( code, expected, false, false, true );
    }

    @Test
    void removeAllWhitespace() throws IOException, PdmlException {

        String code = """
            [root
                [x 100.0  ]
                [y   1.123]
                [z   3    ]
                [ws     ]
            ]""";
        String expected = "[root\n[x 100.0][y 1.123][z 3][ws]]";
        testRemoveWhitespaceInTree ( code, expected, true, true, true );
    }

    void testRemoveWhitespaceInTree (
        String code, String expectedResult,
        boolean removeWhitespaceTextLeafSiblings,
        boolean removeWhitespaceTextLeavesInTextNodes,
        boolean trimTextNodes ) throws IOException, PdmlException {

        TaggedNode rootNode = ParseASTUtil.parseString ( code );
        WhitespaceUtil.removeWhitespaceInTree ( rootNode,
            removeWhitespaceTextLeafSiblings, removeWhitespaceTextLeavesInTextNodes, trimTextNodes );
        String result = PdmlNodeWriterUtil.writeToString ( rootNode, false );
        assertEquals( expectedResult, result );
    }
}
