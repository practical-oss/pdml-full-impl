package dev.ps.pdml.companion.commands.list;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pdml.companion.commands.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class ListNodeNamesCommandTest {

    @TempDir Path tempDir;

    @Test
    void execute() throws IOException {

        String input = "[root text]";
        String expectedOutput = "root";
        execute_ ( input, expectedOutput );

        input = """
            [root root text
                [child_1 child_1 text
                    [child_1_1 child_1_1 text]
                    [child_1_2]
                ]

                [child_2 child_2 text]
            ]""";
        expectedOutput = """
            root
            child_1
            child_1_1
            child_1_2
            child_2""";
        execute_ ( input, expectedOutput );

        input = """
            [root root text
                [foo child_1 text
                    [bar child_1_1 text]
                    [foo]
                ]

                [bar child_2 text]
            ]""";
        expectedOutput = "bar, foo, root";
        TestHelper.testCLICommandWithInputOutputFile (
            "list_node_names",
            tempDir, "in/in.pdml", "out/out.pdml",
            input, expectedOutput,
            List.of (
                "--sort", "true",
                "--distinct", "true",
                "--separator", ", "),
            false );
    }

    void execute_ (
        @NotNull String input,
        @NotNull String expectedOutput ) throws IOException  {

        TestHelper.testCLICommandWithInputOutputFile (
            "ln", tempDir, input, expectedOutput );
    }
}
