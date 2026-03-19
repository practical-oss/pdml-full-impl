package dev.ps.pdml.parser.util;

import dev.ps.shared.text.inspection.InvalidDataException;

import java.io.IOException;
import java.util.function.Consumer;

public interface ThrowableConsumer<T> extends Consumer<T> {

    void acceptOrThrow ( T t ) throws IOException, InvalidDataException;

    default void accept ( T t ) {

        try {
            acceptOrThrow ( t );
        } catch ( IOException | InvalidDataException e ) {
            throw new RuntimeException ( e );
        }
    }
}
