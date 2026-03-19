package dev.ps.pdml.ext.types.instances;

import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.ext.types.PdmlType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringLiteralTypeTest {

    @Test
    void test() throws Exception {

        PdmlType<?> type = StringLiteralType.NON_NULL_INSTANCE;

        // Unquoted string literal
        TaggedNode rootNode = TextTypeTest.parse ( "[root a\\sb]", type );
        assertEquals ( "a b", rootNode.toText() );
        String nodeString = rootNode.getCastedJavaObjectContained();
        assertNotNull ( nodeString );
        assertEquals ( "a b", nodeString );

        // Quoted string literal
        rootNode = TextTypeTest.parse ( """
            [root "a b"]
            """, type );
        assertEquals ( "a b", rootNode.toText() );

        // Raw string literal
        rootNode = TextTypeTest.parse ( "[root ~|a b|~]", type );
        assertEquals ( "a b", rootNode.toText() );

        // Multiline string literal
        rootNode = TextTypeTest.parse ( """
            [root ""\"
                a b
                ""\"]
            """, type );
        assertEquals ( "a b", rootNode.toText() );


        // Whitespace and comments are ignored
        rootNode = TextTypeTest.parse ( """
            [root
                ^/* comment */
                ^/* comment */
                "a b"
                ^/* comment */
                ^/* comment */
            ]
            """, type );
        assertEquals ( "a b", rootNode.toText() );


        // Nullable

        rootNode = TextTypeTest.parse ( "[root]", StringLiteralType.NULLABLE_INSTANCE );
        assertNull ( rootNode.toTextOrNull() );

        rootNode = TextTypeTest.parse ( "[root \"\"]", StringLiteralType.NULLABLE_INSTANCE );
        assertNull ( rootNode.toTextOrNull() );


        // Validator

        var threeCharsType = new StringLiteralType (
            "3_chars",
            false,
            objectTokenPair -> {
                String string = objectTokenPair.object();
                assert string != null;
                if ( string.length() != 3 ) {
                    throw new InvalidPdmlDataException (
                        "Must be 3 chars",
                        "INVALID",
                        objectTokenPair.textLocation () );
                }
            } );

        rootNode = TextTypeTest.parse ( "[root 123]", threeCharsType );
        assertEquals ( "123", rootNode.toText() );

        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( "[root 1234]", threeCharsType ) );


        // Invalid

        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( "[root]", type ) );
        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( "[root]", threeCharsType ) );

        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( "[root   ]", type ) );
        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( "[root   ]", threeCharsType ) );

        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( "[root \"\"]", type ) );
        assertThrows ( PdmlException.class, () ->
            TextTypeTest.parse ( "[root \"\"]", threeCharsType ) );
    }
}
