package dev.ps.pdml.data.exception;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.inspection.InvalidTextException;
import dev.ps.shared.text.inspection.message.TextInspectionError;
import dev.ps.shared.text.range.TextRange;

public class InvalidPdmlDataException extends PdmlException {

    public InvalidPdmlDataException (
        @NotNull String message,
        @Nullable String id,
        @Nullable TextRange textLocation,
        @Nullable Throwable cause ) {

        super ( message, id, textLocation, cause );
    }

    public InvalidPdmlDataException (
        @NotNull String message,
        @Nullable String id,
        @Nullable TextRange textLocation ) {

        super ( message, id, textLocation );
    }

    public InvalidPdmlDataException ( @NotNull Exception cause ) {
        super ( cause );
    }

    public InvalidPdmlDataException ( @NotNull TextInspectionError textInspectionError ) {
        super ( textInspectionError );
    }

    public InvalidPdmlDataException ( @NotNull InvalidTextException invalidTextException ) {
        super ( invalidTextException );
    }

    public InvalidPdmlDataException (
        @NotNull InvalidDataException invalidDataException ) {

        this ( invalidDataException.getMessage(),
            invalidDataException.id (),
            invalidDataException.location (),
            invalidDataException.getCause() );
    }
}
