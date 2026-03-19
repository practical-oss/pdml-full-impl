package dev.ps.pdml.parser.util;

import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.reader.util.NullableParsedString;
import dev.ps.pdml.parser.PdmlParserConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringLiteralsUtilTest {


    @Test
    void parseAsTextSegmentList() throws Exception {

        String code = """
            a "b b"
              ""  ~|c|~
            """;
        List<NullableParsedString<?>> list = StringLiteralsUtil.parseAsTextSegmentList (
            new StringReaderResource ( code ), PdmlParserConfig.defaultConfig(), true, false );
        assertNotNull ( list );
        assertEquals ( 4, list.size() );
        assertEquals ( "a", list.get(0).string () );
        assertEquals ( "b b", list.get(1).string () );
        assertNull ( list.get(2).string () );
        assertEquals ( "c", list.get(3).string () );
    }


    @Test
    void parseAsStringList() throws Exception {

        String code = "a b";
        ReaderResource readerResource = new StringReaderResource ( code );
        @Nullable List<String> stringList = StringLiteralsUtil.parseAsStringList (
            readerResource, PdmlParserConfig.defaultConfig(), false, false );
        assertNotNull ( stringList );
        assertEquals ( stringList, List.of ( "a", "b" ) );

        code = """
            [root
                a ^// bare
                "b c" ^// quoted
                ^/* multi-line */
                \"\"\"
                d
                e
                \"\"\"
                ^// raw
                ~|f|~
            ]
            """;
        readerResource = new StringReaderResource ( code );
        stringList = StringLiteralsUtil.parseAsStringList (
            readerResource, PdmlParserConfig.defaultConfig(), false, true );
        assertNotNull ( stringList );
        assertEquals ( stringList, List.of ( "a", "b c", "d\ne", "f" ) );

        code = "a \"\" b";
        List<String> expected = new ArrayList<> ();
        expected.add ( "a" );
        expected.add ( null );
        expected.add ( "b" );
        readerResource = new StringReaderResource ( code );
        stringList = StringLiteralsUtil.parseAsStringList (
            readerResource, PdmlParserConfig.defaultConfig(), true, false );
        assertNotNull ( stringList );
        assertEquals ( stringList, expected );

        code = "";
        readerResource = new StringReaderResource ( code );
        stringList = StringLiteralsUtil.parseAsStringList (
            readerResource, PdmlParserConfig.defaultConfig(), false, false );
        assertNull ( stringList );
    }
}
