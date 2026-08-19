package dev.ps.pdml.writer;

import dev.ps.pdml.core.util.EscapeUtil;
import dev.ps.pdml.core.writer.CorePdmlWriter;
import dev.ps.pdml.core.writer.PdmlWriterConfig;
import dev.ps.pdml.data.PdmlExtensionsConstants;
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
    public PdmlWriter writeTag ( @NotNull String unescapedTag ) throws IOException {
        super.writeTag ( unescapedTag );
        return this;
    }

    public PdmlWriter writeNSAndTag (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag ) throws IOException {

        if ( nameSpacePrefix != null && ! nameSpacePrefix.isEmpty() ) {
            writeTag ( nameSpacePrefix );
            write ( PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR );
        }

        writeTag ( unescapedTag );
        return this;
    }

    @Override
    public PdmlWriter writeSpaceSeparator() throws IOException {
        super.writeSpaceSeparator ();
        return this;
    }

    @Override
    public PdmlWriter writeText ( @NotNull String unescapedText ) throws IOException {
        super.writeText ( unescapedText );
        return this;
    }


    // Convenience Methods

    @Override
    public PdmlWriter writeTagAndSpaceSeparator ( @NotNull String unescapedTag ) throws IOException {
        super.writeTagAndSpaceSeparator ( unescapedTag );
        return this;
    }

    public PdmlWriter writeTagAndSpaceSeparator (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag ) throws IOException {

        writeNSAndTag ( nameSpacePrefix, unescapedTag );
        return writeSpaceSeparator();
    }

    @Override
    public PdmlWriter writeNodeStart (
        @NotNull String unescapedTag,
        boolean appendSpaceSeparator ) throws IOException {

        super.writeNodeStart ( unescapedTag, appendSpaceSeparator );
        return this;
    }

    public PdmlWriter writeNodeStart (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag,
        boolean appendSpaceSeparator ) throws IOException {

        writeNodeStartChar();
        writeNSAndTag ( nameSpacePrefix, unescapedTag );
        if ( appendSpaceSeparator ) {
            writeSpaceSeparator();
        }
        return this;
    }

    @Override
    public PdmlWriter writeEmptyNode ( @NotNull String unescapedTag ) throws IOException {
        super.writeEmptyNode ( unescapedTag );
        return this;
    }

    public PdmlWriter writeEmptyNode (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag ) throws IOException {

        writeNodeStartChar();
        writeNSAndTag ( nameSpacePrefix, unescapedTag );
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
        @NotNull String unescapedTag,
        @NotNull String unescapedText ) throws IOException {

        super.writeTextNode ( unescapedTag, unescapedText );
        return this;
    }

    public PdmlWriter writeTextNode (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag,
        @NotNull String unescapedText ) throws IOException {

        writeNodeStart ( nameSpacePrefix, unescapedTag, true );
        writeText ( unescapedText );
        return writeNodeEndChar();
    }

    @Override
    public PdmlWriter writeTextNodeOrEmptyNode (
        @NotNull String unescapedTag,
        @Nullable String unescapedText ) throws IOException {

        super.writeTextNodeOrEmptyNode ( unescapedTag, unescapedText );
        return this;
    }

    public PdmlWriter writeTextNodeOrEmptyNode (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag,
        @Nullable String unescapedText ) throws IOException {

        if ( isNonEmptyText ( unescapedText ) ) {
            return writeTextNode ( nameSpacePrefix, unescapedTag, unescapedText );
        } else {
            return writeEmptyNode ( nameSpacePrefix, unescapedTag );
        }
    }

    @Override
    public PdmlWriter writeTextNodeIfTextNotEmpty (
        @NotNull String unescapedTag,
        @Nullable String unescapedText ) throws IOException {

        super.writeTextNodeIfTextNotEmpty ( unescapedTag, unescapedText );
        return this;
    }

    public PdmlWriter writeTextNodeIfTextNotEmpty (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag,
        @Nullable String unescapedText ) throws IOException {

        if ( isNonEmptyText ( unescapedText ) ) {
            writeTextNode ( nameSpacePrefix, unescapedTag, unescapedText );
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
    public PdmlWriter writeNodeStartLine ( @NotNull String unescapedTag, boolean increaseIndent ) throws IOException {
        super.writeNodeStartLine ( unescapedTag, increaseIndent );
        return this;
    }

    public PdmlWriter writeNodeStartLine (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag,
        boolean increaseIndent ) throws IOException {

        writeIndent();
        writeNodeStartChar();
        writeNSAndTag ( nameSpacePrefix, unescapedTag );
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
    public PdmlWriter writeEmptyNodeLine ( @NotNull String unescapedTag ) throws IOException {
        super.writeEmptyNodeLine ( unescapedTag );
        return this;
    }

    public PdmlWriter writeEmptyNodeLine (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag ) throws IOException {

        writeIndent();
        writeEmptyNode ( nameSpacePrefix, unescapedTag );
        return writeLineBreak();
    }

    @Override
    public PdmlWriter writeTextLine ( @NotNull String unescapedText ) throws IOException {
        super.writeTextLine ( unescapedText );
        return this;
    }

    @Override
    public PdmlWriter writeTextNodeLine (
        @NotNull String unescapedTag,
        @NotNull String unescapedText ) throws IOException {

        super.writeTextNodeLine ( unescapedTag, unescapedText );
        return this;
    }

    public PdmlWriter writeTextNodeLine (
        @Nullable String nameSpacePrefix,
        @NotNull String unescapedTag,
        @NotNull String unescapedText ) throws IOException {

        writeIndent();
        writeTextNode ( nameSpacePrefix, unescapedTag, unescapedText );
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
        writeTag ( attributeName );
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
        return writeDoubleQuotedStringLiteral ( value );
    }

    public PdmlWriter writeUnquotedAttributeValue ( @NotNull String value ) throws IOException {
        return writeBareStringLiteral ( value );
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

        write ( PdmlExtensionsConstants.NAMESPACE_DECLARATIONS_END_CHAR );
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

    public PdmlWriter writeBlockComment ( @NotNull String comment ) throws IOException {

        // TODO add starts (*) if comment contains */, e.g. ^** */ **/
        write ( PdmlExtensionsConstants.BLOCK_COMMENT_EXTENSION_START );
        writeRaw ( comment );
        return write ( PdmlExtensionsConstants.BLOCK_COMMENT_END );
    }

    public PdmlWriter writeBlockCommentLine ( @NotNull String comment ) throws IOException {

        writeIndent();
        writeBlockComment ( comment );
        return writeLineBreak();
    }


    // String Literal

    public PdmlWriter writeDoubleQuotedStringLiteral ( @Nullable CharSequence value ) throws IOException {
        EscapeUtil.writeQuotedStringLiteral ( value, writer );
        return this;
    }

    /*
    public PdmlWriter writeNullableDoubleQuotedStringLiteral ( @Nullable CharSequence value ) throws IOException {
        // PdmlWriterUtil.writeNullableDoubleQuotedStringLiteral ( value, writer );
        EscapeUtil.writeAsQuotedStringLiteral ( value, writer );
        return this;
    }
     */

    public PdmlWriter writeBareStringLiteral ( @NotNull CharSequence value ) throws IOException {
        EscapeUtil.writeBareStringLiteral ( value, writer );
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
