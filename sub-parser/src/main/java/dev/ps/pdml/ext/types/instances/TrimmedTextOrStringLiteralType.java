package dev.ps.pdml.ext.types.instances;

import dev.ps.shared.text.range.TextPosition;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.ext.types.AbstractPdmlType;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pdml.reader.PdmlTokenReader;

import java.io.IOException;

// Currently not used
public class TrimmedTextOrStringLiteralType extends AbstractPdmlType<String> {


    private static final AbstractPdmlType.@NotNull ObjectParser<String>
        OBJECT_PARSER = pdmlParser -> {

        TextPosition startPosition = pdmlParser.pdmlReader ().currentTextPosition();
        String string = pdmlParser.parseTextLeafAsTrimmedTextOrStringLiteral ();
        return new ObjectTokenPair<> ( string, startPosition );
    };

    private static final AbstractPdmlType.@Nullable ObjectValidator<String>
        DEFAULT_OBJECT_VALIDATOR = null;

    private static final AbstractPdmlType.@Nullable ObjectHandler<String>
        OBJECT_HANDLER = null;


    public static final @NotNull TrimmedTextOrStringLiteralType
        NON_NULL_INSTANCE = new TrimmedTextOrStringLiteralType (
            "text_or_string_literal", false, DEFAULT_OBJECT_VALIDATOR, true );

    public static final @NotNull TrimmedTextOrStringLiteralType
        NULLABLE_INSTANCE = new TrimmedTextOrStringLiteralType (
        "text_or_string_literal_or_null", true, DEFAULT_OBJECT_VALIDATOR, true );


    private final boolean escapeInsertedText;
    public boolean getEscapeInsertedText() { return escapeInsertedText; }


    public TrimmedTextOrStringLiteralType (
        @NotNull String name,
        boolean isNullAllowed,
        @Nullable ObjectValidator<String> objectValidator,
        boolean escapeInsertedText ) {

        super ( name, isNullAllowed, OBJECT_PARSER, objectValidator, OBJECT_HANDLER );

        this.escapeInsertedText = escapeInsertedText;
    }


    @Override
    public void handleObject (
        @NotNull ObjectTokenPair<String> objectTokenPair,
        @Nullable TaggedNode parentNode,
        @NotNull PdmlTokenReader pdmlReader ) throws IOException {

        String string = objectTokenPair.object();
        handleTextObject ( string, string, parentNode, pdmlReader, escapeInsertedText );
    }
}
