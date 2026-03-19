package dev.ps.pdml.companion.commands.list;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pdml.companion.commands.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class ListTextsCommandTest {

    @TempDir Path tempDir;

    @Test
    void execute() throws IOException {

        String input = "[root text]";
        String expectedOutput = "text";
        execute_ ( input, expectedOutput );

        input = "[root root text[child_1 child_1 text]between \\[child_1\\] and \\[child_2\\][child_2 child_2 text]]";
        expectedOutput = """
            root text
            child_1 text
            between [child_1] and [child_2]
            child_2 text""";
        execute_ ( input, expectedOutput );

        input = "[root foo[child_1 bar]foo[child_2 bar]]";
        expectedOutput = "bar, foo";
        TestHelper.testCLICommandWithInputOutputFile (
            "list_texts",
            tempDir, "in.pdml", "out.pdml",
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
            "list_texts", tempDir,
            input, expectedOutput );
    }
}
