package dev.ps.pdml.parser.util;

import dev.ps.shared.text.inspection.InvalidDataException;

import java.io.IOException;
import java.util.function.Function;

public interface ThrowableFunction<T,R> extends Function<T,R> {

    R applyOrThrow ( T t ) throws IOException, InvalidDataException;

    default R apply ( T t ) {

        try {
            return applyOrThrow ( t );
        } catch ( IOException | InvalidDataException e ) {
            throw new RuntimeException ( e );
        }
    }
}
