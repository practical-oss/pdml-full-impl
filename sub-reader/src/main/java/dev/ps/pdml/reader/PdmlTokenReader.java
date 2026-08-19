package dev.ps.pdml.reader;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidTextException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.location.FromToTextRangeImpl;
import dev.ps.shared.text.location.TextLocation;
import dev.ps.shared.text.reader.util.MultilineStringLiteralUtil;
import dev.ps.shared.text.reader.util.RawStringLiteralUtil;
import dev.ps.shared.text.location.TextPosition;
import dev.ps.shared.text.unicode.reader.ChainableCodePointReader;
import dev.ps.shared.text.unicode.reader.CodePointReader;
import dev.ps.shared.text.unicode.reader.InMemoryCodePointReader;
import dev.ps.pdml.core.reader.CorePdmlTokenReader;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;

import java.io.IOException;

import static dev.ps.pdml.data.PdmlExtensionsConstants.*;

public class PdmlTokenReader extends CorePdmlTokenReader {


    // Comments constants
    public static final @NotNull String LINE_OR_BLOCK_COMMENT_EXTENSION_START =
        String.valueOf ( EXTENSION_START_CHAR ) + LINE_COMMENT_START.charAt ( 0 );
    private static final char LINE_OR_BLOCK_COMMENT_START = LINE_COMMENT_START.charAt ( 0 );
    private static final char BLOCK_COMMENT_END_CHAR = BLOCK_COMMENT_END.charAt ( 1 );


    private final @NotNull ChainableCodePointReader codePointReader;


    private PdmlTokenReader ( @NotNull ChainableCodePointReader chainableCodePointReader ) {
        super ( chainableCodePointReader );
        this.codePointReader = chainableCodePointReader;

        if ( codePointReader.currentResource() == null ) {
            throw new IllegalArgumentException ( "codePointReader.currentResource() must not return null" );
        }
    }

    public PdmlTokenReader ( @NotNull CodePointReader codePointReader ) throws IOException {
        this ( new ChainableCodePointReader ( codePointReader ) );
    }

    public PdmlTokenReader ( @NotNull ReaderResource readerResource ) throws IOException {
        this ( new InMemoryCodePointReader ( readerResource, null ) );
    }


    // Extensions

    public boolean isAtExtensionStartChar() {
        return isAtChar ( PdmlExtensionsConstants.EXTENSION_START_CHAR );
    }

    public boolean skipExtensionStartChar() throws IOException {
        return skipChar ( PdmlExtensionsConstants.EXTENSION_START_CHAR );
    }


    // Comments

    // Only used from parser and this
    public @Nullable String readLineOrBlockComment() throws IOException, MalformedPdmlException {

        String result = readBlockComment();
        if ( result != null ) return result;

        result = readLineComment();
        if ( result != null ) return result;

        if ( isAtChar ( LINE_OR_BLOCK_COMMENT_START ) ) {
            throw error (
                "A comment must start with '" + LINE_COMMENT_START +
                "' (line comment) or '" + BLOCK_COMMENT_START + "' (block comment).",
                "INVALID_COMMENT",
                currentTextPosition () );
        } else {
            return null;
        }
    }

    public boolean skipLineOrBlockComment() throws IOException, MalformedPdmlException {
        // return skipBlockComment() || skipSingleLineComment();
        return readLineOrBlockComment() != null;
    }

    public @Nullable String readLineComment() throws IOException, MalformedPdmlException {

        /*
        boolean includeLineBreak;
        if ( isAtString ( SINGLE_LINE_COMMENT_WITH_2_SLASHES_EXTENSION_START ) ) {
            // includeLineBreak = true;
            includeLineBreak = false;
        } else if ( isAtString ( SINGLE_LINE_COMMENT_WITH_1_SLASH_EXTENSION_START ) ) {
            // includeLineBreak = false;
            codePointReader.advance(); // goto /
            throw error (
                "A comment must start with '" + SINGLE_LINE_COMMENT_WITH_2_SLASHES_EXTENSION_START +
                    "' (single-line comment) or '" + BLOCK_COMMENT_EXTENSION_START + "' (block comment).",
                "INVALID_COMMENT",
                currentTextPosition() );
        } else {
            return null;
        }

        return codePointReader.readLine ( includeLineBreak );
         */

        if ( isAtString ( LINE_COMMENT_START ) ) {
            return codePointReader.readLine ( false );
        } else {
            return null;
        }
    }

    public @Nullable String readBlockComment() throws IOException, MalformedPdmlException {

        if ( ! isAtString ( PdmlExtensionsConstants.BLOCK_COMMENT_START ) ) return null;

        StringBuilder result = new StringBuilder();
        readBlockCommentSnippet ( result );

        return result.toString();
    }

    private void readBlockCommentSnippet (
        @NotNull StringBuilder result ) throws IOException, MalformedPdmlException {

        // We are at the start of a block comment, i.e. /*
        TextPosition startStartPosition = currentTextPosition();
        codePointReader.advance(); // skip /
        TextPosition startEndPosition = currentTextPosition();
        result.append ( LINE_OR_BLOCK_COMMENT_START );

        // The comment can start with more than one *, e.g. ^/*** ... ***/
        String stars = codePointReader.readWhileAtChar ( BLOCK_COMMENT_STAR_CHAR );
        assert stars != null && ! stars.isEmpty();
        result.append ( stars );

        String commentEnd = stars + BLOCK_COMMENT_END_CHAR;

        while ( true ) {

            if ( isAtEnd() ) {
                throw error (
                    "The block comment must be closed later in the document with '" + BLOCK_COMMENT_END + "'.",
                    "UNCLOSED_COMMENT",
                    // startStartPosition );
                    new FromToTextRangeImpl ( codePointReader.currentResource(),
                        startStartPosition, startEndPosition, codePointReader.parentReaderPosition() ) );
            }

            if ( skipString ( commentEnd ) ) {
                result.append ( commentEnd );
                return;

            } else if ( isAtString ( PdmlExtensionsConstants.BLOCK_COMMENT_EXTENSION_START ) ) {
                result.append ( EXTENSION_START_CHAR );
                boolean ok = skipExtensionStartChar();
                assert ok;
                readBlockCommentSnippet ( result ); // recursive call for nested comments

            } else {
                result.appendCodePoint ( currentCodePoint() );
                if ( isNotAtEnd() ) advanceChar();
            }
        }
    }

    // TODO This should be a parser method
    public boolean skipWhitespaceAndComments() throws IOException, MalformedPdmlException {

        if ( isAtEnd() ) return false;

        boolean skipped = false;
        /*
        while ( true ) {
            if ( skipWhitespace() ||
                skipLineOrBlockComment() ) {
                skipped = true;
            } else {
                break;
            }
        }
         */
        while ( true ) {
            if ( skipWhitespace() ) {
                skipped = true;
            } else if ( isAtString ( LINE_OR_BLOCK_COMMENT_EXTENSION_START ) ) {
                skipExtensionStartChar();
                skipLineOrBlockComment();
                skipped = true;
            } else {
                break;
            }
        }
        return skipped;
    }


    // Attributes

    public boolean skipAttributesExtensionStart() throws IOException {
        return skipString ( ATTRIBUTES_EXTENSION_START );
    }

    public boolean skipAttributesStart() throws IOException {
        return skipChar ( PdmlExtensionsConstants.ATTRIBUTES_START_CHAR );
    }

    public boolean skipAttributesEnd() throws IOException {
        return skipChar ( PdmlExtensionsConstants.ATTRIBUTES_END_CHAR );
    }

    public boolean skipAttributeAssignChar() throws IOException {
        return skipChar ( PdmlExtensionsConstants.ATTRIBUTE_ASSIGN_CHAR );
    }


    // Namespaces

    public boolean skipNamespaceSeparator() throws IOException {
        return codePointReader.skipChar ( PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR );
    }

    /*
    public boolean isAtNamespaceDeclarationsExtensionStart () throws IOException {
        return isAtString ( NAMESPACE_DECLARATIONS_EXTENSION_START );
    }
     */


    // String Literals

    public @NotNull String requireRawStringLiteral() throws IOException, MalformedPdmlException {

        @NotNull TextPosition startPosition = currentTextPosition();
        try {
            return RawStringLiteralUtil.readLiteral ( codePointReader );
        } catch ( InvalidTextException e ) {
            throw error (
                e.getMessage(),
                "INVALID_RAW_STRING_LITERAL",
                startPosition );
        }
    }

    public @NotNull String requireMultilineStringLiteral() throws IOException, MalformedPdmlException {

        @NotNull TextPosition startPosition = currentTextPosition();
        try {
            return MultilineStringLiteralUtil.readLiteral (
                codePointReader, MultilineStringLiteralUtil.DEFAULT_ESCAPE_MAP );
        } catch ( InvalidTextException e ) {
            throw error (
                e.getMessage(),
                "INVALID_MULTI_LINE_STRING_LITERAL",
                startPosition );
        }
    }


    // Reader Wrappers

    public void advanceChar() throws IOException {
        codePointReader.advance();
    }

    // TODO? should be private
    public boolean isAtChar ( char c ) {
        return codePointReader.isAtChar ( c );
    }

    // TODO? should be private
    public boolean isAtString ( @NotNull String s ) throws IOException {
        // if ( isAtEnd() ) return false;
        return codePointReader.isAtString ( s );
    }

    // TODO? should be private
    public boolean skipChar ( char c ) throws IOException {
        return codePointReader.skipChar ( c );
    }

    // TODO? should be private
    public boolean skipString ( @NotNull String string ) throws IOException {
        return codePointReader.skipString ( string );
    }

    public boolean skipAllWhileMatchesString ( @NotNull String string ) throws IOException {
        return codePointReader.skipAllWhileMatchesString ( string );
    }

    public void setMark ( int readAheadLimit ) throws IOException {
        codePointReader.setMark ( readAheadLimit );
    }

    public void goBackToMark() throws IOException {
        codePointReader.goBackToMark();
    }

    public int peekNextCodePoint() throws IOException {
        return codePointReader.peekNextCodePoint();
    }


    // Insert

    public void insertReaderResource ( @NotNull ReaderResource readerResource ) throws IOException {
        codePointReader.insertReaderResource ( readerResource, codePointReader.currentReader() );
    }

    public void insertStringToRead ( @NotNull String string ) throws IOException {
        codePointReader.insertString ( string, codePointReader.currentReader() );
    }

    /*
    public void insertFileToRead ( @NotNull Path filePath ) throws IOException {
        codePointScanner.insert ( filePath );
    }
    */


    // Error

    private @NotNull MalformedPdmlException error (
        @NotNull String message,
        @NotNull String id,
        @Nullable TextLocation location ) {

        return new MalformedPdmlException ( message, id, location );
    }

    private @NotNull MalformedPdmlException errorAtCurrentPosition (
        @NotNull String message,
        @NotNull String id ) {

        return new MalformedPdmlException ( message, id, currentTextPosition() );
    }


    /*
    // Debugging

    public @NotNull String stateToString() { return codePointScanner.stateToString(); }

    public void stateToOSOut ( @Nullable String label ) { codePointScanner.stateToOSOut ( label ); }
     */
}
