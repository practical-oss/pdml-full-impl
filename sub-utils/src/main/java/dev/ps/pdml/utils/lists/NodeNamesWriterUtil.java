package dev.ps.pdml.utils.lists;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;

import java.io.IOException;

public class NodeNamesWriterUtil {

    public static void writeNames (
        @NotNull ReaderResource pdmlReaderResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull WriterResource textWriterResource,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException, PdmlException {

        @NotNull TaggedNode rootNode = ParseASTUtil.parseReaderResource (
            pdmlReaderResource, parserConfig );
        writeNames ( rootNode, textWriterResource, separator, sort, distinct );
    }

    public static void writeNames (
        @NotNull TaggedNode rootNode,
        @NotNull WriterResource writerResource,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException {

        TextLeavesWriterUtil.writeTextsOrNames ( true, rootNode, writerResource, separator, sort, distinct );
    }
}
