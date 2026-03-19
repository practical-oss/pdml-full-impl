package dev.ps.pdml.reader;

import dev.ps.shared.text.range.TextPosition;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.core.reader.CorePdmlReader_OLD;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.shared.text.inspection.InvalidTextException;
import dev.ps.shared.text.reader.CharReader;
import dev.ps.shared.text.reader.stack.CharReaderWithInsertsImpl;
import dev.ps.shared.text.reader.stack.CharReaderWithInserts;
import dev.ps.shared.text.reader.util.MultilineStringLiteralUtil;
import dev.ps.shared.text.reader.util.RawStringLiteralUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;

import static dev.ps.pdml.data.PdmlExtensionsConstants.*;

public class PdmlReader_OLD extends CorePdmlReader_OLD {

    private PdmlReader_OLD ( @NotNull CharReaderWithInserts charReader ) {
        super ( charReader, true );
    }

    public PdmlReader_OLD ( @NotNull CharReader charReader ) throws IOException {
        this ( new CharReaderWithInsertsImpl ( charReader ) );
    }


    public boolean skipNamespaceSeparator() throws IOException {
        return charReader.skipChar ( PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR );
    }


    // Comments

    /*
    private boolean isAtSingleOrMultilineCommentExtensionStart() throws IOException {
        return isAtString ( SINGLE_OR_MULTI_LINE_COMMENT_EXTENSION_START );
    }

    private boolean isAtSinglelineCommentExtensionStart() throws IOException {
        return isAtString ( PdmlExtensionsConstants.SINGLE_LINE_COMMENT_WITH_2_SLASHES_EXTENSION_START );
    }
     */

    private boolean isAtMultilineCommentExtensionStart() throws IOException {
        return isAtString ( PdmlExtensionsConstants.MULTI_LINE_COMMENT_EXTENSION_START );
    }

    public boolean skipSingleOrMultilineComment() throws IOException, MalformedPdmlException {
        return skipMultilineComment() || skipSinglelineComment();
    }

    public boolean skipMultilineComment() throws IOException, MalformedPdmlException {

        // can be made faster by writing a specific version that doesn't use a StringBuilder
        // to build and return the comment's content
        return readMultilineComment() != null;
    }

    public boolean skipSinglelineComment() throws IOException {
        return readSinglelineComment() != null;
    }

    public @Nullable String readSingleOrMultilineComment() throws IOException, MalformedPdmlException {

        String result = readMultilineComment();
        if ( result != null ) {
            return result;
        } else {
            return readSinglelineComment();
        }
    }

    public @Nullable String readMultilineComment() throws IOException, MalformedPdmlException {

        if ( ! isAtMultilineCommentExtensionStart() ) return null;

        StringBuilder result = new StringBuilder();
        readMultilineCommentSnippet ( result );

        return result.toString();
    }

    private void readMultilineCommentSnippet ( @NotNull StringBuilder result ) throws IOException, MalformedPdmlException {

        TextPosition position = currentPosition();

        // we are at the start of a multiline comment, i.e. ^/*
        String caretAndSlash = String.valueOf ( EXTENSION_START_CHAR ) + SINGLE_OR_MULTI_LINE_COMMENT_START_CHAR;
        boolean ok = skipString ( caretAndSlash );
        assert ok;
        result.append ( caretAndSlash );

        // The comment can start with more than one *, e.g. ^/*** ... ***/
        String stars = readWhileAtChar ( MULTI_LINE_COMMENT_STAR_CHAR );
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
                result.append ( currentChar() );
                if ( isNotAtEnd() ) advanceChar();
            }
        }
    }

    public @Nullable String readSinglelineComment() throws IOException {

        boolean includeLineBreak;
        if ( isAtString ( SINGLE_LINE_COMMENT_WITH_2_SLASHES_EXTENSION_START ) ) {
            includeLineBreak = true;
        } else if ( isAtString ( SINGLE_LINE_COMMENT_WITH_1_SLASH_EXTENSION_START ) ) {
            includeLineBreak = false;
        } else {
            return null;
        }

        return charReader.readUntilEndOfLine ( includeLineBreak );
    }

    public boolean skipWhitespaceAndComments() throws IOException, MalformedPdmlException {

        if ( isAtEnd() ) return false;

        boolean skipped = false;
        while ( true ) {
            if ( skipWhitespace () ||
                skipSingleOrMultilineComment() ) {
                skipped = true;
            } else {
                break;
            }
        }
        return skipped;
    }


    // Namespaces

    /*
    public boolean isAtNamespaceDeclarationsExtensionStart () throws IOException {
        return isAtString ( NAMESPACE_DECLARATIONS_EXTENSION_START );
    }
     */


    // Attributes

    /*
    public boolean isAtAttributesExtensionStart() throws IOException {
        return isAtString ( ATTRIBUTES_EXTENSION_START );
    }

    public boolean isAtAttributesStart() {
        return isAtChar ( ATTRIBUTES_START_CHAR );
    }
     */

    public boolean readAttributesExtensionStart() throws IOException {
        return skipString ( ATTRIBUTES_EXTENSION_START );
    }

    public boolean readAttributesStart() throws IOException {
        return skipChar ( PdmlExtensionsConstants.ATTRIBUTES_START_CHAR );
    }

    public boolean readAttributesEnd() throws IOException {
        return skipChar ( PdmlExtensionsConstants.ATTRIBUTES_END_CHAR );
    }

    /*
    public boolean readAttributeAssignSymbol() throws IOException {
        return skipChar ( PdmlExtensionsConstants.ATTRIBUTE_ASSIGN_CHAR );
    }
     */


    // Extensions

    public boolean isAtExtensionStart() {
        return isAtChar ( PdmlExtensionsConstants.EXTENSION_START_CHAR );
    }

    public boolean readExtensionStartChar() throws IOException {
        return skipChar ( PdmlExtensionsConstants.EXTENSION_START_CHAR );
    }

    /*
    public @Nullable String readExtensionKindLetters() throws IOException {
        return charReader.readWhile ( Character::isLetter );
    }
     */

    public @NotNull String readRawStringLiteral() throws IOException, MalformedPdmlException {

        @NotNull TextPosition startPosition = currentPosition();
        try {
            return RawStringLiteralUtil.readLiteral ( charReader );
        } catch ( InvalidTextException e ) {
            throw error (
                e.getMessage(),
                "INVALID_RAW_STRING_LITERAL",
                startPosition );
        }
    }

    public @NotNull String readMultilineStringLiteral() throws IOException, MalformedPdmlException {

        @NotNull TextPosition startPosition = currentPosition();
        try {
            return MultilineStringLiteralUtil.readLiteral (
                charReader, MultilineStringLiteralUtil.DEFAULT_ESCAPE_MAP );
        } catch ( InvalidTextException e ) {
            throw error (
                e.getMessage(),
                "INVALID_MULTI_LINE_STRING_LITERAL",
                startPosition );
        }
    }


    // Reader Wrappers

    // @Override

    public boolean isAtChar ( char c ) {
        return charReader.isAtChar ( c );
    }

    public boolean isAtString ( @NotNull String s ) throws IOException {
        // if ( isAtEnd() ) return false;
        return charReader.isAtString ( s );
    }

    // Skip

    public boolean skipChar ( char c ) throws IOException {
        return charReader.skipChar ( c );
    }

    public boolean skipString ( @NotNull String string ) throws IOException {
        return charReader.skipString ( string );
    }

    public void advanceChar() throws IOException {
        charReader.advance();
    }

    /*
    public boolean skipSpaceOrTabOrLineBreak() throws IOException {
        return charReader.skipSpaceOrTabOrLineBreak();
    }
     */

    // Read

    public @Nullable String readWhileAtChar ( char c ) throws IOException {
        return charReader.readWhileAtChar ( c );
    }

    public boolean skipAllWhileCharsMatch ( @NotNull String chars ) throws IOException {
        return charReader.skipAllWhileCharsMatch ( chars );
    }


    // Read-ahead

    public void setMark ( int readAheadLimit ) { charReader.setMark ( readAheadLimit ); }

    public void removeMark() { charReader.removeMark(); }

    public void goBackToMark() { charReader.goBackToMark(); }

    /*
    public boolean isNextChar ( char c ) throws IOException {
        return charReader.isNextChar ( c );
    }
     */

    public @Nullable Character peekNextChar() throws IOException {
        return charReader.peekNextChar();
    }


    // Error Handling

/*
    private MalformedPdmlException error (
        @NotNull String message,
        @NotNull String id,
        TextSpan positionOrRange ) {

        return new MalformedPdmlException ( message, id, positionOrRange );
    }
 */

    /*
    public MalformedPdmlException errorAtCurrentLocation (
        @NotNull String message, @NotNull String id ) {

        return new MalformedPdmlException ( message, id, currentPosition() );
    }
     */


    // Insert

    public void insertStringToRead (
        @NotNull String string,
        @Nullable ReaderResource textResource ) {

        if ( textResource == null ) {
            charReader.insert ( string );
        } else {
            charReader.insert ( string, textResource );
        }
    }

/*
    public void insertFileToRead ( @NotNull Path filePath ) throws IOException {
        charReader.insert ( filePath );
    }



    // Debugging

    public @NotNull String stateToString() { return charReader.stateToString(); }

    public void stateToOSOut ( @Nullable String label ) { charReader.stateToOSOut ( label ); }
 */
}
