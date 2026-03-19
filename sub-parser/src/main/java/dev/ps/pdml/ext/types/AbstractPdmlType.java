package dev.ps.pdml.ext.types;

import dev.ps.pdml.core.util.PdmlEscapeUtil;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pdml.reader.PdmlTokenReader;

import java.io.IOException;

public abstract class AbstractPdmlType<T> implements PdmlType<T> {


    public interface ObjectParser<T> {

        @NotNull ObjectTokenPair<T> parseObject (
            @NotNull PdmlParser pdmlParser ) throws IOException, PdmlException;
    }

    public interface ObjectValidator<T> {

        void validateObject (
            @NotNull ObjectTokenPair<T> objectTokenPair ) throws InvalidPdmlDataException;
    }

    public interface ObjectHandler<T> {

        void handleObject (
            @NotNull ObjectTokenPair<T> objectTokenPair,
            @Nullable TaggedNode parentNode,
            @NotNull PdmlTokenReader pdmlReader ) throws IOException;
    }


    protected static void handleTextObject (
        @Nullable Object javaObjectContained,
        @Nullable String objectAsString,
        @Nullable TaggedNode parentNode,
        @NotNull PdmlTokenReader pdmlReader,
        boolean escapeInsertedText ) throws IOException {

        if ( objectAsString != null ) {
            if ( parentNode != null ) {
                parentNode.appendText ( objectAsString );
            } else {
                pdmlReader.insertStringToRead (
                    escapeInsertedText ?
                        PdmlEscapeUtil.escapeNodeText ( objectAsString ) :
                        objectAsString );
            }
        }

        if ( parentNode != null ) {
            parentNode.setJavaObjectContained ( javaObjectContained );
        }
    }

    protected static @Nullable String readTrimmedText (
        @NotNull PdmlTokenReader pdmlReader ) throws IOException, PdmlException {

        pdmlReader.skipWhitespaceAndComments();
        // ReaderResourcePosition startLocation = pdmlReader.currentLocation();
        // TextPosition startPosition = pdmlReader.currentPosition();
        // TODO? use parser to read text, with support for extensions
        @Nullable String string = pdmlReader.readTextLeaf ();
        pdmlReader.skipWhitespaceAndComments();
        if ( string != null ) {
            string = string.trim();
        }
        // return startPosition;
        return string;
    }


    protected final @NotNull String name;
    @Override
    public @NotNull String getName() { return name; }

    protected final boolean isNullAllowed;
    @Override
    public boolean isNullAllowed() { return isNullAllowed; }

    protected final @NotNull ObjectParser<T> objectParser;

    protected final @Nullable ObjectValidator<T> objectValidator;

    protected final @Nullable ObjectHandler<T> objectHandler;


    protected AbstractPdmlType (
        @NotNull String name,
        boolean isNullAllowed,
        @NotNull ObjectParser<T> objectParser,
        @Nullable ObjectValidator<T> objectValidator,
        @Nullable ObjectHandler<T> objectHandler ) {

        this.name = name;
        this.isNullAllowed = isNullAllowed;
        this.objectParser = objectParser;
        this.objectValidator = objectValidator;
        this.objectHandler = objectHandler;
    }


    @Override
    public @NotNull ObjectTokenPair<T> parseObject (
        @NotNull PdmlParser pdmlParser ) throws IOException, PdmlException {

        return objectParser.parseObject ( pdmlParser );
    }

    @Override
    public void validateObject (
        @NotNull ObjectTokenPair<T> objectTokenPair ) throws InvalidPdmlDataException {

        @Nullable T object = objectTokenPair.object();
        if ( object == null ) {
            if ( ! isNullAllowed ) {
                throw new InvalidPdmlDataException (
                    "An empty value or null is not allowed.",
                    "NULL_NOT_ALLOWED",
                    objectTokenPair.textLocation () );
            }
        } else {
            if ( objectValidator != null ) {
                objectValidator.validateObject ( objectTokenPair );
            }
        }
    }

    @Override
    public void handleObject (
        @NotNull ObjectTokenPair<T> objectTokenPair,
        @Nullable TaggedNode parentNode,
        @NotNull PdmlTokenReader pdmlReader ) throws IOException {

        if ( objectHandler != null ) {
            objectHandler.handleObject ( objectTokenPair, parentNode, pdmlReader );
        }
    }
}
