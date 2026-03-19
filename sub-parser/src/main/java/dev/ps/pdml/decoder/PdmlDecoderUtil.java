package dev.ps.pdml.decoder;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.prt.type.decoder.DecoderUtil;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.AnyType;
import dev.ps.pdml.parser.PdmlParserConfig;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class PdmlDecoderUtil {


    public static final DecoderUtil.@NotNull DecoderSupplier DEFAULT_DECODER_SUPPLIER =
        ( reader, readerResource ) -> {
            try {
                PdmlParser parser = PdmlParser.create (
                    reader, readerResource, PdmlParserConfig.defaultConfig () );
                return new PdmlDecoder ( parser );
            } catch ( IOException e ) {
                throw new RuntimeException ( e );
            }
        };


    // Reader

    public static <N> @Nullable N decode (
        @NotNull AnyType<N> type,
        @NotNull Reader reader,
        @NotNull ReaderResource readerResource )
            throws IOException, InvalidDataException {

        /*
        PdmlParser parser = PdmlParser.create (
            reader, readerResource, currentLineNumber, currentColumnNumber, PdmlParserConfig.defaultConfig() );
        PdmlDecoder decoder = new PdmlDecoder ( parser );
        return type.decodeToNative ( decoder );
         */

        return decodeToInstance ( type, reader, readerResource ).nativeObject ();
    }

    public static <N> @NotNull AnyInstance<N> decodeToInstance (
        @NotNull AnyType<N> type,
        @NotNull Reader reader,
        @NotNull ReaderResource readerResource )
            throws IOException, InvalidDataException {

        PdmlParser parser = PdmlParser.create (
            reader, readerResource, PdmlParserConfig.defaultConfig() );
        PdmlDecoder decoder = new PdmlDecoder ( parser );
        return type.decodeToInstance ( decoder );
    }


    // ReaderResource

    public static <N> @Nullable N decodeReaderResource (
        @NotNull AnyType<N> type,
        @NotNull ReaderResource readerResource ) throws IOException, InvalidDataException {

        /*
        try ( Reader reader = readerResource.newReader() ) {
            return decode ( type, reader, readerResource );
        }
         */
        return decodeReaderResourceToInstance ( type, readerResource ).nativeObject ();
    }

    public static <N> @NotNull AnyInstance<N> decodeReaderResourceToInstance (
        @NotNull AnyType<N> type,
        @NotNull ReaderResource readerResource ) throws IOException, InvalidDataException {

        /*
        try ( Reader reader = readerResource.newReader() ) {
            return decodeToInstance ( type, reader, readerResource );
        }
         */
        PdmlParser parser = PdmlParser.create (
            readerResource, PdmlParserConfig.defaultConfig() );
        PdmlDecoder decoder = new PdmlDecoder ( parser );
        return type.decodeToInstance ( decoder );
    }


    // String

    public static <N> @Nullable N decodeString (
        @NotNull AnyType<N> type,
        @NotNull String string ) throws IOException, InvalidDataException {

        return decodeReaderResource ( type, new StringReaderResource ( string ) );
    }

    public static <N> @NotNull AnyInstance<N> decodeStringToInstance (
        @NotNull AnyType<N> type,
        @NotNull String string ) throws IOException, InvalidDataException {

        return decodeReaderResourceToInstance ( type, new StringReaderResource ( string ) );
    }


    // File

    public static <N> @Nullable N decodeFile (
        @NotNull AnyType<N> type,
        @NotNull Path filePath ) throws IOException, InvalidDataException {

        return decodeReaderResource ( type, new FileReaderResource ( filePath ) );
    }

    public static <N> @NotNull AnyInstance<N> decodeFileToInstance (
        @NotNull AnyType<N> type,
        @NotNull Path filePath ) throws IOException, InvalidDataException {

        return decodeReaderResourceToInstance ( type, new FileReaderResource ( filePath ) );
    }
}
