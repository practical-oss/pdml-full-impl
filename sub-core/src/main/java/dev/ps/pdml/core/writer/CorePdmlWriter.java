package dev.ps.pdml.core.writer;

import dev.ps.pdml.core.util.EscapeUtil;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.writer.HelperWriter;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class CorePdmlWriter implements Flushable, Closeable {


    /* TODO
        add parameter boolean escapeHTML to write HTML-safe code
            e.g. '<' -> \u003c
        add parameter boolean checkForNestingErrors, e.g.
            number of calls to writeNodeEnd() must be less or equal than calls to writeNodeStart()
            at the end of writing, all opened nodes must also have been closed
            only one root node
            etc.
     */


    protected final @NotNull Writer writer;
    protected final @NotNull HelperWriter helper;


    public CorePdmlWriter ( @NotNull Writer writer, PdmlWriterConfig config ) {

        this.writer = writer;
        this.helper = new HelperWriter ( writer, config );
    }

    public CorePdmlWriter ( @NotNull Writer writer, int indentSize ) {
        this ( writer, new PdmlWriterConfig ( indentSize ) );
    }

    public CorePdmlWriter ( @NotNull Writer writer ) {
        this ( writer, PdmlWriterConfig.DEFAULT_CONFIG );
    }


    // Basic Tokens

    public CorePdmlWriter writeNodeStartChar() throws IOException {
        return write ( CorePdmlConstants.NODE_START_CHAR );
    }

    public CorePdmlWriter writeNodeEndChar() throws IOException {
        return write ( CorePdmlConstants.NODE_END_CHAR );
    }

    public CorePdmlWriter writeTag ( @NotNull String unescapedTag ) throws IOException {
        EscapeUtil.writeTag ( unescapedTag, writer );
        return this;
    }

    public CorePdmlWriter writeSpaceSeparator() throws IOException {
        return write ( ' ' );
    }

    public CorePdmlWriter writeText ( @NotNull String unescapedText ) throws IOException {
        EscapeUtil.writeText ( unescapedText, writer );
        return this;
    }


    // Convenience Methods

    public CorePdmlWriter writeTagAndSpaceSeparator ( @NotNull String unescapedTag ) throws IOException {
        writeTag ( unescapedTag );
        return writeSpaceSeparator ();
    }

    /*
    public CorePdmlWriter writeNodeName ( @NotNull NodeName nodeName ) throws IOException {
        return writeNodeName ( nodeName.qualifiedName(), true );
    }
     */

    public CorePdmlWriter writeNodeStart ( @NotNull String unescapedTag, boolean appendSpaceSeparator ) throws IOException {
        writeNodeStartChar();
        writeTag ( unescapedTag );
        if ( appendSpaceSeparator ) {
            writeSpaceSeparator ();
        }
        return this;
    }

    /*
    public CorePdmlWriter writeNodeStart ( @NotNull NodeName nodeName ) throws IOException {
        return writeNodeStart ( nodeName.qualifiedName() );
    }
     */

    public CorePdmlWriter writeEmptyNode ( @NotNull String unescapedTag ) throws IOException {

        writeNodeStartChar();
        writeTag ( unescapedTag );
        return writeNodeEndChar();
    }

    /*
    public CorePdmlWriter writeEmptyNode ( @NotNull NodeName nodeName ) throws IOException {
        return writeEmptyNode ( nodeName.qualifiedName() );
    }
     */

    public CorePdmlWriter writeText ( @NotNull String text, boolean escapeText ) throws IOException {

        if ( escapeText ) {
            return writeText ( text );
        } else {
            return writeRaw ( text );
        }
    }

    public CorePdmlWriter writeRaw ( @NotNull String string ) throws IOException {
        return write ( string );
    }

    public CorePdmlWriter writeTextNode (
        @NotNull String unescapedTag,
        @NotNull String unescapedText ) throws IOException {

        writeNodeStart ( unescapedTag, true );
        writeText ( unescapedText );
        return writeNodeEndChar();
    }

    public CorePdmlWriter writeTextNodeOrEmptyNode (
        @NotNull String unescapedTag,
        @Nullable String unescapedText ) throws IOException {

        if ( isNonEmptyText ( unescapedText ) ) {
            return writeTextNode ( unescapedTag, unescapedText );
        } else {
            return writeEmptyNode ( unescapedTag );
        }
    }

    public CorePdmlWriter writeTextNodeIfTextNotEmpty (
        @NotNull String unescapedTag,
        @Nullable String unescapedText ) throws IOException {

        if ( isNonEmptyText ( unescapedText ) ) {
            writeTextNode ( unescapedTag, unescapedText );
        }
        return this;
    }

    protected boolean isNonEmptyText ( @Nullable String text ) {
        return text != null && ! text.isEmpty();
    }

    /*
    public CorePdmlWriter writeTextNode (
        @NotNull NodeName nodeName,
        @NotNull String text ) throws IOException {

        return writeTextNode ( nodeName.qualifiedName(), text );
    }
     */


    // Pretty Printing

    public CorePdmlWriter increaseIndent() {
        helper.increaseIndent();
        return this;
    }

    public CorePdmlWriter decreaseIndent() {
        helper.decreaseIndent();
        return this;
    }

    public CorePdmlWriter writeIndent() throws IOException {
        helper.writeIndent();
        return this;
    }

    public CorePdmlWriter writeLineBreak() throws IOException {
        helper.writeLineBreak();
        return this;
    }


    // Line Mode

    public CorePdmlWriter writeNodeStartLine ( @NotNull String unescapedTag, boolean increaseIndent ) throws IOException {

        writeIndent();
        writeNodeStartChar();
        writeTag ( unescapedTag );
        writeLineBreak();
        if ( increaseIndent ) increaseIndent();
        return this;
    }

    /*
    public CorePdmlWriter writeNodeStartLine ( @NotNull NodeName nodeName, boolean increaseIndent ) throws IOException {
        return writeNodeStartLine ( nodeName.qualifiedName(), increaseIndent );
    }
     */

    public CorePdmlWriter writeNodeEndLine ( boolean decreaseIndent ) throws IOException {

        if ( decreaseIndent ) decreaseIndent();
        writeIndent();
        writeNodeEndChar();
        return writeLineBreak();
    }

    public CorePdmlWriter writeEmptyNodeLine ( @NotNull String unescapedTag ) throws IOException {

        writeIndent();
        writeEmptyNode ( unescapedTag );
        return writeLineBreak();
    }

    /*
    public CorePdmlWriter writeEmptyNodeLine ( @NotNull NodeName nodeName ) throws IOException {
        return writeEmptyNodeLine ( nodeName.qualifiedName() );
    }
     */

    public CorePdmlWriter writeTextLine (
        @NotNull String unescapedText ) throws IOException {

        writeIndent();
        writeText ( unescapedText );
        return writeLineBreak();
    }

    public CorePdmlWriter writeTextNodeLine (
        @NotNull String unescapedTag,
        @NotNull String unescapedText ) throws IOException {

        writeIndent();
        writeTextNode ( unescapedTag, unescapedText );
        return writeLineBreak();
    }

    /*
    public CorePdmlWriter writeTextNodeLine (
        @NotNull NodeName nodeName,
        @NotNull String text ) throws IOException {

        return writeTextNodeLine ( nodeName.qualifiedName(), text );
    }
     */


    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }


    protected @NotNull CorePdmlWriter write ( @NotNull String string ) throws IOException {

        writer.write ( string );
        return this;
    }

    protected @NotNull CorePdmlWriter write ( char aChar ) throws IOException {

        writer.write ( aChar );
        return this;
    }
}
