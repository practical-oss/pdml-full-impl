package dev.ps.pdml.core.parser;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.range.*;
import dev.ps.shared.text.reader.util.ParsedString;
import dev.ps.pdml.core.reader.CorePdmlTokenReader;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.util.NonNullTextNode;
import dev.ps.pdml.data.util.NullTextNode;
import dev.ps.pdml.data.util.NullableTextNode;

import java.io.IOException;

public class CorePdmlParser {


    protected interface FailableSupplier<T> {
        @Nullable T get() throws IOException, PdmlException;
    }


    private final @NotNull CorePdmlTokenReader reader;
    public @NotNull CorePdmlTokenReader pdmlReader () { return reader; }

    private final boolean ignoreTextAfterRootNodeEnd;


    protected CorePdmlParser (
        @NotNull CorePdmlTokenReader reader,
        @NotNull CorePdmlParserConfig config ) {

        this.reader = reader;
        this.ignoreTextAfterRootNodeEnd = config.getIgnoreTextAfterRootNodeEnd();
    }

    public CorePdmlParser (
        @NotNull ReaderResource readerResource,
        @NotNull CorePdmlParserConfig config ) throws IOException {

        this ( new CorePdmlTokenReader ( readerResource ), config );
    }

    /*
    public CorePdmlParser (
        @NotNull Reader reader,
        @NotNull ReaderResource resource,
        @NotNull CorePdmlParserConfig config ) throws IOException {

        // this ( reader, resource, null, null, config );
        this ( new CorePdmlTokenReader ( reader, resource ), config );
    }
     */


    // Document

    public @Nullable TaggedNode parseDocument() throws IOException, PdmlException {

        skipWhitespaceBeforeRootNode();

        TaggedNode rootNode = parseTaggedNode();

        if ( ! ignoreTextAfterRootNodeEnd ) {
            requireDocumentEnd();
        }

        return rootNode;
    }

    public void skipWhitespaceBeforeRootNode() throws IOException {
        reader.skipWhitespace();
    }

    public void requireDocumentEnd() throws IOException, PdmlException {

        reader.skipWhitespace();
        if ( reader.isNotAtEnd() ) {
            throw abortingErrorAtCurrentPosition ( "No more text expected", "END_OF_PDML_DOCUMENT_EXPECTED" );
        }
    }


    // Tagged Node

    public @Nullable TaggedNode parseTaggedNode() throws IOException, PdmlException {

        long startOffset = reader.currentCodePointOffset();

        NodeTag tag = parseFromNodeStartToTag();
        if ( tag == null ) {
            return null;
        }

        TaggedNode taggedNode = new TaggedNode ( tag );

        if ( ! parseNodeEnd() ) {
            taggedNode.setSeparator ( requireSeparator() );
            requireChildNodes ( taggedNode );
            requireTaggedNodeEnd ( taggedNode );
        }

        taggedNode.setTextLocation ( createTextRange ( startOffset ) );

        return taggedNode;
    }


    // Node Elements

    public @Nullable NodeTag parseTag() throws IOException, PdmlException {

        ParsedString<?> parsedString = reader.readWithTextRange ( CorePdmlTokenReader::readTag );
        return parsedString != null
            ? NodeTag.create ( parsedString.string(), parsedString.source(), null )
            : null;
    }

    public @Nullable String parseSeparator() throws IOException {
        return reader.readSeparator();
    }

    public @Nullable TextLeaf parseTextLeaf() throws IOException, PdmlException {

        ParsedString<?> parsedText = reader.readWithTextRange ( CorePdmlTokenReader::readTextLeaf );
        return parsedText != null
            ? new TextLeaf ( parsedText.string(), parsedText.source() )
            : null;
    }

    protected void parseChildNodes ( TaggedNode parentNode ) throws IOException, PdmlException {

        while ( reader.isNotAtEnd() ) {

            if ( reader.isAtNodeEnd() ) {
                return;

            } else if ( reader.isAtNodeStart() ) {
                TaggedNode childNode = requireTaggedNode();
                parentNode.appendChild ( childNode );

            } else {
                TextLeaf textLeaf = requireTextLeaf();
                parentNode.appendChild ( textLeaf );
            }
        }
    }

    private boolean parseNodeEnd() throws IOException {
        return reader.skipNodeEnd();
    }

/*
    public void requireNodeEnd ( @NotNull NodeTag nodeTag ) throws IOException, MalformedPdmlException {

        if ( ! parseNodeEnd() ) {
            String message = "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "' to end node '" + nodeTag + "'";
            @Nullable TextRange sourceLocation = nodeTag.startLocation();
            if ( sourceLocation != null ) {
                message = message + " starting at " + sourceLocation.startLineColumn();
            }
            message = message + ".";

            throw abortingErrorAtCurrentPosition (
                message,
                "EXPECTING_NODE_END" );
        }
    }
 */

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



    // Convenience Methods

    public @Nullable NodeTag parseFromNodeStartToTag() throws IOException, PdmlException {

        if ( ! reader.skipNodeStart() ) return null;
        return requireTag();
    }

/*
    public @Nullable NodeTag parseFromNodeStartToSeparator() throws IOException, PdmlException {

        @Nullable NodeTag tag = parseFromNodeStartToTag();
        if ( tag == null ) {
            return null;
        }

        @Nullable TaggedNode taggedNode = parseTaggedNodeStartAndTag();
        if ( taggedNode == null ) {
            throw abortingErrorAtCurrentPosition ( "Node start required.", "NODE_START_REQUIRED" );
        }

        taggedNode.setSeparator ( requireSeparator() );

        return taggedNode;
    }
 */

/*
    // TODO parseFromNodeStartToTag
    public @Nullable TaggedNode parseTaggedNodeStartAndTag() throws IOException, PdmlException {

        // TODO use range
        TextPosition startPosition = reader.currentTextPosition();
        if ( ! reader.skipNodeStart() ) return null;
        NodeTag tag = requireTag();
        return new TaggedNode ( tag, startPosition );
    }

    // TODO requireFromNodeStartToSeparator
    public @NotNull TaggedNode requireTaggedNodeStartAndTagAndSeparator() throws IOException, PdmlException {

        @Nullable TaggedNode taggedNode = parseTaggedNodeStartAndTag();
        if ( taggedNode == null ) {
            throw abortingErrorAtCurrentPosition ( "Node start required.", "NODE_START_REQUIRED" );
        }

        taggedNode.setSeparator ( requireSeparator() );

        return taggedNode;
    }
 */

    public @Nullable TaggedNode parseTaggedLeafNode() throws IOException, PdmlException {

        long startOffset = reader.currentCodePointOffset();

        NodeTag tag = parseFromNodeStartToTag();
        if ( tag == null ) {
            return null;
        }

        TaggedNode taggedNode = new TaggedNode ( tag );

        requireTaggedNodeEnd ( taggedNode );

        taggedNode.setTextLocation ( createTextRange ( startOffset ) );

        return taggedNode;
    }

    public @Nullable NullableTextNode parseTextNode() throws IOException, PdmlException {

        long startOffset = reader.currentCodePointOffset();

        NodeTag tag = parseFromNodeStartToTag();
        if ( tag == null ) {
            return null;
        }

        TaggedNode taggedNode = new TaggedNode ( tag );

        String separator = reader.readSeparator ();
        @Nullable TextLeaf textLeaf;
        if ( separator != null ) {
            // taggedNode.setSeparator ( requireSeparator() );
            textLeaf = requireTextLeaf();
        } else {
            textLeaf = null;
        }

        requireTaggedNodeEnd ( taggedNode );

        TextRange textRange = createTextRange ( startOffset );
        return textLeaf != null
            ? new NonNullTextNode ( taggedNode.getTag(), textLeaf, textRange )
            : new NullTextNode ( taggedNode.getTag(), textRange );
    }


    // requireXXX Methods

    public @NotNull TaggedNode requireDocument() throws IOException, PdmlException {
        return require (
            this::parseDocument,
            "Root node required (e.g. [root ...])",
            "ROOT_NODE_REQUIRED" );
    }

    public @NotNull TaggedNode requireTaggedNode() throws IOException, PdmlException {
        return require ( this::parseTaggedNode, "Tagged node required.", "TAGGED_NODE_REQUIRED" );
    }

    public @NotNull NodeTag requireTag() throws IOException, PdmlException {
        return require ( this::parseTag, "Node tag required.", "NODE_TAG_REQUIRED" );
    }

    public @NotNull String requireSeparator() throws IOException, PdmlException {
        return require (
            this::parseSeparator,
            "A tag/value separator is required (e.g. a space).",
            "SEPARATOR_REQUIRED" );
    }

    public @NotNull TextLeaf requireTextLeaf() throws IOException, PdmlException {
        return require ( this::parseTextLeaf, "Text required", "TEXT_REQUIRED" );
    }

    // protected @NotNull ChildNodes requireChildNodes ( TaggedNode parentNode ) throws IOException, PdmlException {
    protected void requireChildNodes ( TaggedNode parentNode ) throws IOException, PdmlException {

        // return require ( () -> parseChildNodes ( parentNode ), "Child nodes required", "CHILD_NODES_REQUIRED" );
        parseChildNodes ( parentNode );
        if ( parentNode.isEmpty() ) {
            throw abortingErrorAtCurrentPosition ( "Child nodes required", "CHILD_NODES_REQUIRED" );
        }
    }

    /*
    public void requireNodeEnd() throws IOException, MalformedPdmlException {

        if ( ! parseNodeEnd() ) {
            throw abortingErrorAtCurrentPosition (
                "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "' to close the node.",
                "EXPECTING_NODE_END" );
        }
    }
     */

    /*
    public boolean parseTaggedNodeEnd ( @NotNull TaggedNode taggedNode ) throws IOException {

        TextPosition position = reader.currentTextPosition();
        if ( reader.skipNodeEnd () ) {
            taggedNode.setEndPosition ( position );
            return true;
        } else {
            return false;
        }
    }
         */

    public void requireTaggedNodeEnd ( @NotNull TaggedNode taggedNode ) throws IOException, MalformedPdmlException {

        if ( ! parseNodeEnd() ) {
            String message = "Expecting '" + CorePdmlConstants.NODE_END_CHAR + "' to close node '" + taggedNode.getTag() + "'";
            @Nullable TextRange nodeLocation = taggedNode.getTag().startLocation();
            if ( nodeLocation != null ) {
                message = message + " at " + nodeLocation.startLineColumn();
            }
            message = message + ".";

            throw abortingErrorAtCurrentPosition (
                message,
                "EXPECTING_NODE_END" );
        }
    }

    public @NotNull NodeTag requireFromNodeStartToTag() throws IOException, PdmlException {
        return require ( this::parseFromNodeStartToTag, "Node start required.", "NODE_START_REQUIRED" );
/*
        @Nullable NodeTag tag = parseFromNodeStartToTag ();
        if ( tag == null ) {
            throw malformedErrorAtCurrentPosition ( "Node start required.", "NODE_START_REQUIRED" );
        }
        return tag;
 */
    }

    public @NotNull TaggedNode requireTaggedLeafNode() throws IOException, PdmlException {
        return require (
            this::parseTaggedLeafNode,
            "Tagged leaf node (empty node) required.",
            "TAGGED_LEAF_NODE_REQUIRED" );
    }

    public @NotNull NullableTextNode requireTextNode() throws IOException, PdmlException {
        return require (
            this::parseTextNode, "Text node required.", "TEXT_NODE_REQUIRED" );
    }

    protected <T> @NotNull T require (
        @NotNull FailableSupplier<T> supplier,
        @NotNull String errorMessage,
        @NotNull String errorId ) throws IOException, PdmlException {

        T o = supplier.get();
        if ( o != null ) {
            return o;
        } else {
            throw abortingErrorAtCurrentPosition ( errorMessage, errorId );
        }
    }

    protected @NotNull FromToTextRange createTextRange  ( long startOffset ) {
        return new FromToTextRangeWithCodePointOffsets (
            reader.currentResource (), startOffset, reader.currentCodePointOffset (), null );
    }


    // Error Handling

    private MalformedPdmlException abortingErrorAtCurrentPosition (
        @NotNull String message, @NotNull String id ) {

        return new MalformedPdmlException ( message, id, reader.currentTextPosition() );
    }
}
