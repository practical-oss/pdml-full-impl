package dev.ps.pdml.html.treeview;

import dev.ps.pdml.data.util.DemoDocs;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.shared.basics.utilities.file.TempFileUtils;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.ioresource.writer.FileWriterResource;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PdmlToHtmlTreeViewUtilTest {

    @Test
    void pdmlReaderToHtmlWriterResource() throws Exception {

        String corePdmlDemo = DemoDocs.corePdmlDemoDoc();
        Path htmlFilePath = TempFileUtils.createEmptyTempFile ( "test", "html", true );
        assertDoesNotThrow  ( () -> PdmlToHtmlTreeViewUtil.pdmlReaderToHtmlWriterResource (
            new StringReaderResource ( corePdmlDemo ),
            new FileWriterResource ( htmlFilePath, false ),
            PdmlParserConfig.defaultConfig(), true ) );
    }
}
