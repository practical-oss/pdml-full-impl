package dev.ps.pdml.decoder;

import dev.ps.prt.type.CommonTypes;
import dev.ps.prt.type.collection.list.impl.NativeListType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PdmlDecoderUtilTest {

    @Test
    void decodeString() throws Exception {

        assertEquals ( "ab",
            PdmlDecoderUtil.decodeString ( CommonTypes.STRING, "ab" ) );
        assertEquals ( "a\nb",
            PdmlDecoderUtil.decodeString ( CommonTypes.STRING, "a\\nb" ) );
        assertEquals ( "foobar",
            PdmlDecoderUtil.decodeString ( CommonTypes.STRING, "^[const c = foo]^[ins_const c]bar" ) );

        NativeListType<Integer> type = new NativeListType<> ( "int_list", CommonTypes.INT32, null );
        assertEquals ( List.of ( 1, 123 ),
            PdmlDecoderUtil.decodeString ( type, "[el 1][el 123]" ) );
        assertEquals ( List.of ( 1, 123 ),
            PdmlDecoderUtil.decodeString ( type,"1 \"123\"" ) );
    }
}
