package dev.ps.pdml.data.node.leaf;

import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextLeafTest {

    enum Quality {
        BAD, MEDIUM, GOOD
    }

    @Test
    void toEnum() throws Exception {

        TextLeaf textLeaf = new TextLeaf ( "GOOD", null );
        Quality quality = textLeaf.toEnum ( Quality.class, false );
        assertEquals ( "GOOD", quality.name() );

        textLeaf = new TextLeaf ( "good", null );
        quality = textLeaf.toEnum ( Quality.class, true );
        assertEquals ( "GOOD", quality.name() );

        final TextLeaf textLeaf2 = new TextLeaf ( "foo", null );
        // quality = textLeaf2.toEnum ( Quality.class, false );
        assertThrows ( InvalidPdmlDataException.class, () -> textLeaf2.toEnum ( Quality.class, false ) );
    }
}
