package dev.ps.pdml.html.treeview;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.json.PdmlToJsonUtil;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.ResourcesUtils;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;

import java.io.IOException;
import java.nio.file.Path;

public class PdmlToHtmlTreeViewUtil {

    public static void readerToWriter (
        @NotNull TextResourceReader pdmlCodeReader,
        @NotNull TextResourceWriter htmlCodeWriter,
        @NotNull PdmlParserConfig parserConfig,
        boolean removeWhitespaceNodes ) throws IOException, PdmlException {

        @NotNull TaggedNode pdmlRootNode = ParseASTUtil.parseReader (
            pdmlCodeReader, parserConfig );

        if ( removeWhitespaceNodes ) {
            pdmlRootNode.removeWhitespaceTextLeafsInTree ( false );
        }

        treeToWriter ( pdmlRootNode, htmlCodeWriter );
    }

    public static void treeToWriter (
        @NotNull TaggedNode pdmlRootNode,
        @NotNull TextResourceWriter htmlCodeWriter ) throws IOException {

        String jsonString = PdmlToJsonUtil.treeToCode ( pdmlRootNode, true );

        // The occurrence of "</script>" in the JSON code would be interpreted by the HTML browser
        // as the end of the script tag, and lead to nasty errors. Therefore "</script>" must be escaped.
        jsonString = jsonString.replace ( "</script>", "<\\/script>" );

        String htmlTemplate = ResourcesUtils.readTextResource (
            Path.of ( "dev/ps/pdml/html/treeview/PDML_tree_view_template.html" ),
            PdmlToHtmlTreeViewUtil.class );
        String htmlCode = htmlTemplate.replace ( "{{pdmlTreeData}}", jsonString );
        htmlCodeWriter.getWriter().write ( htmlCode );

        // showHtmlFileInDefaultBrowser ( htmlOutputFile );
    }

    /*
    private static void showHtmlFileInDefaultBrowser (
        @NotNull Path htmlFilePath ) throws IOException {

        DesktopUtil.openInDefaultBrowser ( htmlFilePath );
    }
     */
}
