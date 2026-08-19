package dev.ps.pdml.utils;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.pdml.writer.node.PdmlNodeWriter;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;

import java.io.Writer;

public class FullPdmlToCorePdmlUtil {

    public static void fullPdmlToCorePdml (
        @NotNull ReaderResource fullPdmlReaderResource,
        @NotNull WriterResource corePdmlWriterResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlNodeWriterConfig writerConfig,
        boolean keepAttributes ) throws Exception {

        @NotNull TaggedNode rootNode = ParseASTUtil.parseReaderResource (
            fullPdmlReaderResource, parserConfig );
        treeToCorePdml ( rootNode, corePdmlWriterResource, writerConfig, keepAttributes );
    }

    private static void treeToCorePdml (
        @NotNull TaggedNode rootNode,
        @NotNull WriterResource corePdmlCodeWriter,
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

        try ( Writer writer = corePdmlCodeWriter.newWriter() ) {
            PdmlNodeWriter nodeWriter = new PdmlNodeWriter (
                writer, writerConfig );
            nodeWriter.writeRootNode ( rootNode );
            writer.flush();
        }
    }
}
