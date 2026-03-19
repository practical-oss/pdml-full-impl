package dev.ps.pdml.core.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

import dev.ps.shared.text.reader.util.ParsedString;
import dev.ps.shared.text.range.FromToTextRangeWithCodePointOffsets;
import dev.ps.shared.text.range.TextRange;
import dev.ps.shared.text.range.TextPosition;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidTextException;
import dev.ps.shared.text.reader.stack.CharReaderWithInserts;
import dev.ps.shared.text.reader.stack.CharReaderWithInsertsImpl;
import dev.ps.shared.text.reader.util.CharEscapeUtil;

public class CorePdmlReader_OLD {


    protected interface FailableStringSupplier {
        @Nullable String get() throws IOException, MalformedPdmlException;
    }


    // TODO use CodePointReader
    protected final @NotNull CharReaderWithInserts charReader;
    protected final boolean allowUnicodeEscapes;


    protected CorePdmlReader_OLD (
        @NotNull CharReaderWithInserts charReader,
        boolean allowUnicodeEscapes ) {

        this.charReader = charReader;
        this.allowUnicodeEscapes = allowUnicodeEscapes;
    }

    public CorePdmlReader_OLD (
        @NotNull Reader reader,
        @NotNull ReaderResource resource ) throws IOException {

        this ( CharReaderWithInsertsImpl.createAndAdvance ( reader, resource ), false );
    }


    // Node Start/End

    public boolean readNodeStart() throws IOException {
        return charReader.skipChar ( CorePdmlConstants.NODE_START_CHAR );
    }

    public boolean readNodeEnd() throws IOException {
        return charReader.skipChar ( CorePdmlConstants.NODE_END_CHAR );
    }

    public boolean isAtNodeStart() {
        return charReader.isAtChar ( CorePdmlConstants.NODE_START_CHAR );
    }

    public boolean isAtNodeEnd() {
        return charReader.isAtChar ( CorePdmlConstants.NODE_END_CHAR );
    }


    // Node Components

    public @Nullable String readTagAsString() throws IOException, MalformedPdmlException {
        return readTagOrText (
            CorePdmlConstants.TAG_END_CHARS,
            CorePdmlConstants.INVALID_TAG_CHARS );
    }

    public @Nullable ParsedString readTag() throws IOException, MalformedPdmlException {
        return readParsedString ( this::readTagAsString );
    }

    public @Nullable String readSeparator() throws IOException {
        return charReader.readSpaceOrTabOrLineBreak();
    }

    public @Nullable String readTextAsString() throws IOException, MalformedPdmlException {
        return readTagOrText (
            CorePdmlConstants.TEXT_LEAF_END_CHARS,
            CorePdmlConstants.INVALID_TEXT_LEAF_CHARS );
    }

    public @Nullable ParsedString readText() throws IOException, MalformedPdmlException {

        /*
        long startOffset = charReader.currentCodePointOffset();
        String text = readTextAsString ();
        long endOffset = charReader.currentCodePointOffset();
        return new ParsedString ( text, new CodePointOffsetsTextRange (
            currentResource(), startOffset, endOffset, null ) );
         */
        return readParsedString ( this::readTextAsString );
    }

    protected @Nullable ParsedString readParsedString (
        @NotNull FailableStringSupplier stringSupplier ) throws IOException, MalformedPdmlException {

        long startOffset = charReader.currentCodePointOffset();
        String text = stringSupplier.get();
        if ( text == null ) {
            return null;
        }
        long endOffset = charReader.currentCodePointOffset();
        return new ParsedString ( text, new FromToTextRangeWithCodePointOffsets (
            currentResource(), startOffset, endOffset, null ) );
    }


    // Other Public Methods

    public boolean skipWhitespace() throws IOException {
        return charReader.skipSpacesAndTabsAndLineBreaks();
    }

    public boolean isAtEnd() { return charReader.isAtEnd(); }

    public boolean isNotAtEnd() { return charReader.isNotAtEnd(); }

    public @NotNull ReaderResource currentResource() {

        ReaderResource readerResource = charReader.currentResource();
        assert readerResource != null;
        return readerResource;
    }

    public @NotNull TextPosition currentPosition() {
        return charReader.currentPosition ();
    }

    public char currentChar() {
        return charReader.currentChar();
    }


    // Error

    protected @NotNull MalformedPdmlException error (
        @NotNull String message,
        @NotNull String id,
        @Nullable TextRange textLocation ) {

        return new MalformedPdmlException ( message, id, textLocation );
    }

    protected @NotNull MalformedPdmlException errorAtCurrentPosition (
        @NotNull String message,
        @NotNull String id ) {

        return new MalformedPdmlException ( message, id, currentPosition() );
    }


    // Private Methods

    private @Nullable String readTagOrText (
        @NotNull Set<Character> endChars,
        @NotNull Set<Character> invalidChars ) throws IOException, MalformedPdmlException {

        return readTagOrText ( endChars, invalidChars,
            CorePdmlConstants.TAG_AND_TEXT_ESCAPE_CHARS,
            allowUnicodeEscapes );
    }

    // TODO public needed?
    public @Nullable String readTagOrText (
        @NotNull Set<Character> endChars,
        @NotNull Set<Character> invalidChars,
        @NotNull Map<Character, Character> escapeChars,
        boolean allowUnicodeEscapes ) throws IOException, MalformedPdmlException {

        final StringBuilder result = new StringBuilder();

        while ( true ) {

            char currentChar = charReader.currentChar();

            if ( isAtEnd() ) {
                break;

            } else if ( endChars.contains ( currentChar ) ) {
                break;

            } else if ( invalidChars.contains ( currentChar ) ) {
                throw errorAtCurrentPosition (
                    "Character '" + currentChar + "' is not allowed in this context.",
                    "INVALID_CHAR" );

            } else if ( currentChar <= 0X001F &&
                currentChar != '\n' && currentChar != '\r' && currentChar != '\t' && currentChar != '\f' ) {
                String hexString = charToHexString ( currentChar );
                throw errorAtCurrentPosition (
                    "Unicode code point " + hexString + " is invalid. Unicode code points below U+001F (control characters) are not allowed, except U+0009 (Character Tabulation), U+000A (End of Line), U+000C (Form Feed), and U+000D (Carriage Return).",
                    "INVALID_CHAR" );

            } else if ( currentChar >= 0X0080 && currentChar <= 0X009F ) {
                String hexString = charToHexString ( currentChar );
                throw errorAtCurrentPosition (
                    "Unicode code point " + hexString + " is invalid. Unicode code points in the range U+0080 to U+009F (control characters) are not allowed.",
                    "INVALID_CHAR" );

            /*
                This doesn't work because Java uses UTF-16 to store strings in memory
                Each char in Java is a 16-bit (2-byte) code unit, which follows UTF-16 encoding rules.
                } else if ( currentChar >= 0XD800 && currentChar <= 0XDFFF ) {
                    String hexString = charToHexString ( currentChar );
                    errorDetectedAtCurrentPosition (
                        "Unicode code point " + hexString + " is invalid. Unicode code points in the range U+D800 to U+DFFF are not allowed (they are surrogates reserved to encode code points beyond U+FFFF in UTF-16).",
                        "INVALID_CHAR" );
             */

                // TODO move to before invalid chars
            } else if ( currentChar == CorePdmlConstants.ESCAPE_CHAR ) {
                appendCharacterEscapeSequence ( escapeChars, allowUnicodeEscapes, result );

            } else {
                result.append ( currentChar );
                charReader.advance();
            }
        }

        return result.isEmpty() ? null : result.toString();
    }

    private @NotNull String charToHexString ( char c ) {
        return Integer.toHexString ( c );
    }

    private void appendCharacterEscapeSequence (
        @NotNull Map<Character,Character> charEscapeMap,
        boolean allowUnicodeEscapes,
        @NotNull StringBuilder result )
        throws IOException, MalformedPdmlException {

        try {
            CharEscapeUtil.unescapeSequenceAndAppend ( charReader, charEscapeMap, allowUnicodeEscapes, result );

        } catch ( InvalidTextException e ) {
            String id = e.id ();
            if ( id == null ) id = "INVALID_CHARACTER_ESCAPE_SEQUENCE";
            throw error ( e.getMessage(), id, e.location () );
        }
    }
}
