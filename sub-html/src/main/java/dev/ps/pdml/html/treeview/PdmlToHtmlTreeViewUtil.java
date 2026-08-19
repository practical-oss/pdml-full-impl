package dev.ps.pdml.html.treeview;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.json.PdmlToJsonUtil;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.JavaResourceUtil;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public class PdmlToHtmlTreeViewUtil {

    public static void pdmlReaderToHtmlWriterResource (
        @NotNull ReaderResource pdmlReaderResource,
        @NotNull WriterResource htmlWriterResource,
        @NotNull PdmlParserConfig parserConfig,
        boolean removePrettyPrintingWhitespaceInTree ) throws IOException, PdmlException {

        @NotNull TaggedNode pdmlRootNode = ParseASTUtil.parseReaderResource (
            pdmlReaderResource, parserConfig );

        if ( removePrettyPrintingWhitespaceInTree ) {
            pdmlRootNode.removePrettyPrintingWhitespaceInTree();;
        }

        pdmlTreeToHtmlWriterResource ( pdmlRootNode, htmlWriterResource );
    }

    private static void pdmlTreeToHtmlWriterResource (
        @NotNull TaggedNode pdmlRootNode,
        @NotNull WriterResource htmlWriterResource ) throws IOException {

        try ( Writer writer = htmlWriterResource.newWriter() ) {
            pdmlTreeToHtmlWriter ( pdmlRootNode, writer );
        }
    }

    private static void pdmlTreeToHtmlWriter (
        @NotNull TaggedNode pdmlRootNode,
        @NotNull Writer htmlWriter ) throws IOException {

        String jsonString = PdmlToJsonUtil.pdmlTreeToJsonCode ( pdmlRootNode, true );

        // The occurrence of "</script>" in the JSON code would be interpreted by the HTML browser
        // as the end of the script tag, and lead to nasty errors. Therefore "</script>" must be escaped.
        jsonString = jsonString.replace ( "</script>", "<\\/script>" );

        String htmlTemplate = JavaResourceUtil.readUTF8TextResource (
            Path.of ( "dev/ps/pdml/html/treeview/PDML_tree_view_template.html" ),
            PdmlToHtmlTreeViewUtil.class );
        String htmlCode = htmlTemplate.replace ( "{{pdmlTreeData}}", jsonString );
        htmlWriter.write ( htmlCode );
        htmlWriter.flush();

        // showHtmlFileInDefaultBrowser ( htmlOutputFile );
    }

    /*
    private static void showHtmlFileInDefaultBrowser (
        @NotNull Path htmlFilePath ) throws IOException {

        DesktopUtil.openInDefaultBrowser ( htmlFilePath );
    }
     */
}
