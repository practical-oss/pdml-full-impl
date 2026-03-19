package dev.ps.pdml.ext.types.instances;

import dev.ps.shared.text.range.TextPosition;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.ext.types.AbstractPdmlType;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateType extends AbstractPdmlType<LocalDate> {

    private static final AbstractPdmlType.@NotNull ObjectParser<LocalDate>
        DEFAULT_OBJECT_PARSER = pdmlParser -> {

        TextPosition startPosition = pdmlParser.pdmlReader ().currentTextPosition();
        @Nullable String text = readTrimmedText ( pdmlParser.pdmlReader () );
        if ( text == null ) {
            return new ObjectTokenPair<> ( null, startPosition );
        } else {
            // String stringValue = textToken.getText();
            try {
                return new ObjectTokenPair<> ( LocalDate.parse ( text ), startPosition );
            } catch ( DateTimeParseException e ) {
                throw new InvalidPdmlDataException (
                    "'" + text + "' is an invalid date. Reason: " + e.getMessage (),
                    "ILLEGAL_DATE_VALUE",
                    startPosition );
            }
        }
    };

    private static final AbstractPdmlType.@Nullable ObjectValidator<LocalDate>
        DEFAULT_OBJECT_VALIDATOR = null;

    private static final AbstractPdmlType.@Nullable ObjectHandler<LocalDate>
        DEFAULT_OBJECT_HANDLER = ( objectTokenPair, parentNode, pdmlReader ) -> {
            LocalDate localDate = objectTokenPair.object();
            handleTextObject (
                localDate, localDate == null ? null : localDate.toString(),
                parentNode, pdmlReader, false );
        };


    public static final @NotNull LocalDateType NON_NULL_INSTANCE = new LocalDateType (
        "local_date", false, DEFAULT_OBJECT_PARSER, DEFAULT_OBJECT_VALIDATOR, DEFAULT_OBJECT_HANDLER );

    public static final @NotNull LocalDateType NULLABLE_INSTANCE = new LocalDateType (
        "local_date_or_null", true, DEFAULT_OBJECT_PARSER, DEFAULT_OBJECT_VALIDATOR, DEFAULT_OBJECT_HANDLER );


    public LocalDateType (
        @NotNull String name,
        boolean isNullAllowed,
        @NotNull ObjectParser<LocalDate> objectParser,
        @Nullable ObjectValidator<LocalDate> objectValidator,
        @Nullable ObjectHandler<LocalDate> objectHandler ) {

        super ( name, isNullAllowed, objectParser, objectValidator, objectHandler );
    }
}
