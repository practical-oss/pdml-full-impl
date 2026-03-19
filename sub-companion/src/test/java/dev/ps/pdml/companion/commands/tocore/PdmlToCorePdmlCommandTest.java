package dev.ps.pdml.companion.commands.tocore;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pdml.companion.commands.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

class PdmlToCorePdmlCommandTest {

    @TempDir Path tempDir;

    @Test
    void execute() throws IOException  {

        String input = "[root]";
        String expectedOutput = "[root]";
        execute_ ( input, expectedOutput );

        input = """
            [root
            ^// comment
            ^[const c1 = foo]
                ^[ins_const c1]
                1 + 1 = ^[ins_exp 1 + 1]
            ]""";
        expectedOutput = """
            [root

                foo
                1 + 1 = 2
            ]""";
        execute_ ( input, expectedOutput );
    }

    void execute_ (
        @NotNull String input,
        @NotNull String expectedOutput ) throws IOException  {

        TestHelper.testCLICommandWithInputOutputFile (
            "PDML_to_Core_PDML", tempDir,
            input, expectedOutput );
    }
}
