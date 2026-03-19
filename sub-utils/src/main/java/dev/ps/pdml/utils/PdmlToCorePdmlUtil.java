package dev.ps.pdml.utils;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.pdml.writer.node.PdmlNodeWriter;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;

public class PdmlToCorePdmlUtil {

    /* not used
    public static void pdmlFileToToCorePdmlFile (
        @NotNull Path pdmlInputFile,
        @NotNull Path pdmlOutputFile,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlNodeWriterConfig writerConfig ) throws Exception {

        try ( TextResourceReader pdmlReader = new TextResourceReader ( pdmlInputFile );
          TextResourceWriter pdmlWriter = new TextResourceWriter ( pdmlOutputFile, true ) ) {

            pdmlCodeToCorePdml ( pdmlReader, pdmlWriter, parserConfig, writerConfig );
        }
    }
     */

    public static void pdmlCodeToCorePdml (
        @NotNull TextResourceReader pdmlCodeReader,
        @NotNull TextResourceWriter corePdmlCodeWriter,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlNodeWriterConfig writerConfig,
        boolean keepAttributes ) throws Exception {

        @NotNull TaggedNode rootNode = ParseASTUtil.parseReader (
            pdmlCodeReader, parserConfig );
        treeToCorePdml ( rootNode, corePdmlCodeWriter, writerConfig, keepAttributes );
    }

    private static void treeToCorePdml (
        @NotNull TaggedNode rootNode,
        @NotNull TextResourceWriter corePdmlCodeWriter,
        @NotNull PdmlNodeWriterConfig writerConfig,
        boolean keepAttributes ) throws Exception {

        rootNode.replaceAttributesWithTextNodes();

        if ( ! keepAttributes ) {

            // generates ConcurrentModificationException
            // rootNode.treeBranchNodeStream ( true ).forEach ( BranchNode::replaceAttributesWithTextNodes );

            for ( TaggedNode child : rootNode.treeTaggedNodeStream ( true ).toList() ) {
                child.replaceAttributesWithTextNodes();
            }
        }

        PdmlNodeWriter nodeWriter = new PdmlNodeWriter (
            corePdmlCodeWriter.getWriter(), writerConfig );
        nodeWriter.writeRootNode ( rootNode );
    }
}
