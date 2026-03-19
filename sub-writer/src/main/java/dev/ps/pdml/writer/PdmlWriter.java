package dev.ps.pdml.writer;

import dev.ps.pdml.core.writer.CorePdmlWriter;
import dev.ps.pdml.core.writer.PdmlWriterConfig;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.pdml.core.util.PdmlWriterUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;

public class PdmlWriter extends CorePdmlWriter {

    public PdmlWriter ( @NotNull Writer writer, PdmlWriterConfig config ) {
        super ( writer, config );
    }

    public PdmlWriter ( @NotNull Writer writer, int indentSize ) {
        super ( writer, indentSize );
    }

    public PdmlWriter ( @NotNull Writer writer ) {
        super ( writer );
    }


    // All methods inherited from CorePdmlWriter must be overridden to return PdmlWriter

    // Basic Tokens

    @Override
    public PdmlWriter writeNodeStartChar() throws IOException {
        super.writeNodeStartChar();
        return this;
    }

    @Override
    public PdmlWriter writeNodeEndChar() throws IOException {
        super.writeNodeEndChar ();
        return this;
    }

    @Override
    public PdmlWriter writeNodeName ( @NotNull String nodeName ) throws IOException {
        super.writeNodeName ( nodeName );
        return this;
    }

    public PdmlWriter writeNodeName (
        @Nullable String nameSpacePrefix,
        @NotNull String localName ) throws IOException {

        if ( nameSpacePrefix != null && ! nameSpacePrefix.isEmpty() ) {
            writeName ( nameSpacePrefix );
            write ( PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR );
        }

        writeName ( localName );
        return this;
    }

    @Override
    public PdmlWriter writeSpaceSeparator() throws IOException {
        super.writeSpaceSeparator ();
        return this;
    }

    @Override
    public PdmlWriter writeText ( @NotNull String text ) throws IOException {
        super.writeText ( text );
        return this;
    }


    // Convenience Methods

    @Override
    public PdmlWriter writeNodeNameAndSpaceSeparator ( @NotNull String nodeName ) throws IOException {
        super.writeNodeNameAndSpaceSeparator ( nodeName );
        return this;
    }

    public PdmlWriter writeNodeNameAndSpaceSeparator (
        @Nullable String nameSpacePrefix,
        @NotNull String localName ) throws IOException {

        writeNodeName ( nameSpacePrefix, localName );
        return writeSpaceSeparator();
    }

    @Override
    public PdmlWriter writeNodeStart (
        @NotNull String nodeName,
        boolean appendSpaceSeparator ) throws IOException {

        super.writeNodeStart ( nodeName, appendSpaceSeparator );
        return this;
    }

    public PdmlWriter writeNodeStart (
        @Nullable String nameSpacePrefix,
        @NotNull String localName,
        boolean appendSpaceSeparator ) throws IOException {

        writeNodeStartChar();
        writeNodeName ( nameSpacePrefix, localName );
        if ( appendSpaceSeparator ) {
            writeSpaceSeparator();
        }
        return this;
    }

    @Override
    public PdmlWriter writeEmptyNode ( @NotNull String nodeName ) throws IOException {
        super.writeEmptyNode ( nodeName );
        return this;
    }

    public PdmlWriter writeEmptyNode (
        @Nullable String nameSpacePrefix,
        @NotNull String localName ) throws IOException {

        writeNodeStartChar();
        writeNodeName ( nameSpacePrefix, localName );
        return writeNodeEndChar ();
    }

    @Override
    public PdmlWriter writeText ( @NotNull String text, boolean escapeText ) throws IOException {
        super.writeText ( text, escapeText );
        return this;
    }

    @Override
    public PdmlWriter writeRaw ( @NotNull String string ) throws IOException {
        super.writeRaw ( string );
        return this;
    }

    @Override
    public PdmlWriter writeTextNode (
        @NotNull String nodeName,
        @NotNull String text ) throws IOException {

        super.writeTextNode ( nodeName, text );
        return this;
    }

    public PdmlWriter writeTextNode (
        @Nullable String nameSpacePrefix,
        @NotNull String localName,
        @NotNull String text ) throws IOException {

        writeNodeStart ( nameSpacePrefix, localName, true );
        writeText ( text );
        return writeNodeEndChar();
    }

    @Override
    public PdmlWriter writeTextNodeOrEmptyNode (
        @NotNull String nodeName,
        @Nullable String text ) throws IOException {

        super.writeTextNodeOrEmptyNode ( nodeName, text );
        return this;
    }

    public PdmlWriter writeTextNodeOrEmptyNode (
        @Nullable String nameSpacePrefix,
        @NotNull String localName,
        @Nullable String text ) throws IOException {

        if ( isNonEmptyText ( text ) ) {
            return writeTextNode ( nameSpacePrefix, localName, text );
        } else {
            return writeEmptyNode ( nameSpacePrefix, localName );
        }
    }

    @Override
    public PdmlWriter writeTextNodeIfTextNotEmpty (
        @NotNull String nodeName,
        @Nullable String text ) throws IOException {

        super.writeTextNodeIfTextNotEmpty ( nodeName, text );
        return this;
    }

    public PdmlWriter writeTextNodeIfTextNotEmpty (
        @Nullable String nameSpacePrefix,
        @NotNull String localName,
        @Nullable String text ) throws IOException {

        if ( isNonEmptyText ( text ) ) {
            writeTextNode ( nameSpacePrefix, localName, text );
        }
        return this;
    }


    // Pretty Printing

    @Override
    public PdmlWriter increaseIndent() {
        super.increaseIndent();
        return this;
    }

    @Override
    public PdmlWriter decreaseIndent() {
        super.decreaseIndent();
        return this;
    }

    @Override
    public PdmlWriter writeIndent() throws IOException {
        super.writeIndent();
        return this;
    }

    @Override
    public PdmlWriter writeLineBreak() throws IOException {
        super.writeLineBreak();
        return this;
    }


    // Line Mode

    @Override
    public PdmlWriter writeNodeStartLine ( @NotNull String nodeName, boolean increaseIndent ) throws IOException {
        super.writeNodeStartLine ( nodeName, increaseIndent );
        return this;
    }

    public PdmlWriter writeNodeStartLine (
        @Nullable String nameSpacePrefix,
        @NotNull String localName,
        boolean increaseIndent ) throws IOException {

        writeIndent();
        writeNodeStartChar();
        writeNodeName ( nameSpacePrefix, localName );
        writeLineBreak();
        if ( increaseIndent ) increaseIndent();
        return this;
    }

    @Override
    public PdmlWriter writeNodeEndLine ( boolean decreaseIndent ) throws IOException {
        super.writeNodeEndLine ( decreaseIndent );
        return this;
    }

    @Override
    public PdmlWriter writeEmptyNodeLine ( @NotNull String nodeName ) throws IOException {
        super.writeEmptyNodeLine ( nodeName );
        return this;
    }

    public PdmlWriter writeEmptyNodeLine (
        @Nullable String nameSpacePrefix,
        @NotNull String localName ) throws IOException {

        writeIndent();
        writeEmptyNode ( nameSpacePrefix, localName );
        return writeLineBreak();
    }

    @Override
    public PdmlWriter writeTextLine ( @NotNull String text ) throws IOException {
        super.writeTextLine ( text );
        return this;
    }

    @Override
    public PdmlWriter writeTextNodeLine (
        @NotNull String nodeName,
        @NotNull String text ) throws IOException {

        super.writeTextNodeLine ( nodeName, text );
        return this;
    }

    public PdmlWriter writeTextNodeLine (
        @Nullable String nameSpacePrefix,
        @NotNull String localName,
        @NotNull String text ) throws IOException {

        writeIndent();
        writeTextNode ( nameSpacePrefix, localName, text );
        return writeLineBreak();
    }


    // Attributes

    public PdmlWriter writeAttributesStart ( boolean appendSpace ) throws IOException {

        write ( PdmlExtensionsConstants.EXTENSION_START_CHAR );
        write ( PdmlExtensionsConstants.ATTRIBUTES_START_CHAR );
        if ( appendSpace ) writeSpaceSeparator();
        return this;
    }

    public PdmlWriter writeAttributesEnd ( boolean appendSpace ) throws IOException {

        write ( PdmlExtensionsConstants.ATTRIBUTES_END_CHAR );
        if ( appendSpace ) writeSpaceSeparator();
        return this;
    }

    /*
    public PdmlWriter writeAttributesEnd() throws IOException {
        return writeAttributesEnd ( true );
    }
     */

    public PdmlWriter writeAttributesSeparator() throws IOException {
        // return write ( PdmlExtensionsConstants.ATTRIBUTES_SEPARATOR );
        return write ( ' ' );
    }

    public PdmlWriter writeAttributeName ( @NotNull String attributeName ) throws IOException {
        writeName ( attributeName );
        return this;
    }

    public PdmlWriter writeAttributeAssignOperator() throws IOException {
        return write ( PdmlExtensionsConstants.ATTRIBUTE_ASSIGN_CHAR );
    }

    public PdmlWriter writeAttributeValue ( @Nullable String value, boolean unquotedIfPossible ) throws IOException {

        if ( unquotedIfPossible && PdmlAttributeUtil_OLD.canValueBeUnquoted ( value ) ) {
            return writeUnquotedAttributeValue ( value );
        } else {
            return writeDoubleQuotedAttributeValue ( value );
        }
    }

    public PdmlWriter writeAttributeValue ( @Nullable String value ) throws IOException {
        return writeDoubleQuotedAttributeValue ( value );
    }

    public PdmlWriter writeDoubleQuotedAttributeValue ( @Nullable String value ) throws IOException {
        return writeNullableDoubleQuotedStringLiteral ( value );
    }

    public PdmlWriter writeUnquotedAttributeValue ( @NotNull String value ) throws IOException {
        return writeUnquotedStringLiteral ( value );
    }

    public PdmlWriter writeAttribute (
        // @Nullable String nameSpacePrefix,
        @NotNull String name,
        @Nullable String value,
        boolean unquotedValueIfPossible ) throws IOException {

        // writeAttributesSeparator();
        // writeName ( nameSpacePrefix, tag );
        writeAttributeName ( name );
        writeAttributeAssignOperator();
        writeAttributeValue ( value, unquotedValueIfPossible );

        return this;
    }

    public PdmlWriter writeAttribute (
        @NotNull String name,
        @Nullable String value ) throws IOException {

        return writeAttribute ( name, value, false );
    }


    // Namespaces

    public PdmlWriter writeNamespacesStart ( boolean appendSpace ) throws IOException {
        write ( PdmlExtensionsConstants.NAMESPACE_DECLARATIONS_EXTENSION_START );
        if ( appendSpace ) writeSpaceSeparator();
        return this;
    }

    public PdmlWriter writeNamespacesEnd ( boolean appendSpace ) throws IOException {

        write ( PdmlExtensionsConstants.NAMESPACE_DECLARATIONS_END );
        if ( appendSpace ) writeSpaceSeparator();
        return this;
    }

    public PdmlWriter writeNamespacesSeparator() throws IOException {
        // return write ( PdmlExtensionsConstants.NAMESPACE_DECLARATIONS_SEPARATOR );
        return write ( ' ' );
    }


    public PdmlWriter writeNamespace (
        @NotNull String nameSpacePrefix,
        @NotNull String URI ) throws IOException {

        return writeAttribute ( nameSpacePrefix, URI, false );
    }


    // Comment

    public PdmlWriter writeMultilineComment ( @NotNull String comment ) throws IOException {

        // TODO add starts (*) if comment contains */, e.g. ^** */ **/
        write ( PdmlExtensionsConstants.MULTI_LINE_COMMENT_EXTENSION_START );
        writeRaw ( comment );
        return write ( PdmlExtensionsConstants.MULTI_LINE_COMMENT_END );
    }

    public PdmlWriter writeMultilineCommentLine ( @NotNull String comment ) throws IOException {

        writeIndent();
        writeMultilineComment ( comment );
        return writeLineBreak();
    }


    // String Literal

    public PdmlWriter writeDoubleQuotedStringLiteral ( @Nullable CharSequence value ) throws IOException {
        PdmlWriterUtil.writeDoubleQuotedStringLiteral ( value, writer );
        return this;
    }

    public PdmlWriter writeNullableDoubleQuotedStringLiteral ( @Nullable CharSequence value ) throws IOException {
        PdmlWriterUtil.writeNullableDoubleQuotedStringLiteral ( value, writer );
        return this;
    }

    public PdmlWriter writeUnquotedStringLiteral ( @NotNull CharSequence value ) throws IOException {
        PdmlWriterUtil.writeUnquotedStringLiteral ( value, writer );
        return this;
    }


    @Override
    protected @NotNull PdmlWriter write ( @NotNull String string ) throws IOException {
        super.write ( string );
        return this;
    }

    @Override
    protected @NotNull PdmlWriter write ( char aChar ) throws IOException {
        super.write ( aChar );
        return this;
    }
}
