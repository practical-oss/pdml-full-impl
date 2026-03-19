package dev.ps.pdml.utils.treewalker;

import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.utils.treewalker.handler.PdmlTreeWalkerEventHandler;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class PdmlCodeWalkerUtil {

    /*
    public static <N,R> R walkReader (
        @NotNull Reader reader,
        @Nullable ReaderResource readerResource,
        @Nullable Integer currentLineNumber,
        @Nullable Integer currentColumnLineNumber,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlTreeWalkerEventHandler<N,R> eventHandler ) throws IOException, PdmlException {

        PdmlCodeWalker<N, R> walker = new PdmlCodeWalker<> (
            reader, readerResource, currentLineNumber, currentColumnLineNumber, parserConfig, eventHandler );
        walker.walk();
        return eventHandler.getResult();
    }
     */

    public static <N,R> R walkReaderResource (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlTreeWalkerEventHandler<N,R> eventHandler ) throws IOException, PdmlException {

        try ( Reader reader = readerResource.newReader() ) {
            PdmlCodeWalker<N, R> walker = new PdmlCodeWalker<> (
                reader, readerResource, parserConfig, eventHandler );
            walker.walk();
            return eventHandler.getResult();
        }
    }

    public static <N,R> R walkReader (
        @NotNull ReaderResource readerResource,
        @NotNull PdmlTreeWalkerEventHandler<N,R> eventHandler ) throws IOException, PdmlException {

        return walkReaderResource ( readerResource, PdmlParserConfig.defaultConfig(), eventHandler );

    }

    public static <N,R> R walkCode (
        @NotNull String code,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlTreeWalkerEventHandler<N,R> eventHandler ) throws IOException, PdmlException {

        return walkReaderResource ( new StringReaderResource ( code ), parserConfig, eventHandler );
    }

    public static <N,R> R walkCode (
        @NotNull String code,
        @NotNull PdmlTreeWalkerEventHandler<N,R> eventHandler ) throws IOException, PdmlException {

        return walkReader ( new StringReaderResource ( code ), eventHandler );
    }

    public static <N,R> R walkFile (
        @NotNull Path filePath,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlTreeWalkerEventHandler<N,R> eventHandler ) throws IOException, PdmlException {

        return walkReaderResource ( new FileReaderResource ( filePath ), parserConfig, eventHandler );
    }

    public static <N,R> R walkFile (
        @NotNull Path filePath,
        @NotNull PdmlTreeWalkerEventHandler<N,R> eventHandler ) throws IOException, PdmlException {

        return walkReader ( new FileReaderResource ( filePath ), eventHandler );
    }
}
