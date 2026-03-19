package dev.ps.pdml.writer.node;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.text.writer.LineBreakKind;
import dev.ps.prt.argument.StringArguments;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PdmlNodeWriterTest {

    @Test
    void test() {

        // PdmlNodeWriterConfig config = PdmlNodeWriterConfig.DEFAULT_CONFIG;
        PdmlNodeWriterConfig config = new PdmlNodeWriterConfig (
            4, false, LineBreakKind.UNIX, true );

        TaggedNode rootNode = new TaggedNode ( "root" );
        String result = PdmlNodeWriterUtil.writeToString ( rootNode, false, config );
        assertEquals ( "[root]", result );
        result = PdmlNodeWriterUtil.writeToString ( rootNode, true, config );
        assertEquals ( "[root]\n", result );

/*
        rootNode
            .addAttribute ( "a1", "v 1" )
            .addAttribute ( "a2", null )
            .addAttribute ( "a3", "v3" );
 */
        StringArguments attributes = StringArguments.builder()
            .append ( "a1", "v 1" )
            .append ( "a2", null )
            .append ( "a3", "v3" )
            .build();
        rootNode.setStringAttributes ( attributes );
        result = PdmlNodeWriterUtil.writeToString ( rootNode, false, config );
        assertEquals ( """
            [root ^(a1="v 1" a2="" a3=v3)]""", result );
        result = PdmlNodeWriterUtil.writeToString ( rootNode, true, config );
        assertEquals ( """
            [root ^(a1="v 1" a2="" a3=v3)\n]\n""", result );

        TaggedNode childNode = new TaggedNode ( "ch1" );
        childNode.appendTextChild ( "ch11", " a b c " );
        rootNode = new TaggedNode ( "root" );
        rootNode
            .appendChild ( childNode )
            .appendTextChild ( "ch2","bar" );
        result = PdmlNodeWriterUtil.writeToString ( rootNode, false, config );
        assertEquals ( "[root [ch1 [ch11  a b c ]][ch2 bar]]", result );
        result = PdmlNodeWriterUtil.writeToString ( rootNode, true, config );
        assertEquals ( """
            [root
                [ch1
                    [ch11  a b c ]
                ]
                [ch2 bar]
            ]
            """, result );

        rootNode = new TaggedNode ( "root" );
        rootNode.appendText ( "foo bar" )
            .appendText ( " []\\" );
        result = PdmlNodeWriterUtil.writeToString ( rootNode, false, config );
        assertEquals ( "[root foo bar \\[\\]\\\\]", result );
        result = PdmlNodeWriterUtil.writeToString ( rootNode, true, config );
        assertEquals ( """
            [root
                foo bar
                 \\[\\]\\\\
            ]
            """, result );
    }

/*
    @Test
    void writeBranchNodeLines() throws IOException {

        PdmlNodeWriterConfig config = new PdmlNodeWriterConfig ( true, PdmlWriterConfig.DEFAULT_CONFIG );

        BranchNode rootNode = new BranchNode ( "root" );
        String result = PdmlNodeWriterUtil.writeToString ( rootNode, false, config );
        assertEquals ( "[root]", result );

        rootNode
            .addAttribute ( "a1", "v 1" )
            .addAttribute ( "a2", null )
            .addAttribute ( "a3", "v3" );
        result = PdmlNodeWriterUtil.writeToString ( rootNode, false, config );
    }
 */
}
