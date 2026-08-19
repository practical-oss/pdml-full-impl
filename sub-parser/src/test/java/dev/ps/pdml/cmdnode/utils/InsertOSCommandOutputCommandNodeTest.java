package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.os.OSName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InsertOSCommandOutputCommandNodeTest {

    @Test
    void handleNode() throws IOException, PdmlException {

        // TODO not yet tested on Linux

        boolean isWindowsOS = OSName.isWindowsOS();
        String pdmlCode = isWindowsOS
            ? "[root ^[ins-cmdo [cmd cmd.exe, /c, sort] [stdin c\r\nb\r\na\r\n]]]"
            : "[root ^[ins-cmdo [cmd sort] [stdin c\nb\na]]]";
        @NotNull TaggedNode rootNode = ParseASTUtil.parseString ( pdmlCode );
        String expected = isWindowsOS
            ? "a\r\nb\r\nc\r\n"
            : "a\nb\nc\n";
        assertEquals ( expected, rootNode.toText() );

        if ( isWindowsOS ) {
            // Use & for several commands
            rootNode = ParseASTUtil.parseString ( """
                [root ^[ins-cmdo [cmd cmd.exe, /c, "echo Hi & echo my friend"]]]""" );
            assertEquals ( "Hi \r\nmy friend\r\n", rootNode.toText() );

            // Use | for pipe
            rootNode = ParseASTUtil.parseString ( """
                [root ^[ins-cmdo [cmd cmd.exe, /c, "echo abc| sort"]]]""" );
            assertEquals ( "abc\r\n", rootNode.toText() );

            // Arithmetic expression
            rootNode = ParseASTUtil.parseString ( """
                [root ^[ins-cmdo [cmd cmd.exe, /c, "set /a 1+1"]]]""" );
            assertEquals ( "2", rootNode.toText() );
        }
    }
}
