package dev.ps.pdml.utils.parser;

/*
import dev.pp.pdml.data.exception.PdmlException;
import dev.pp.pdml.data.node.tagged.TaggedNode;
import dev.pp.pdml.parser.PdmlParser;
import dev.pp.pdml.parser.PdmlParserConfig;
import dev.pp.pdml.parser.PdmlParserConfigBuilder;
import dev.pp.core.basics.annotations.NotNull;
import dev.pp.core.basics.annotations.Nullable;
import dev.pp.core.parameters.parameters.Parameters;
import dev.pp.core.text.reader.CharReader;
import dev.pp.core.text.reader.CharReaderImpl;
import dev.pp.core.text.ioresource.reader.FileReaderResource;
import dev.pp.core.text.ioresource.reader.StringReaderResource;
import dev.pp.core.text.ioresource.IOResource;
import dev.pp.core.text.utilities.file.TextFileReaderUtil;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
 */

@Deprecated
public class StringParametersUtil{}
/*
public class StringParametersUtil {

    public static @Nullable Parameters<String> parse (
        @NotNull CharReader charReader,
        boolean hasRootNode,
        boolean allowNullValues,
        @NotNull PdmlParserConfig parserConfig ) throws IOException, PdmlException {

        PdmlParser parser = PdmlParser.create ( charReader, parserConfig );
        TaggedNode rootNode;
        if ( hasRootNode ) {
            rootNode = parser.requireRootNode();
        } else {
            rootNode = new TaggedNode ( "root" );
            parser.parseChildNodes ( rootNode );
        }
        return rootNode.toStringParametersOrNull ( allowNullValues );
    }

    public static @Nullable Parameters<String> parseReader (
        @NotNull Reader reader,
        @Nullable IOResource readerResource,
        @Nullable Integer currentLineNumber,
        @Nullable Integer currentColumnLineNumber,
        boolean hasRootNode,
        boolean allowNullValues,
        @NotNull PdmlParserConfig parserConfig ) throws IOException, PdmlException {

        return parse (
            CharReaderImpl.createAndAdvance ( reader, readerResource, currentLineNumber, currentColumnLineNumber ),
            hasRootNode, allowNullValues, parserConfig );
    }

    public static @Nullable Parameters<String> parseReader (
        @NotNull Reader reader,
        @Nullable IOResource readerResource,
        boolean hasRootNode ) throws IOException, PdmlException {

        return parseReader ( reader, readerResource, null, null,
            hasRootNode, true, PdmlParserConfigBuilder.createDefault() );
    }

    public static @Nullable Parameters<String> parseString (
        @NotNull String string,
        @Nullable IOResource readerResource,
        @Nullable Integer currentLineNumber,
        @Nullable Integer currentColumnLineNumber,
        boolean hasRootNode,
        boolean allowNullValues,
        @NotNull PdmlParserConfig parserConfig ) throws IOException, PdmlException {

        try ( StringReader reader = new StringReader ( string ) ) {
            return parseReader ( reader, readerResource, currentLineNumber, currentColumnLineNumber,
                hasRootNode, allowNullValues, parserConfig );
        }
    }

    public static @Nullable Parameters<String> parseString (
        @NotNull String string,
        boolean hasRootNode ) throws IOException, PdmlException {

        try ( StringReader reader = new StringReader ( string ) ) {
            return parseReader ( reader, new StringReaderResource ( string ), hasRootNode );
        }
    }

    public static @Nullable Parameters<String> parseFile (
        @NotNull Path filePath,
        boolean hasRootNode,
        boolean allowNullValues,
        @NotNull PdmlParserConfig parserConfig ) throws IOException, PdmlException {

        try ( Reader reader = TextFileReaderUtil.createUTF8FileReader ( filePath ) ) {
            return parseReader (
                reader, new FileReaderResource ( filePath ), null, null,
                hasRootNode, allowNullValues, parserConfig );
        }
    }

    public static @Nullable Parameters<String> parseFile (
        @NotNull Path filePath,
        boolean hasRootNode ) throws IOException, PdmlException {

        try ( Reader reader = TextFileReaderUtil.createUTF8FileReader ( filePath ) ) {
            return parseReader ( reader, new FileReaderResource ( filePath ), hasRootNode );
        }
    }
}
 */
