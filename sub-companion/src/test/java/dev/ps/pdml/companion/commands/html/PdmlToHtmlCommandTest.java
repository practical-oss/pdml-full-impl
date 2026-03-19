package dev.ps.pdml.companion.commands.html;

import dev.ps.pdml.companion.commands.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class PdmlToHtmlCommandTest {

    @TempDir Path tempDir;

    @Test
    void execute() throws IOException {

        TestHelper.testCLICommandWithInputOutputFile (
            "PDML_to_HTML", tempDir,
            "input.pdml", "out/result.html",
            "[root text]", "<html lang=\"en\">",
            List.of ( "--open_browser", "false" ), true );
    }
}
