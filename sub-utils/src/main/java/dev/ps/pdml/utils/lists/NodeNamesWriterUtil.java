package dev.ps.pdml.utils.lists;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;

import java.io.IOException;

public class NodeNamesWriterUtil {

    public static void writeNames (
        @NotNull TextResourceReader pdmlCodeReader,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull TextResourceWriter writer,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException, PdmlException {

        @NotNull TaggedNode rootNode = ParseASTUtil.parseReader (
            pdmlCodeReader, parserConfig );
        writeNames ( rootNode, writer, separator, sort, distinct );
    }

    public static void writeNames (
        @NotNull TaggedNode rootNode,
        @NotNull TextResourceWriter writer,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException {

        TextLeavesWriterUtil.writeTextsOrNames ( true, rootNode, writer, separator, sort, distinct );
    }
}
