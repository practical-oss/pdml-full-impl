package dev.ps.pdml.encoder;

import dev.ps.prt.type.CommonTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PdmlEncoderUtilTest {

    @Test
    void encodeToString() throws Exception {

        assertEquals ( "ab",
            PdmlEncoderUtil.encodeToString ( "ab", CommonTypes.STRING ) );
        assertEquals ( "a\\[\\]",
            PdmlEncoderUtil.encodeToString ( "a[]", CommonTypes.STRING ) );
/*
        assertEquals ( "foobar",
            PdmlDecoderUtil.decodeString ( "^[const c = foo]^[ins c]bar", StringType.INSTANCE ) );

        NativeListType<Integer> type = new NativeListType<> ( "int_list", IntegerType.INSTANCE );
        assertEquals ( List.of ( 1, 123 ),
            PdmlDecoderUtil.decodeString ( "[el 1][el 123]", type ) );
        assertEquals ( List.of ( 1, 123 ),
            PdmlDecoderUtil.decodeString ( "1 \"123\"", type ) );
 */
    }
}
