package dev.ps.pdml.reader;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidTextException;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.range.TextRange;
import dev.ps.shared.text.reader.util.MultilineStringLiteralUtil;
import dev.ps.shared.text.reader.util.RawStringLiteralUtil;
import dev.ps.shared.text.range.TextPosition;
import dev.ps.shared.text.unicode.reader.ChainableCodePointReader;
import dev.ps.shared.text.unicode.reader.CodePointReader;
import dev.ps.shared.text.unicode.reader.InMemoryCodePointReader;
import dev.ps.pdml.core.reader.CorePdmlTokenReader;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;

import java.io.IOException;

import static dev.ps.pdml.data.PdmlExtensionsConstants.*;

public class PdmlTokenReader extends CorePdmlTokenReader {


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

    public @Nullable String readSingleOrMultilineComment() throws IOException, MalformedPdmlException {

        String result = readMultilineComment();
        if ( result != null ) {
            return result;
        } else {
            return readSinglelineComment();
        }
    }

    private @Nullable String readSinglelineComment() throws IOException {

        boolean includeLineBreak;
        if ( isAtString ( SINGLE_LINE_COMMENT_WITH_2_SLASHES_EXTENSION_START ) ) {
            includeLineBreak = true;
        } else if ( isAtString ( SINGLE_LINE_COMMENT_WITH_1_SLASH_EXTENSION_START ) ) {
            includeLineBreak = false;
        } else {
            return null;
        }

        return codePointReader.readLine ( includeLineBreak );
    }

    private @Nullable String readMultilineComment() throws IOException, MalformedPdmlException {

        if ( ! isAtMultilineCommentExtensionStart() ) return null;

        StringBuilder result = new StringBuilder();
        readMultilineCommentSnippet ( result );

        return result.toString();
    }

    private void readMultilineCommentSnippet ( @NotNull StringBuilder result ) throws IOException, MalformedPdmlException {

        TextPosition position = currentTextPosition();

        // we are at the start of a multiline comment, i.e. ^/*
        String caretAndSlash = String.valueOf ( EXTENSION_START_CHAR ) + SINGLE_OR_MULTI_LINE_COMMENT_START_CHAR;
        boolean ok = skipString ( caretAndSlash );
        assert ok;
        result.append ( caretAndSlash );

        // The comment can start with more than one *, e.g. ^/*** ... ***/
        // String stars = readWhileAtChar ( MULTI_LINE_COMMENT_STAR_CHAR );
        String stars = codePointReader.readWhileAtChar ( MULTI_LINE_COMMENT_STAR_CHAR );
        assert stars != null && ! stars.isEmpty();
        result.append ( stars );

        String commentEnd = stars + MULTI_LINE_COMMENT_END_CHAR;

        while ( true ) {

            if ( isAtEnd() ) {
                throw error (
                    "The comment starting at line " + position.startLine () +
                        ", column " + position.startColumn () + " is never closed.",
                    "UNCLOSED_COMMENT",
                    position );
            }

            if ( skipString ( commentEnd ) ) {
                result.append ( commentEnd );
                return;

            } else if ( isAtMultilineCommentExtensionStart () ) {
                readMultilineCommentSnippet ( result ); // recursive call for nested comments

            } else {
                result.appendCodePoint ( currentCodePoint() );
                if ( isNotAtEnd() ) advanceChar();
            }
        }
    }

    private boolean isAtMultilineCommentExtensionStart() throws IOException {
        return isAtString ( PdmlExtensionsConstants.MULTI_LINE_COMMENT_EXTENSION_START );
    }

    public boolean skipSingleOrMultilineComment() throws IOException, MalformedPdmlException {
        // return skipMultilineComment() || skipSinglelineComment();
        return readSingleOrMultilineComment() != null;
    }

    public boolean skipWhitespaceAndComments() throws IOException, MalformedPdmlException {

        if ( isAtEnd() ) return false;

        boolean skipped = false;
        while ( true ) {
            if ( skipWhitespace() ||
                skipSingleOrMultilineComment() ) {
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
        @Nullable TextRange location ) {

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
