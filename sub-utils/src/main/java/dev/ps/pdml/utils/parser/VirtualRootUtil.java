package dev.ps.pdml.utils.parser;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.reader.CharReader;
import dev.ps.shared.text.reader.CharReaderImpl;
import dev.ps.shared.text.reader.stack.CharReaderWithInserts;
import dev.ps.shared.text.reader.stack.CharReaderWithInsertsImpl;
import dev.ps.shared.text.ioresource.IOResource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

@Deprecated
public class VirtualRootUtil {

    /*
    public static @NotNull CharReaderWithInserts createVirtualRootReader (
        @NotNull String virtualRootName,
        @NotNull CharReader rootContentReader ) throws IOException {

//        CharReader rootNodeStartReader = CharReaderImpl.createForString ( "[" + virtualRootName + " " );
        CharReader rootNodeStartReader = new CharReaderImpl (
            new StringReader ( "[" + virtualRootName + " " ), null, null, null );
//        CharReader rootNodeEndReader = CharReaderImpl.createForString ( "]" );
        CharReader rootNodeEndReader = new CharReaderImpl (
            new StringReader ( "]" ), null, null, null );

        CharReaderWithInserts result = new CharReaderWithInsertsImpl ( rootNodeEndReader );
        result.insert ( rootContentReader );
        result.insert ( rootNodeStartReader );

        return result;
    }

 */

    public static @NotNull CharReaderWithInserts createVirtualRootReader (
        @NotNull Reader rootContentReader,
        @Nullable IOResource rootContentTextResource,
        @Nullable Integer currentLineNumber,
        @Nullable Integer currentColumnLineNumber ) throws IOException {

        return createVirtualRootReader (
            "root", rootContentReader, rootContentTextResource, currentLineNumber, currentColumnLineNumber );
    }

    public static @NotNull CharReaderWithInserts createVirtualRootReader (
        @NotNull String virtualRootName,
        @NotNull Reader rootContentReader,
        @Nullable IOResource rootContentTextResource,
        @Nullable Integer currentLineNumber,
        @Nullable Integer currentColumnLineNumber ) throws IOException {

        // TODO close StringReaders
        CharReader rootNodeStartReader = CharReaderImpl.createAndAdvance (
            new StringReader ( "[" + virtualRootName + " " ), null, null, null );
        CharReader rootNodeEndReader = CharReaderImpl.createAndAdvance (
            new StringReader ( "]" ), null, null, null );
        CharReader rootNodeContentReader = CharReaderImpl.createAndAdvance (
            rootContentReader, rootContentTextResource, currentLineNumber, currentColumnLineNumber );

        CharReaderWithInserts result = new CharReaderWithInsertsImpl ( rootNodeEndReader );
        result.insert ( rootNodeContentReader );
        result.insert ( rootNodeStartReader );

        return result;
    }
}
