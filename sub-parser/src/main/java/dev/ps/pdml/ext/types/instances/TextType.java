package dev.ps.pdml.ext.types.instances;

import dev.ps.shared.text.range.TextPosition;
import dev.ps.pdml.ext.types.AbstractPdmlType;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pdml.reader.PdmlTokenReader;

public class TextType extends AbstractPdmlType<String> {

    private static final AbstractPdmlType.@NotNull ObjectParser<String>
        OBJECT_PARSER = pdmlParser -> {

        // TODO? allow embedded comments
        PdmlTokenReader reader = pdmlParser.pdmlReader ();
        TextPosition startPosition = reader.currentTextPosition();
        // @Nullable String string = reader.readText();
        @Nullable String string = pdmlParser.parseTextLeafAsStringAndIgnoreComments ();
        return new ObjectTokenPair<> ( string, startPosition );
    };

    private static final AbstractPdmlType.@Nullable ObjectValidator<String>
        DEFAULT_OBJECT_VALIDATOR = null;

    private static final AbstractPdmlType.@Nullable ObjectHandler<String>
        OBJECT_HANDLER = ( objectTokenPair, parentNode, pdmlReader ) -> {
            String text = objectTokenPair.object();
            handleTextObject ( text, text, parentNode, pdmlReader, true );
        };

    public static final @NotNull TextType NON_NULL_INSTANCE = new TextType (
        "text", false, DEFAULT_OBJECT_VALIDATOR );

    public static final @NotNull TextType NULLABLE_INSTANCE = new TextType (
        "text_or_null", true, DEFAULT_OBJECT_VALIDATOR );


    public TextType (
        @NotNull String name,
        boolean isNullAllowed,
        AbstractPdmlType.@Nullable ObjectValidator<String> objectValidator ) {

        super ( name, isNullAllowed, OBJECT_PARSER, objectValidator, OBJECT_HANDLER );
    }
}
