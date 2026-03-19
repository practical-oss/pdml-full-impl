package dev.ps.pdml.encoder;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.writer.FileWriterResource;
import dev.ps.shared.text.ioresource.writer.WriterResource;
import dev.ps.prt.type.AnyInstance;
import dev.ps.prt.type.AnyType;
import dev.ps.pdml.core.writer.PdmlWriterConfig;
import dev.ps.pdml.writer.PdmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;

public class PdmlEncoderUtil {

    // Writer

    public static <N> void encode (
        @NotNull Writer writer,
        @NotNull N nativeObject,
        AnyType<N> type ) throws IOException {

        encode ( writer, type.createInstance ( nativeObject, null ) );
    }

    public static <N> void encode (
        @NotNull Writer writer,
        AnyInstance<N> instance ) throws IOException {

        PdmlWriter pdmlWriter = new PdmlWriter ( writer, PdmlWriterConfig.DEFAULT_CONFIG );
        PdmlEncoder encoder = new PdmlEncoder ( pdmlWriter );
        instance.encode ( encoder );
    }


    // WriterResource

    public static <N> void encode (
        @NotNull WriterResource writerResource,
        @NotNull N nativeObject,
        AnyType<N> type ) throws IOException {

        encode ( writerResource, type.createInstance ( nativeObject, null ) );
    }

    public static <N> void encode (
        @NotNull WriterResource writerResource,
        AnyInstance<N> instance ) throws IOException {

        try ( Writer writer = writerResource.newWriter() ) {
            encode ( writer, instance );
        }
    }


    // String

    public static <N> String encodeToString (
        @NotNull N nativeObject,
        AnyType<N> type ) throws IOException {

        try ( Writer writer = new StringWriter() ) {
            encode ( writer, nativeObject, type );
            return writer.toString();
        }
    }

    public static <N> String encodeToString (
        AnyInstance<N> instance ) throws IOException {

        try ( Writer writer = new StringWriter() ) {
            encode ( writer, instance );
            return writer.toString();
        }
    }


    // File

    public static <N> void encodeToFile (
        @NotNull Path filePath,
        boolean createDirectoryIfNotExist,
        @NotNull N nativeObject,
        AnyType<N> type ) throws IOException {

        encode (
            new FileWriterResource ( filePath, createDirectoryIfNotExist ),
            nativeObject, type );
    }

    public static <N> void encodeToFile (
        @NotNull Path filePath,
        boolean createDirectoryIfNotExist,
        AnyInstance<N> instance ) throws IOException {

        encode (
            new FileWriterResource ( filePath, createDirectoryIfNotExist ),
            instance );
    }
}
