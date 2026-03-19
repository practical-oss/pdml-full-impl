package dev.ps.pdml.core.parser;

import dev.ps.shared.text.range.TextRange;
import dev.ps.shared.text.range.TextPosition;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.core.reader.CorePdmlReader_OLD;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.pdml.data.util.NullTextNode;
import dev.ps.pdml.data.util.NonNullTextNode;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pdml.data.util.NullableTextNode;

import java.io.IOException;
import java.io.Reader;

public class CorePdmlParser_OLD {


    private final @NotNull CorePdmlReader_OLD reader;
    // TODO? public @NotNull CorePdmlReader reader() { return reader; }
    private final boolean ignoreTextAfterRootNodeEnd;


    protected CorePdmlParser_OLD (
        @NotNull CorePdmlReader_OLD reader,
        @NotNull CorePdmlParserConfig config ) {

        this.reader = reader;
        this.ignoreTextAfterRootNodeEnd = config.getIgnoreTextAfterRootNodeEnd();
    }

    public CorePdmlParser_OLD (
        @NotNull Reader reader,
        @NotNull ReaderResource resource,
        @NotNull CorePdmlParserConfig config ) throws IOException {

        // this ( reader, resource, null, null, config );
        this ( new CorePdmlReader_OLD ( reader, resource ), config );
    }


    // Root Node

    public @Nullable TaggedNode parseRootNode() throws IOException, PdmlException {

        skipWhitespaceBeforeRootNode();

        TaggedNode rootNode = parseTaggedNode ();
        // if ( rootNode == null )  return null;

        if ( ! ignoreTextAfterRootNodeEnd ) {
            requireDocumentEnd();
        }

        return rootNode;
    }

    public @NotNull TaggedNode requireRootNode() throws IOException, PdmlException {

        TaggedNode rootNode = parseRootNode();
        if ( rootNode != null ) {
            return rootNode;
        } else {
            throw errorAtCurrentPosition (
                "Root node required (e.g. \"[root ...]\")", "ROOT_NODE_REQUIRED" );
        }
    }

    public void skipWhitespaceBeforeRootNode() throws IOException {
        skipWhitespace ();
    }

    public void requireDocumentEnd() throws IOException, PdmlException {

        skipWhitespace ();
        if ( reader.isNotAtEnd() ) {
            throw errorAtCurrentPosition ( "No more text expected", "END_OF_DOCUMENT_EXPECTED" );
        }
    }


    // Tagged Node

    public @Nullable TaggedNode parseTaggedNode() throws IOException, PdmlException {

        @Nullable TaggedNode taggedNode = parseTaggedNodeStartAndTag ();
        if ( taggedNode == null ) {
            return null;
        }

        if ( parseTaggedNodeEnd ( taggedNode ) ) {
            // empty node
            return taggedNode;
        }

        taggedNode.setSeparator ( requireSeparator() );
        requireChildNodes ( taggedNode );
        requireTaggedNodeEnd ( taggedNode );

        return taggedNode;
    }

    public @NotNull TaggedNode requireTaggedNode() throws IOException, PdmlException {

        TaggedNode taggedNode = parseTaggedNode();
        if ( taggedNode != null ) {
            return taggedNode;
        } else {
            throw errorAtCurrentPosition (
                "Tagged node required.", "TAGGED_NODE_REQUIRED" );
        }
    }

    public @Nullable TaggedNode parseTaggedNodeStartAndTag() throws IOException, PdmlException {

        TextPosition startPosition = reader.currentPosition();
        if ( ! reader.readNodeStart() ) return null;
        NodeTag tag = requireTag();
        // return new TaggedNode ( tag, startPosition );
        return new TaggedNode ( tag );
    }

    public @NotNull TaggedNode requireNodeStartAndTagAndSeparator() throws IOException, PdmlException {

        @Nullable TaggedNode taggedNode = parseTaggedNodeStartAndTag();
        if ( taggedNode == null ) {
            throw errorAtCurrentPosition ( "Node start required.", "NODE_START_REQUIRED" );
        }

        taggedNode.setSeparator ( requireSeparator() );

        return taggedNode;
    }


    // Tag

    public @Nullable NodeTag parseTag() throws IOException, PdmlException {

        TextRange tagPosition = reader.currentPosition();
        String tag = reader.readTagAsString ();
        if ( tag == null ) {
            return null;
        } else {
            return NodeTag.create ( tag, tagPosition, null );
        }
    }

    public @NotNull NodeTag requireTag() throws IOException, PdmlException {

        @Nullable NodeTag tag = parseTag();
        if ( tag != null ) {
            return tag;
        } else {
            throw errorAtCurrentPosition ( "Node tag required.", "NODE_TAG_REQUIRED" );
        }
    }


    // Separator

    public @NotNull String requireSeparator() throws IOException, PdmlException {

        @Nullable String separator = reader.readSeparator();
        if ( separator != null ) {
            return separator;
        } else {
            throw errorAtCurrentPosition (
                "A tag/value separator is required.", "SEPARATOR_REQUIRED" );
        }
    }


    // Child Nodes

    protected void parseChildNodes ( TaggedNode parentNode ) throws IOException, PdmlException {

        while ( reader.isNotAtEnd() ) {

            if ( reader.isAtNodeEnd() ) {
                return;

            } else if ( reader.isAtNodeStart() ) {
                TaggedNode childNode = requireTaggedNode ();
                parentNode.appendChild ( childNode );

            } else {
                TextLeaf textLeaf = requireTextLeaf ();
                parentNode.appendChild ( textLeaf );
            }
        }
    }

    protected void requireChildNodes ( TaggedNode parentNode ) throws IOException, PdmlException {

        parseChildNodes ( parentNode );
        if ( parentNode.isEmpty() ) {
            throw errorAtCurrentPosition ( "Child nodes required", "CHILD_NODES_REQUIRED" );
        }
    }


    // Node End

    public void requireNodeEnd() throws IOException, MalformedPdmlException {

        if ( ! reader.readNodeEnd() ) {
            throw errorAtCurrentPosition (
                "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "' to close the node.",
                "EXPECTING_NODE_END" );
        }
    }

    public boolean parseTaggedNodeEnd ( @NotNull TaggedNode taggedNode ) throws IOException {

        TextPosition position = reader.currentPosition();
        if ( reader.readNodeEnd() ) {
            // taggedNode.setEndPosition ( position );
            return true;
        } else {
            return false;
        }
    }

    public void requireTaggedNodeEnd ( @NotNull TaggedNode taggedNode ) throws IOException, MalformedPdmlException {

        if ( ! parseTaggedNodeEnd ( taggedNode ) ) {
            String message = "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "' to close node '" + taggedNode.getTag() + "'";
            @Nullable TextRange nodeLocation = taggedNode.getTag().startLocation();
            if ( nodeLocation != null ) {
                message = message + " at " + nodeLocation.startLineColumn ();
            }
            message = message + ".";

            throw errorAtCurrentPosition (
                message,
                "EXPECTING_NODE_END" );
        }
    }

    /*
    public void requireNodeEnd ( @NotNull NodeTag startTag ) throws IOException, MalformedPdmlException {

        if ( ! reader.readNodeEnd() ) {
            String message = "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "' to close node '" + startTag + "'";
            @Nullable TextPositionImpl position = startTag.startPosition();
            if ( position != null ) {
                message = message + " at position: " + position;
            }
            message = message + ".";

            throw errorAtCurrentPosition (
                message,
                "EXPECTING_NODE_END" );
        }
    }
     */


    // Text Leaf

    public @Nullable TextLeaf parseTextLeaf() throws IOException, PdmlException {

        @Nullable TextPosition position = reader.currentPosition();
        @Nullable String text = reader.readTextAsString ();
        return text == null ? null : new TextLeaf ( text, position );
    }

    public @NotNull TextLeaf requireTextLeaf() throws IOException, PdmlException {

        TextLeaf textLeaf = parseTextLeaf();
        if ( textLeaf != null ) {
            return textLeaf;
        } else {
            throw errorAtCurrentPosition ( "Text required", "TEXT_REQUIRED" );
        }
    }


    // Convenience Methods

    public @Nullable NullableTextNode parseTextNode() throws IOException, PdmlException {

        @NotNull TextRange startPosition = reader.currentPosition();
        @Nullable TaggedNode taggedNode = parseTaggedNodeStartAndTag();
        if ( taggedNode == null ) {
            return null;
        }

        String separator = reader.readSeparator();
        @Nullable TextLeaf textLeaf;
        if ( separator != null ) {
            // taggedNode.setSeparator ( requireSeparator() );
            textLeaf = requireTextLeaf();
        } else {
            textLeaf = null;
        }

        requireTaggedNodeEnd ( taggedNode );

        return textLeaf != null
            ? new NonNullTextNode ( taggedNode.getTag(), textLeaf, startPosition )
            : new NullTextNode ( taggedNode.getTag(), startPosition );
    }

    public @NotNull NullableTextNode requireTextNode() throws IOException, PdmlException {

        @Nullable NullableTextNode textNode = parseTextNode();
        if ( textNode != null ) {
            return textNode;
        } else {
            throw errorAtCurrentPosition ( "Text node required.", "TEXT_NODE_REQUIRED" );
        }
    }

    public @Nullable TaggedNode parseTaggedLeafNode() throws IOException, PdmlException {

        @Nullable TaggedNode taggedNode = parseTaggedNodeStartAndTag ();
        if ( taggedNode == null ) {
            return null;
        }

        requireTaggedNodeEnd ( taggedNode );

        return taggedNode;
    }

    public @NotNull TaggedNode requireTaggedLeafNode() throws IOException, PdmlException {

        @Nullable TaggedNode emptyNode = parseTaggedLeafNode ();
        if ( emptyNode != null ) {
            return emptyNode;
        } else {
            throw errorAtCurrentPosition ( "Empty node required.", "EMPTY_NODE_REQUIRED" );
        }
    }

    protected boolean skipWhitespace() throws IOException {
        return reader.skipWhitespace();
    }


    // Error Handling

    private MalformedPdmlException errorAtCurrentPosition (
        @NotNull String message, @NotNull String id ) {

        return new MalformedPdmlException ( message, id, reader.currentPosition() );
    }
}
