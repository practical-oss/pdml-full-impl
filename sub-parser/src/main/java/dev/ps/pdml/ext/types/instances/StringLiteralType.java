package dev.ps.pdml.ext.types.instances;

import dev.ps.shared.text.range.TextPosition;
import dev.ps.pdml.ext.types.AbstractPdmlType;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pdml.reader.PdmlTokenReader;

public class StringLiteralType extends AbstractPdmlType<String> {

    private static final AbstractPdmlType.@NotNull ObjectParser<String>
        OBJECT_PARSER = pdmlParser -> {

        PdmlTokenReader pdmlReader = pdmlParser.pdmlReader ();
        pdmlReader.skipWhitespaceAndComments ();
        TextPosition startPosition = pdmlReader.currentTextPosition();
        // @Nullable String string = pdmlParser.parseStringLiteralOrNull();
        @Nullable String string = pdmlParser.parseTextLeafAsTrimmedTextOrStringLiteral ();
        pdmlReader.skipWhitespaceAndComments ();
        return new ObjectTokenPair<> (
            string, startPosition );
    };

    private static final AbstractPdmlType.@Nullable ObjectValidator<String>
        DEFAULT_OBJECT_VALIDATOR = null;

    private static final AbstractPdmlType.@Nullable ObjectHandler<String>
        OBJECT_HANDLER = ( objectTokenPair, parentNode, pdmlReader ) -> {
            String string = objectTokenPair.object();
            handleTextObject ( string, string, parentNode, pdmlReader, true );
        };


    public static final @NotNull StringLiteralType NON_NULL_INSTANCE = new StringLiteralType (
        "string", false, DEFAULT_OBJECT_VALIDATOR );

    public static final @NotNull StringLiteralType NULLABLE_INSTANCE = new StringLiteralType (
        "string_or_null", true, DEFAULT_OBJECT_VALIDATOR );


    public StringLiteralType (
        @NotNull String name,
        boolean isNullAllowed,
        AbstractPdmlType.@Nullable ObjectValidator<String> objectValidator ) {

        super ( name, isNullAllowed, OBJECT_PARSER, objectValidator, OBJECT_HANDLER );
    }
}

