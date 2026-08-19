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
import java.io.Writer;
import java.util.stream.Stream;

public class TextLeavesWriterUtil {

    public static void writeTexts (
        @NotNull ReaderResource pdmlReaderResource,
        @NotNull PdmlParserConfig parserConfig,
        // TODO? boolean skipWhitespaceNodes,
        @NotNull WriterResource writerResource,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException, PdmlException {

        @NotNull TaggedNode rootNode = ParseASTUtil.parseReaderResource (
            pdmlReaderResource, parserConfig );
        writeTexts ( rootNode, writerResource, separator, sort, distinct );
    }

    public static void writeTexts (
        @NotNull TaggedNode rootNode,
        @NotNull WriterResource writerResource,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException {

        writeTextsOrNames ( false, rootNode, writerResource, separator, sort, distinct );
    }

    static void writeTextsOrNames (
        boolean writeNames,
        @NotNull TaggedNode rootNode,
        @NotNull WriterResource writerResource,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException {

        try ( Writer writer = writerResource.newWriter() ) {
            writeTextsOrNames ( writeNames, rootNode, writer, separator, sort, distinct );
        }
    }

    static void writeTextsOrNames (
        boolean writeNames,
        @NotNull TaggedNode rootNode,
        @NotNull Writer writer,
        @Nullable String separator,
        boolean sort,
        boolean distinct ) throws IOException {

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
                writer.write ( separator );
            }

            writer.write ( string );

            isFirst = false;
        }

        writer.flush();
    }
}
