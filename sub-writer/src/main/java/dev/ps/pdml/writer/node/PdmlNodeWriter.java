package dev.ps.pdml.writer.node;

import dev.ps.pdml.data.namespace.NodeNamespaces;
import dev.ps.pdml.data.namespace.NodeNamespace;
import dev.ps.pdml.data.node.Node;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.tagged.ChildNodes;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.pdml.writer.PdmlWriter;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.prt.argument.StringArgument;
import dev.ps.prt.argument.StringArguments;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public class PdmlNodeWriter implements Flushable, Closeable {

    private final @NotNull PdmlWriter writer;
    @NotNull public PdmlWriter getPdmlWriter() { return writer; }

    private final @NotNull PdmlNodeWriterConfig config;
    @NotNull public PdmlNodeWriterConfig getConfig() { return config; }


    public PdmlNodeWriter (
        @NotNull Writer writer,
        @NotNull PdmlNodeWriterConfig config ) {

        this.writer = new PdmlWriter ( writer, config );
        this.config = config;
    }

    public PdmlNodeWriter ( @NotNull Writer writer ) {
        this ( writer, PdmlNodeWriterConfig.DEFAULT_CONFIG );
    }


    public @NotNull PdmlNodeWriter writeRootNode ( @NotNull TaggedNode rootNode ) throws IOException {
        writeTaggedNode ( rootNode );
        // writer.writeLineBreak();
        return this;
    }

    public @NotNull PdmlNodeWriter writeTaggedNode ( @NotNull TaggedNode taggedNode ) throws IOException {

        /*
        writer.writeNodeStartChar();

        writeNodeName ( taggedNode.getName() );
        if ( taggedNode.isNotEmpty() ) {
            writer.writeSpaceSeparator();
        }

        writeNodeNamespaces ( taggedNode );
        writeNodeAttributes ( taggedNode );
         */
        writeNodeStart ( taggedNode );
        // writeNodeStart (
        //    taggedNode.getName(), taggedNode.getNamespaceDefinitions(), taggedNode.getAttributes(), taggedNode.isEmpty(), taggedNode.hasChildNodes() );
        writeChildNodes ( taggedNode.getChildNodes() );
        writeNodeEnd();

        return this;
    }

    public @NotNull PdmlNodeWriter writeTaggedNodeLines ( @NotNull TaggedNode taggedNode ) throws IOException {

        /*
        writer.writeNodeStartChar();

        writeNodeName ( taggedNode.getName() );
        if ( taggedNode.isNotEmpty() ) {
            writer.writeSpaceSeparator();
        }

        writeNodeNamespaces ( taggedNode );
        writeNodeAttributes ( taggedNode );
         */
        if ( taggedNode.isEmpty() ) {
            writer.writeIndent();
            writer.writeNodeStartChar();
            writeNodeName ( taggedNode.getTag () );
            writer.writeNodeEndChar();
            writer.writeLineBreak();
        } else {
            writeNodeStartLine (
                taggedNode.getTag (), taggedNode.getNamespaceDefinitions (), taggedNode.getStringAttributes () );
            writeChildNodesLines ( taggedNode.getChildNodes () );
            writeNodeEndLine ();
        }

        return this;
    }

    /*
    private @NotNull PdmlNodeWriter writeNodeStart ( @NotNull TaggedNode taggedNode ) throws IOException {
        return writeNodeStart (
            taggedNode.getName(), taggedNode.getNamespaceDefinitions(), taggedNode.getAttributes(), taggedNode.isEmpty(), taggedNode.hasChildNodes() );
    }
     */

    public @NotNull PdmlNodeWriter writeNodeStart ( @NotNull TaggedNode taggedNode ) throws IOException {

        String separator = taggedNode.getSeparator();
        if ( separator == null && taggedNode.isNotEmpty() ) {
            separator = " ";
        }
        writeNodeStart (
            taggedNode.getTag (), separator,
            taggedNode.getNamespaceDefinitions(), taggedNode.getStringAttributes (), taggedNode.isEmpty(), taggedNode.hasChildNodes() );
        return this;
    }

    public @NotNull PdmlNodeWriter writeNodeStart (
        @NotNull NodeTag name,
        @Nullable String separator,
        @Nullable NodeNamespaces namespaces,
        @Nullable StringArguments attributes,
        boolean hasChildNodes ) throws IOException {

        /*
        writer.writeNodeStartChar();

        writeNodeName ( tag );
        if ( ! isEmptyNode ) {
            writer.writeSpaceSeparator();
        }

        if ( namespaces != null ) writeNamespaces ( namespaces, attributes.isNotEmpty() || hasChildNodes );
        if ( attributes != null ) writeAttributes ( attributes, hasChildNodes );
         */

        // boolean hasAttributes = attributes != null && attributes.isNotEmpty();
        boolean hasAttributes = attributes != null;
        writeNodeStart ( name, separator, namespaces, attributes,
            hasAttributes || hasChildNodes, hasChildNodes );

        return this;
    }

    public @NotNull PdmlNodeWriter writeNodeStartLine (
        @NotNull NodeTag name,
        @Nullable NodeNamespaces namespaces,
        @Nullable StringArguments attributes ) throws IOException {

        writer.writeIndent();
        boolean hasNamespaces = namespaces != null && namespaces.isNotEmpty();
        boolean hasAttributes = attributes != null; // && attributes.isNotEmpty();
        writeNodeStart ( name,
            hasNamespaces || hasAttributes ? " " : null,
            namespaces, attributes,
            hasAttributes, false );
        writer.writeLineBreak();
        writer.increaseIndent();
        return this;
    }

/*
    private @NotNull PdmlNodeWriter writeNodeStart (
        @NotNull NodeName tag,
        @Nullable NodeNamespaces namespaces,
        @Nullable StringArguments attributes,
        boolean appendSpaceAfterName,
        boolean appendSpaceAfterNamespaces,
        boolean appendSpaceAfterAttributes ) throws IOException {

        writer.writeNodeStartChar();

        writeNodeName ( tag );
        if ( appendSpaceAfterName ) {
            writer.writeSpaceSeparator();
        }

        if ( namespaces != null ) writeNamespaces ( namespaces, appendSpaceAfterNamespaces );
        if ( attributes != null ) writeAttributes ( attributes, appendSpaceAfterAttributes );

        return this;
    }
 */

    public @NotNull PdmlNodeWriter writeNodeStart (
        @NotNull NodeTag name,
        @Nullable String separator,
        @Nullable NodeNamespaces namespaces,
        @Nullable StringArguments attributes,
        boolean appendSpaceAfterNamespaces,
        boolean appendSpaceAfterAttributes ) throws IOException {

        writer.writeNodeStartChar();

        writeNodeName ( name );

        if ( separator != null ) {
            writer.writeRaw ( separator );
        }

        if ( namespaces != null ) writeNamespaces ( namespaces, appendSpaceAfterNamespaces );
        if ( attributes != null ) writeAttributes ( attributes, appendSpaceAfterAttributes );

        return this;
    }

    public @NotNull PdmlNodeWriter writeNodeEnd() throws IOException {
        writer.writeNodeEndChar();
        return this;
    }

    public @NotNull PdmlNodeWriter writeNodeEndLine() throws IOException {
        writer.writeNodeEndLine ( true );
        return this;
    }

    public @NotNull PdmlNodeWriter writeNodeName (
        @NotNull NodeTag name ) throws IOException {

        writer.writeNodeName ( name.namespacePrefix(), name.tag () );
        return this;
    }

    /*
    public @NotNull PdmlNodeWriter writeNodeChildNodes ( @NotNull TaggedNode taggedNode ) throws IOException {
        return writeChildNodes ( taggedNode.getChildNodes() );
    }

     */

    public @NotNull PdmlNodeWriter writeChildNodes ( @NotNull ChildNodes childNodes ) throws IOException {

        for ( Node childNode : childNodes ) {
            writeChildNode ( childNode );
        }
        return this;
    }

    public @NotNull PdmlNodeWriter writeChildNodesLines ( @NotNull ChildNodes childNodes ) throws IOException {

        for ( Node childNode : childNodes ) {
            writeChildNodeLine ( childNode );
        }
        return this;
    }

    public @NotNull PdmlNodeWriter writeChildNode ( @NotNull Node childNode ) throws IOException {

        /* Use this code when this is no more a preview feature in Java
        switch ( childNode ) {
            case MutableTaggedNode taggedNode -> writeRootOrTaggedNode ( taggedNode );
            case MutableTextNode textNode -> writeTextNode ( textNode );
            case MutableCommentNode commentNode -> writeCommentNode ( commentNode );
            default -> throw new IllegalStateException ( "Unexpected value: " + childNode );
        }
         */

        if ( childNode instanceof TaggedNode taggedNode ) {
            writeTaggedNode ( taggedNode );
        } else if ( childNode instanceof TextLeaf textLeaf ) {
            writeTextLeaf ( textLeaf );
        } else if ( childNode instanceof CommentLeaf commentLeaf ) {
            writeCommentLeaf ( commentLeaf );
        } else {
            throw new IllegalStateException ( "Unexpected value: " + childNode );
        }

        return this;
    }

    public @NotNull PdmlNodeWriter writeChildNodeLine ( @NotNull Node childNode ) throws IOException {

        /* Use this code when this is no more a preview feature in Java
        switch ( childNode ) {
            case MutableTaggedNode taggedNode -> writeRootOrTaggedNode ( taggedNode );
            case MutableTextNode textNode -> writeTextNode ( textNode );
            case MutableCommentNode commentNode -> writeCommentNode ( commentNode );
            default -> throw new IllegalStateException ( "Unexpected value: " + childNode );
        }
         */

        if ( childNode instanceof TaggedNode taggedNode ) {

            // @Nullable TextLeaf textChild = taggedNode.toTextLeafOrNull();
            @Nullable TextLeaf textChild = null;
            if ( taggedNode.getChildNodes().size() == 1 ) {
                if ( taggedNode.getChildNodes().first() instanceof TextLeaf tl ) {
                    textChild = tl;
                }
            }

            if ( textChild != null ) {
                // special case for node containing only text
                writer.writeIndent();
                writeNodeStart ( taggedNode );
                writeTextLeaf ( textChild );
                writeNodeEnd();
                writer.writeLineBreak();
            } else {
                writeTaggedNodeLines ( taggedNode );
            }
        } else if ( childNode instanceof TextLeaf textLeaf ) {
            writeTextLeafLine ( textLeaf );
            // writeTextLeaf ( textLeaf );
        } else if ( childNode instanceof CommentLeaf commentLeaf ) {
            writeCommentLeafLine ( commentLeaf );
            // writeCommentLeaf ( commentLeaf );
        } else {
            throw new IllegalStateException ( "Unexpected value: " + childNode );
        }

        return this;
    }

    public @NotNull PdmlNodeWriter writeTextLeaf ( @NotNull TextLeaf textNode ) throws IOException {
        writer.writeText ( textNode.getText() );
        return this;
    }

    public @NotNull PdmlNodeWriter writeTextLeafLine ( @NotNull TextLeaf textNode ) throws IOException {
        writer.writeTextLine ( textNode.getText() );
        return this;
    }

    public @NotNull PdmlNodeWriter writeCommentLeaf ( @NotNull CommentLeaf commentNode ) throws IOException {
        writer.writeMultilineComment ( commentNode.getText() );
        return this;
    }

    public @NotNull PdmlNodeWriter writeCommentLeafLine ( @NotNull CommentLeaf commentNode ) throws IOException {
        writer.writeMultilineCommentLine ( commentNode.getText() );
        return this;
    }


    // Namespaces

    public @NotNull PdmlNodeWriter writeNodeNamespaces ( @NotNull TaggedNode node ) throws IOException {

        var namespaces = node.getNamespaceDefinitions();
        if ( namespaces == null ) return this;
        writeNamespaces ( namespaces, node.hasAttributes() || node.hasChildNodes() );

        return this;
    }

    public @NotNull PdmlNodeWriter writeNamespaces (
        @NotNull NodeNamespaces namespaces,
        boolean appendSpace ) throws IOException {

        Collection<NodeNamespace> list = namespaces.list();
        if ( list != null ) writeNamespaces ( list, appendSpace );

        return this;
    }

    private void writeNamespaces (
        @NotNull Collection<NodeNamespace> namespaces,
        boolean appendSpace ) throws IOException {

        writer.writeNamespacesStart ( false );
        for ( NodeNamespace namespace : namespaces ) {
            writer.writeNamespacesSeparator();
            writeNamespace ( namespace );
        }
        writer.writeNamespacesEnd ( appendSpace );
    }

    public @NotNull PdmlNodeWriter writeNamespace ( @NotNull NodeNamespace namespace ) throws IOException {
        writer.writeNamespace ( namespace.namePrefix(), namespace.URI() );
        return this;
    }


    // Attributes

    public @NotNull PdmlNodeWriter writeNodeAttributes ( @NotNull TaggedNode taggedNode ) throws IOException {

        var attributes = taggedNode.getStringAttributes ();
        // if ( attributes == null ) return this;
        writeAttributes ( attributes, taggedNode.hasChildNodes() );

        return this;
    }

    public @NotNull PdmlNodeWriter writeAttributes (
        @Nullable StringArguments attributes,
        boolean appendSpace ) throws IOException {

        if ( attributes == null ) return this;

        writer.writeAttributesStart ( false );

        boolean isFirst = true;
        for ( StringArgument attribute : attributes.list() ) {
            if ( ! isFirst ) {
                writer.writeAttributesSeparator ();
            }
            writeAttribute ( attribute );
            isFirst = false;
        }

        writer.writeAttributesEnd ( appendSpace );
        return this;
    }

    public @NotNull PdmlNodeWriter writeAttribute ( @NotNull StringArgument attribute ) throws IOException {

        writer.writeAttribute (
            attribute.name(),
            attribute.value(),
            config.isWriteUnquotedAttributeValuesIfPossible() );
        return this;
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
