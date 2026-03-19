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
import java.io.Writer;
import java.util.stream.Stream;

public class TextLeavesWriterUtil {

    public static void writeTexts (
        @NotNull TextResourceReader pdmlCodeReader,
        @NotNull PdmlParserConfig parserConfig,
        // TODO? boolean removeWhitespaceNodes,
        @NotNull TextResourceWriter writer,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException, PdmlException {

        @NotNull TaggedNode rootNode = ParseASTUtil.parseReader (
            pdmlCodeReader, parserConfig );
        writeTexts ( rootNode, writer, separator, sort, distinct );
    }

    public static void writeTexts (
        @NotNull TaggedNode rootNode,
        @NotNull TextResourceWriter writer,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException {

        writeTextsOrNames ( false, rootNode, writer, separator, sort, distinct );
    }

    static void writeTextsOrNames (
        boolean writeNames,
        @NotNull TaggedNode rootNode,
        @NotNull TextResourceWriter writer,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException {

        Writer writer_ = writer.getWriter();

        Stream<String> stringStream = writeNames ?
            rootNode.treeQualifiedTagStream ( true ) :
            rootNode.treeTextStream();

        if ( distinct ) {
            stringStream = stringStream.distinct();
        }

        if ( sort ) {
            stringStream = stringStream.sorted();
        }

        boolean isFirst = true;
        for ( String string : stringStream.toList() ) {

            if ( ! isFirst && separator != null ) {
                writer_.write ( separator );
            }

            writer_.write ( string );

            isFirst = false;
        }
    }
}
