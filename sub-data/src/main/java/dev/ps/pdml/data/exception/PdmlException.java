package dev.ps.pdml.data.exception;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidTextException;
import dev.ps.shared.text.inspection.message.TextInspectionError;
import dev.ps.shared.text.location.TextLocation;

public class PdmlException extends InvalidTextException {

/*
    @Deprecated
    public static @NotNull PdmlException createForAbortingProgram() {
        return new PdmlException (
            "Operation aborted because errors were reported",
            "OPERATION_ABORTED",
            null );
    }
 */

    public PdmlException (
        @NotNull String message,
        @Nullable String id,
        @Nullable TextLocation textLocation,
        @Nullable Throwable cause ) {

        super ( message, id, textLocation, cause );
    }

    public PdmlException (
        @NotNull String message,
        @Nullable String id,
        @Nullable TextLocation textLocation ) {

        super ( message, id, textLocation );
    }

    public PdmlException ( @NotNull TextInspectionError textInspectionError ) {
        super ( textInspectionError );
    }

    public PdmlException ( @NotNull Exception cause ) {
        this ( cause.getMessage (), null, (TextLocation) null, cause );
    }

    public PdmlException ( @NotNull InvalidTextException invalidTextException ) {

        this ( invalidTextException.getMessage (),
            invalidTextException.id (),
            invalidTextException.location (),
            invalidTextException.getCause () );
    }
}
