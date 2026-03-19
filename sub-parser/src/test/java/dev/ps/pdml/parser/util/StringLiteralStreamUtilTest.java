package dev.ps.pdml.parser.util;

import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.reader.util.NullableParsedString;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringLiteralStreamUtilTest {

    @Test
    void forEachTextSegment() throws Exception {

        String code = """
            a "b b"
              ""  ~|c|~
            """;
        StringReaderResource readerResource = new StringReaderResource ( code );
        try ( Reader reader = readerResource.newReader() ) {
            List<NullableParsedString<?>> list = new ArrayList<>();
            PdmlParser parser = PdmlParser.create ( reader, readerResource, PdmlParserConfig.defaultConfig() );
            StringLiteralStreamUtil.forEachTextSegment (
                list::add, parser, true );

            assertEquals ( 4, list.size() );
            assertEquals ( "a", list.get(0).string () );
            assertEquals ( "b b", list.get(1).string () );
            assertNull ( list.get(2).string () );
            assertEquals ( "c", list.get(3).string () );
        }
    }
}
