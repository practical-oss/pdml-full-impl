package dev.ps.pdml.core.reader;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidTextException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.range.*;
import dev.ps.shared.text.reader.util.CodePointEscapeUtil;
import dev.ps.shared.text.reader.util.ParsedString;
import dev.ps.shared.text.unicode.reader.CodePointReader;
import dev.ps.shared.text.unicode.reader.InMemoryCodePointReader;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class CorePdmlTokenReader {


    private final @NotNull CodePointReader codePointReader;
    // TODO?
    public @NotNull CodePointReader codePointReader() { return codePointReader; }


    public CorePdmlTokenReader ( @NotNull CodePointReader codePointReader ) {

        this.codePointReader = codePointReader;

        if ( codePointReader.currentResource() == null ) {
            throw new IllegalArgumentException ( "codePointReader.currentResource() must not return null" );
        }
    }

    public CorePdmlTokenReader ( @NotNull ReaderResource readerResource ) throws IOException {
        this ( new InMemoryCodePointReader ( readerResource, null ) );
    }


    // Essential readXXX Methods

    public @Nullable String readNodeStart() throws IOException {
        return skipNodeStart() ? CorePdmlConstants.NODE_START_STRING : null;
    }

    public @Nullable String readNodeEnd() throws IOException {
        return skipNodeEnd() ? CorePdmlConstants.NODE_END_STRING : null;
    }

    public @Nullable String readTag() throws IOException, MalformedPdmlException {
        return readTagOrText (
            CorePdmlConstants.TAG_END_CHARS,
            CorePdmlConstants.INVALID_TAG_CHARS );
    }

    public @Nullable String readSeparator() throws IOException {
        return codePointReader.readSpaceOrTabOrLineBreak();
    }

    public @Nullable String readTextLeaf() throws IOException, MalformedPdmlException {
        return readTagOrText (
            CorePdmlConstants.TEXT_LEAF_END_CHARS,
            CorePdmlConstants.INVALID_TEXT_LEAF_CHARS );
    }

    private @Nullable String readTagOrText (
        @NotNull Set<Character> endChars,
        @NotNull Set<Character> invalidChars ) throws IOException, MalformedPdmlException {

        return readTextFragment ( endChars, invalidChars,
            CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CODE_POINTS,
            false );
    }

    public @Nullable String readTextFragment (
        @NotNull Set<Character> endChars,
        @NotNull Set<Character> invalidChars,
        @Nullable Map<@NotNull Integer, @NotNull Integer> escapeMap,
        boolean allowUnicodeEscapes ) throws IOException, MalformedPdmlException {

        final StringBuilder result = new StringBuilder();

        while ( isNotAtEnd() ) {

            int currentCodePoint = codePointReader.currentCodePoint();
            if ( currentCodePoint > 0XFFFF ) {
                result.appendCodePoint ( currentCodePoint );
                codePointReader.advance();
                continue;
            }

            char currentChar = (char) currentCodePoint;

            if ( endChars.contains ( currentChar ) ) {
                break;
            }

            if ( currentChar == CorePdmlConstants.ESCAPE_CHAR ) {
                appendCharacterEscapeSequence ( escapeMap, allowUnicodeEscapes, result );
                continue;
            }

            if ( invalidChars.contains ( currentChar ) ) {
                throw errorAtCurrentPosition (
                    "Character '" + currentChar + "' is not allowed in this context.",
                    "INVALID_CHAR" );
            }

            if ( currentChar <= 0X001F &&
                currentChar != '\n' && currentChar != '\r' && currentChar != '\t' && currentChar != '\f' ) {
                throw errorAtCurrentPosition (
                    "Unicode code point " + charToHexString ( currentChar ) +
                        " is invalid. Unicode code points below U+001F (control characters) are not allowed, except U+0009 (Character Tabulation), U+000A (End of Line), U+000C (Form Feed), and U+000D (Carriage Return).",
                    "INVALID_CHAR" );
            }

            if ( currentChar >= 0X0080 && currentChar <= 0X009F ) {
                throw errorAtCurrentPosition (
                    "Unicode code point " + charToHexString ( currentChar ) +
                        " is invalid. Unicode code points in the range U+0080 to U+009F (control characters) are not allowed.",
                    "INVALID_CHAR" );
            }

            result.append ( currentChar );
            codePointReader.advance();
        }

        return result.isEmpty() ? null : result.toString();
    }

    private @NotNull String charToHexString ( char c ) {
        return Integer.toHexString ( c );
    }

    private void appendCharacterEscapeSequence (
        @Nullable Map<@NotNull Integer, @NotNull Integer> escapeMap,
        boolean allowUnicodeEscapes,
        @NotNull StringBuilder result )
        throws IOException, MalformedPdmlException {

        try {
            CodePointEscapeUtil.unescapeSequenceAndAppend (
                codePointReader, escapeMap, allowUnicodeEscapes, result );

        } catch ( InvalidTextException e ) {
            String id = e.id ();
            if ( id == null ) id = "INVALID_CHARACTER_ESCAPE_SEQUENCE";
            throw error ( e.getMessage(), id, e.location () );
        }
    }

    /*
    public @Nullable ParsedString<FromToTextRange> readTextLeafWithTextRange () throws IOException, MalformedPdmlException {
        return readWithTextRange ( this::readTextLeaf );
    }
     */


    // skipXXX Methods

    public boolean skipNodeStart() throws IOException {
        return codePointReader.skipChar ( CorePdmlConstants.NODE_START_CHAR );
    }

    public boolean skipNodeEnd() throws IOException {
        return codePointReader.skipChar ( CorePdmlConstants.NODE_END_CHAR );
    }

    public boolean skipWhitespace() throws IOException {
        return codePointReader.skipSpacesAndTabsAndLineBreaks();
    }


    // isAtXXX Methods

    public boolean isAtNodeStart() {
        return codePointReader.isAtChar ( CorePdmlConstants.NODE_START_CHAR );
    }

    public boolean isAtNodeEnd() {
        return codePointReader.isAtChar ( CorePdmlConstants.NODE_END_CHAR );
    }


    // Current State Methods

    public boolean isAtEnd() { return codePointReader.isAtEnd(); }

    public boolean isNotAtEnd() { return codePointReader.isNotAtEnd(); }

    /*
    public @Nullable ReaderResource currentResource() {
        return codePointReader.currentResource();
    }
     */
    public @NotNull ReaderResource currentResource() {

        ReaderResource resource = codePointReader.currentResource();
        assert resource != null;
        return resource;
    }

    public int currentCodePoint() {
        return codePointReader.currentCodePoint();
    }

    public @Nullable String currentCodePointAsString() {
        return codePointReader.currentCodePointAsString();
    }

    public long currentLine() {
        return codePointReader.currentLine();
    }

    public long currentColumn() {
        return codePointReader.currentColumn();
    }

    public long currentCodePointOffset() {
        return codePointReader.currentOffset ();
    }

    public @NotNull TextPosition currentTextPosition() {
        return codePointReader.currentTextPosition();
    }


    /* TODO?
    // requireXXX Methods

    public @NotNull CorePdmlTokenReader requireNodeStart() throws IOException, MalformedPdmlException {
        require ( this::readNodeStart, "Node start required.", "NODE_START_REQUIRED" );
        return this;
    }

    public @NotNull String requireTag() throws IOException, MalformedPdmlException {
        return require ( this::readTag, "Node tag required.", "NODE_TAG_REQUIRED" );
    }

    public @NotNull CorePdmlTokenReader requireTag ( @NotNull Consumer<String> tagConsumer)
        throws IOException, MalformedPdmlException {

        tagConsumer.accept ( requireTag() );
        return this;
    }

    public @NotNull CorePdmlTokenReader requireTag (
        @NotNull String tagRequired,
        boolean ignoreCase ) throws IOException, MalformedPdmlException {

        TextPosition position = currentTextPosition();
        String tagRead = requireTag();
        if ( ! ignoreCase && tagRead.equals ( tagRequired ) ||
            ignoreCase && tagRead.equalsIgnoreCase ( tagRequired ) ) {
            return this;
        } else {
            throw error (
                "Tag '" + tagRequired + "' is required, but tag '" + tagRead + "' was read.",
                "INVALID_TAG",
                position );
        }
    }

    public @NotNull CorePdmlTokenReader requireAnyTag() throws IOException, MalformedPdmlException {
        requireTag();
        return this;
    }

    public @NotNull String requireTextLeaf() throws IOException, MalformedPdmlException {
        return require ( this::readTextLeaf, "Text leaf required.", "TEXT_LEAF_REQUIRED" );
    }

    // TODO add others

    protected interface FailableStringSupplier {
        @Nullable String get() throws IOException, PdmlException;
    }

    protected @NotNull String require (
        @NotNull FailableStringSupplier supplier,
        @NotNull String errorMessage,
        @NotNull String errorId ) throws IOException, MalformedPdmlException {

        String result = supplier.get();
        if ( result != null ) {
            return result;
        } else {
            throw errorAtCurrentPosition ( errorMessage, errorId );
        }
    }
     */


    // Convenience Methods

    public interface ReadStringMethodInvoker {
        @Nullable String readString ( @NotNull CorePdmlTokenReader reader ) throws IOException, MalformedPdmlException;
    }

    public @Nullable ParsedString<FromToTextRange> readWithTextRange (
        @NotNull ReadStringMethodInvoker methodInvoker ) throws IOException, MalformedPdmlException {

        long startOffset = codePointReader.currentOffset();
        String string = methodInvoker.readString ( this );
        if ( string == null ) {
            return null;
        }

        ReaderResource resource = codePointReader.currentResource();
        assert resource != null;
        return new ParsedString<> ( string, new FromToTextRangeWithCodePointOffsets (
            resource, startOffset, codePointReader.currentOffset(), codePointReader.parentReaderPosition() ) );
    }

    public @Nullable ParsedString<TextPosition> readWithTextPosition (
        @NotNull ReadStringMethodInvoker methodInvoker ) throws IOException, MalformedPdmlException {

        TextPosition textStartPosition = codePointReader.currentTextPosition();
        String string = methodInvoker.readString ( this );
        return string != null
            ? new ParsedString<> ( string, textStartPosition )
            : null;
    }

/*
    public @Nullable ParsedString<TextPosition> readWithTextPosition (
        @NotNull ReadStringMethodInvoker methodInvoker ) throws IOException, MalformedPdmlException {

        ReaderResource readerResource = currentResource();
        if ( readerResource != null ) {
            long offset = codePointReader.currentOffset();
            String string = methodInvoker.readString ( this );
            if ( string == null ) {
                return null;
            }
            // TODO parentReader
            return new ParsedString<> ( string, new TextPositionWithCodePointOffset (
                readerResource, offset, null ) );

        } else {
            long line = codePointReader.currentLine();
            long column = codePointReader.currentColumn();
            String string = methodInvoker.readString ( this );
            if ( string == null ) {
                return null;
            }
            // TODO parentReader
            return new ParsedString<> ( string, new TextPositionImpl (
                (ReaderResource) null, line, column, codePointReader.currentCodePoint(), null ) );
        }
    }
 */


    // Error Handling

    private @NotNull MalformedPdmlException error (
        @NotNull String message,
        @NotNull String id,
        @Nullable TextRange location ) {

        return new MalformedPdmlException ( message, id, location );
    }

    private @NotNull MalformedPdmlException errorAtCurrentPosition (
        @NotNull String message,
        @NotNull String id ) {

        return new MalformedPdmlException ( message, id, currentTextPosition() );
    }
}
