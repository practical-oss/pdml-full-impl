package dev.ps.pdml.ext.types.instances;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.ext.types.PdmlType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrimmedTextOrStringLiteralTypeTest {

    @Test
    void test() throws Exception {

        PdmlType<?> type = TrimmedTextOrStringLiteralType.NON_NULL_INSTANCE;

        TaggedNode rootNode = TextTypeTest.parse ( "[root i = 1]", type );
        assertEquals ( "i = 1", rootNode.toText() );

        rootNode = TextTypeTest.parse ( """
            [root
                i = 1
            ]""", type );
        assertEquals ( "i = 1", rootNode.toText() );

        rootNode = TextTypeTest.parse ( """
            [root "i = 1"]""", type );
        assertEquals ( "i = 1", rootNode.toText() );

        rootNode = TextTypeTest.parse ( """
            [root
                " i = 1 "
            ]""", type );
        assertEquals ( " i = 1 ", rootNode.toText() );

        rootNode = TextTypeTest.parse ( "[root ~|i = 1|~]", type );
        assertEquals ( "i = 1", rootNode.toText() );

        rootNode = TextTypeTest.parse ( """
            [root ""\"
                if i = 1
                    j[i] = a
                .

                ""\"
            ]""", type );
        assertEquals ( """
            if i = 1
                j[i] = a
            .
            """, rootNode.toText() );

        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( "[root]", type ) );

        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( """
                [root ""]
                """, type ) );

        rootNode = TextTypeTest.parse ( """
                [root ""]
                """, TrimmedTextOrStringLiteralType.NULLABLE_INSTANCE );
        assertNull ( rootNode.toTextOrNull() );
    }
}
