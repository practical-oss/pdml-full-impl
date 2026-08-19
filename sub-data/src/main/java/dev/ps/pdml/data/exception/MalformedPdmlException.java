package dev.ps.pdml.data.exception;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.location.TextLocation;

public class MalformedPdmlException extends PdmlException {

    public MalformedPdmlException (
        @NotNull String message,
        @Nullable String id,
        @Nullable TextLocation textLocation,
        @Nullable Throwable cause ) {

        super ( message, id, textLocation, cause );
    }

    public MalformedPdmlException (
        @NotNull String message,
        @Nullable String id,
        @Nullable TextLocation textLocation ) {

        super ( message, id, textLocation );
    }
}
