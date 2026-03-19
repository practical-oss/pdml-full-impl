package dev.ps.pdml.html.treeview;

import dev.ps.pdml.data.util.TestDoc;
import dev.ps.shared.basics.utilities.file.TempFileUtils;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class TreeViewUtilTest {

    @Test
    void testShowTree() throws Exception {

        String pdmlTestDoc = TestDoc.getPdmlTestDoc();
        TextResourceReader reader = new TextResourceReader ( pdmlTestDoc );

        // delete on exit
        Path htmlFile = TempFileUtils.createEmptyTempFile ( "test", "html", false );

        // TreeViewUtil.showCode ( reader, PdmlParserConfig.defaultConfig(), true, htmlFile );
    }
}
