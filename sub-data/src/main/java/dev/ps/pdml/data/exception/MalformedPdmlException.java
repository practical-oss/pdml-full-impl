package dev.ps.pdml.data.exception;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.range.TextRange;

public class MalformedPdmlException extends PdmlException {

    public MalformedPdmlException (
        @NotNull String message,
        @Nullable String id,
        @Nullable TextRange textLocation,
        @Nullable Throwable cause ) {

        super ( message, id, textLocation, cause );
    }

    public MalformedPdmlException (
        @NotNull String message,
        @Nullable String id,
        @Nullable TextRange textLocation ) {

        super ( message, id, textLocation );
    }
}
