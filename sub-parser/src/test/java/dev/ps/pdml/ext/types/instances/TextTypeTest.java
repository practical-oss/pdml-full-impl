package dev.ps.pdml.ext.types.instances;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.nodespec.PdmlNodeSpec;
import dev.ps.pdml.data.nodespec.PdmlNodeSpecs;
import dev.ps.pdml.ext.types.PdmlType;
import dev.ps.pdml.ext.types.PdmlTypes;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.PdmlParserConfigBuilder;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TextTypeTest {

    protected static @NotNull TaggedNode parse (
        @NotNull String code,
        @NotNull PdmlType<?> type ) throws IOException, PdmlException {

        PdmlNodeSpec nodeSpec = new PdmlNodeSpec (
            new NodeTag ( "root" ),
            type.getName(), null, null );
        PdmlParserConfig config = new PdmlParserConfigBuilder()
            .types ( new PdmlTypes().add ( type ) )
            .nodeSpecs ( new PdmlNodeSpecs().add ( nodeSpec ) )
            .build();
        return ParseASTUtil.parseString ( code, config );
    }

    @Test
    void test() throws Exception {

        PdmlType<?> textType = TextType.NON_NULL_INSTANCE;
        PdmlType<?> textOrNullType = TextType.NULLABLE_INSTANCE;

        TaggedNode rootNode = parse ( "[root text]", textType );
        assertEquals ( 1, rootNode.getChildNodes().size() );
        assertEquals ( "text", rootNode.toText() );
        String text = rootNode.getCastedJavaObjectContained();
        assertNotNull ( text );
        assertEquals ( "text", text );

        rootNode = parse ( "[untyped_root ^[text text2]]", textType );
        assertEquals ( "text2", rootNode.toText() );

        rootNode = parse ( "[root ^[text text3]]", textType );
        assertEquals ( "text3", rootNode.toText() );

        rootNode = parse ( "[root text \\[\\] text \n]", textType );
        assertEquals ( "text [] text \n", rootNode.toText() );

        rootNode = parse ( "[root  ]", textType );
        assertEquals ( " ", rootNode.toText() );

        assertThrows ( PdmlException.class,
            () -> parse ( "[root]", textType ) );

        rootNode = parse ( "[root]", textOrNullType );
        assertNull ( rootNode.toTextOrNull() );

        assertThrows ( PdmlException.class,
            () -> parse ( "[root text [child] ]", textType ) );
    }
}
