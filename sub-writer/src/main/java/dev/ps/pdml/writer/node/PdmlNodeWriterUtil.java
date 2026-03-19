package dev.ps.pdml.writer.node;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;

public class PdmlNodeWriterUtil {

    public static void write (
        @NotNull Writer writer,
        @NotNull TaggedNode node,
        boolean usePrettyPrinting,
        @NotNull PdmlNodeWriterConfig config ) throws IOException {

        PdmlNodeWriter pdmlWriter = new PdmlNodeWriter ( writer, config );
        if ( usePrettyPrinting ) {
            pdmlWriter.writeTaggedNodeLines ( node );
        } else {
            pdmlWriter.writeTaggedNode ( node );
        }
    }

    public static void write (
        @NotNull Writer writer,
        @NotNull TaggedNode node,
        boolean usePrettyPrinting ) throws IOException {

        write ( writer, node, usePrettyPrinting, PdmlNodeWriterConfig.DEFAULT_CONFIG );
    }

    public static @NotNull String writeToString (
        @NotNull TaggedNode node,
        boolean usePrettyPrinting,
        @NotNull PdmlNodeWriterConfig config ) {

        try ( Writer stringWriter = new StringWriter() ) {
            write ( stringWriter, node, usePrettyPrinting, config );
            return stringWriter.toString();
        } catch ( IOException e ) {
            throw new RuntimeException ( "String writer should never throw " + e );
        }
    }

    public static @NotNull String writeToString (
        @NotNull TaggedNode node,
        boolean usePrettyPrinting ) {

        try ( Writer stringWriter = new StringWriter() ) {
            write ( stringWriter, node, usePrettyPrinting );
            return stringWriter.toString();
        } catch ( IOException e ) {
            throw new RuntimeException ( "String writer should never throw " + e );
        }
    }

    public static void writeToFile (
        @NotNull Path filePath,
        @NotNull TaggedNode node,
        boolean usePrettyPrinting,
        @NotNull PdmlNodeWriterConfig config ) throws IOException {

        try ( Writer fileWriter = TextFileWriterUtil.createUTF8FileWriter ( filePath, true ) ) {
            write ( fileWriter, node, usePrettyPrinting, config );
        }
    }

    public static void writeToFile (
        @NotNull Path filePath,
        @NotNull TaggedNode node,
        boolean usePrettyPrinting ) throws IOException {

        try ( Writer fileWriter = TextFileWriterUtil.createUTF8FileWriter ( filePath, true ) ) {
            write ( fileWriter, node, usePrettyPrinting );
        }
    }

    public static void writeToStdout (
        @NotNull TaggedNode node,
        boolean usePrettyPrinting,
        @NotNull PdmlNodeWriterConfig config ) {

        /*
        PrintWriter writer = new PrintWriter ( System.out );
        write ( writer, node, config );
        writer.flush();
         */
        String code = writeToString ( node, usePrettyPrinting, config );
        System.out.print ( code );
        System.out.flush();
    }
}
