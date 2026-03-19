package dev.ps.pdml.core.parser;

import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class CorePdmlParserUtil {


    // ReaderResource

    public static @NotNull TaggedNode parseResource (
        @NotNull ReaderResource resource,
        @NotNull CorePdmlParserConfig config ) throws IOException, PdmlException {

        try ( Reader reader = resource.newReader() ) {
            CorePdmlParser_OLD parser = new CorePdmlParser_OLD (
                reader, resource, config );
            return parser.requireRootNode();
        }
    }

    public static @NotNull TaggedNode parseResource (
        @NotNull ReaderResource resource ) throws IOException, PdmlException {

        return parseResource ( resource, new CorePdmlParserConfig() );
    }


    // String

    public static @NotNull TaggedNode parseString (
        @NotNull String string,
        @NotNull CorePdmlParserConfig config ) throws IOException, PdmlException {

        return parseResource ( new StringReaderResource ( string ), config );
    }

    public static @NotNull TaggedNode parseString (
        @NotNull String string ) throws IOException, PdmlException {

        return parseResource ( new StringReaderResource ( string ) );
    }


    // File

    public static @NotNull TaggedNode parseFile (
        @NotNull Path filePath,
        @NotNull CorePdmlParserConfig config ) throws IOException, PdmlException {

        return parseResource ( new FileReaderResource ( filePath ), config );
    }

    public static @NotNull TaggedNode parseFile (
        @NotNull Path filePath ) throws IOException, PdmlException {

        return parseResource ( new FileReaderResource ( filePath ) );
    }
}
