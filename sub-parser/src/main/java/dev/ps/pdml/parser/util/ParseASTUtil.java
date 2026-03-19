package dev.ps.pdml.parser.util;

import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class ParseASTUtil {


    // Reader

    private static @NotNull TaggedNode parseReader (
        @NotNull Reader reader,
        // @Nullable ReaderResource readerResource,
        @NotNull ReaderResource readerResource,
        // @Nullable Integer currentLineNumber,
        // @Nullable Integer currentColumnLineNumber,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        PdmlParser parser = PdmlParser.create ( reader, readerResource, config );
        return parser.requireDocument ();
    }

    /*
    public static @NotNull TaggedNode parseReader (
        @NotNull Reader reader,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        // return parseReader ( reader, null, null, null, config );
        PdmlParser parser = PdmlParser.create ( reader, null, config );
        return parser.requireDocument ();
    }
     */

    /*
    public static @NotNull TaggedNode parseReader (
        @NotNull Reader reader ) throws IOException, PdmlException {

        return parseReader ( reader, PdmlParserConfig.defaultConfig() );
    }
     */


    // ReaderResource

    public static @NotNull TaggedNode parseReaderResource (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        /*
        try ( Reader reader = readerResource.newReader() ) {
            return parseReader ( reader, readerResource, null, null, config );
        }
         */
        PdmlParser parser = PdmlParser.create ( readerResource, config );
        return parser.requireDocument ();
    }

    public static @NotNull TaggedNode parseReaderResource (
        @NotNull ReaderResource readerResource ) throws IOException, PdmlException {

        return parseReaderResource ( readerResource, PdmlParserConfig.defaultConfig() );
    }


    // String

    public static @NotNull TaggedNode parseString (
        @NotNull String string,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        return parseReaderResource ( new StringReaderResource ( string ), config );
    }

    public static @NotNull TaggedNode parseString (
        @NotNull String string ) throws IOException, PdmlException {

        return parseReaderResource ( new StringReaderResource ( string ) );
    }


    // File

    public static @NotNull TaggedNode parseFile (
        @NotNull Path filePath,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        return parseReaderResource ( new FileReaderResource ( filePath ), config );
    }

    public static @NotNull TaggedNode parseFile (
        @NotNull Path filePath ) throws IOException, PdmlException {

        return parseReaderResource ( new FileReaderResource ( filePath ) );
    }


    // TextResourceReader

    @Deprecated
    public static @NotNull TaggedNode parseReader (
        @NotNull TextResourceReader textResourceReader,
        @NotNull PdmlParserConfig config ) throws IOException, PdmlException {

        return parseReader (
            textResourceReader.getReader(),
            (ReaderResource) textResourceReader.getTextResource(),
            config );
    }
}
