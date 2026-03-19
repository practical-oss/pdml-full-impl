package dev.ps.pdml.data.node.tagged;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.node.Node;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class ChildNodes implements Iterable<Node> {

    private final @NotNull List<Node> list;

    // Must not be public!
    // used only in 'TaggedNode' to append a node atomically
    @NotNull List<Node> getList() { return list; }


    public ChildNodes ( @NotNull List<Node> list ) {
        this.list = list;
    }

    public ChildNodes() {
        this ( new ArrayList<>() );
    }


    // Queries

    public int size() { return list.size(); }

    public boolean isEmpty() { return list.isEmpty(); }

    public boolean isNotEmpty() { return ! list.isEmpty(); }

    public @Nullable TextRange startLocation() {
        return ! isEmpty() ? first().getTextLocation () : null;
    }

    public @NotNull Node first() {

        // return list.getFirst(); } // >= Java 21
        if ( list.isEmpty() ) {
            throw new NoSuchElementException();
        } else {
            return list.get ( 0 );
        }
    }

    public @NotNull Node last() {

        // return list.getLast(); } // >= Java 21
        if ( list.isEmpty() ) {
            throw new NoSuchElementException();
        } else {
            return this.get ( this.size() - 1 );
        }
    }

    public @NotNull Node get ( int index ) { return list.get ( index ); }

    public boolean contains ( @NotNull Node child ) {
        return indexOfChild ( child ) != null;
    }

    public @Nullable Integer indexOfChild ( @NotNull Node child ) {

        for ( int i = 0; i < list.size(); i++ ) {
            if ( list.get ( i ) == child ) {
                return i;
            }
        }
        return null;
    }


    // Iteration

    public @NotNull Iterator<Node> iterator() { return list.iterator(); }

    public @NotNull Stream<Node> stream() { return list.stream(); }

    public @NotNull Stream<TaggedNode> taggedNodeStream() {

        return stream()
            .filter ( Node::isTaggedNode )
            .map ( node -> (TaggedNode) node );
    }

    public @NotNull Stream<TextLeaf> textLeafStream() {

        return stream()
            .filter ( Node::isTextLeaf )
            .map ( node -> (TextLeaf) node );
    }

    public @NotNull Stream<String> textStream() {
        return textLeafStream()
            .map ( TextLeaf::getText );
    }

    public @NotNull List<Node> list() {
        return Collections.unmodifiableList ( list );
    }


    // Get Child Nodes

    public @Nullable List<TaggedNode> childNodesByQualifiedTagOrNull ( @NotNull String qualifiedTag ) {

        List<TaggedNode> list = taggedNodeStream ()
            .filter ( tn -> tn.getTag ().qualifiedTag ().equals ( qualifiedTag ) )
            .toList();
        return list.isEmpty() ? null : list;
    }

    public @NotNull List<TaggedNode> childNodesByQualifiedTag ( @NotNull String qualifiedTag )
        throws InvalidPdmlDataException {

        @Nullable List<TaggedNode> list = childNodesByQualifiedTagOrNull ( qualifiedTag );
        if ( list != null ) {
            return list;
        } else {
            throw new InvalidPdmlDataException (
                "There are no child nodes with tag '" + qualifiedTag + "'.",
                "NO_CHILD_NODES_FOUND",
                startLocation() );
        }
    }

    public @Nullable TaggedNode uniqueChildByQualifiedTagOrNull ( @NotNull String qualifiedTag ) {

        try {
            return uniqueChildByQualifiedTag ( qualifiedTag );
        } catch ( InvalidPdmlDataException e ) {
            return null;
        }
    }

    public @NotNull TaggedNode uniqueChildByQualifiedTag ( @NotNull String qualifiedTag )
        throws InvalidPdmlDataException {

        @NotNull List<TaggedNode> list = childNodesByQualifiedTag ( qualifiedTag );
        if ( list.size() == 1 ) {
            return list.get(0);
        } else {
            throw new InvalidPdmlDataException (
                "There are " + list.size() + " child nodes with tag '" + qualifiedTag + "'.",
                "CHILD_NOT_UNIQUE",
                startLocation() );
        }
    }


    // toXXX

    public @Nullable List<String> toStringListOrNull (
        boolean allowNullElements ) throws InvalidPdmlDataException {

        List<String> stringList = new ArrayList<>();
        for ( Node node : list ) {
            String element = checkNodeForCollection ( node, allowNullElements );
            if ( node instanceof TaggedNode ) {
                stringList.add ( element );
            }
        }
        return stringList.isEmpty() ? null : stringList;
    }

    // TODO public @Nullable Set<String> toStringSetOrNull (

    public @Nullable Map<String, String> toStringMapOrNull (
        boolean allowNullValues ) throws InvalidPdmlDataException {

        Map<String,String> map = new HashMap<>();
        for ( Node node : list ) {
            String value = checkNodeForCollection ( node, allowNullValues );
            if ( node instanceof TaggedNode taggedNode ) {

                @NotNull String qualifiedTag = taggedNode.getTag().qualifiedTag();
                if ( map.containsKey ( qualifiedTag ) ) {
                    throw new InvalidPdmlDataException (
                        "Node '" + qualifiedTag + "' is not unique.",
                        "DUPLICATE_KEY",
                        taggedNode.getTextLocation () );
                }
                map.put ( qualifiedTag, value );
            }
        }
        return map.isEmpty() ? null : map;
    }

    /*
    @Deprecated
    public @Nullable Parameters<String> toStringParametersOrNull (
        boolean allowNullValues ) throws InvalidPdmlDataException {

        TextPositionOrRange por = isEmpty() ? null : first().getTextPositionOrRange();
        // var startToken = por != null ? por.toTextToken() : null;
        // TextToken startToken = TextToken.create ( por );
        // TextToken startToken = new TextToken ( "", por );
        TextToken startToken = null;
        if ( por != null ) {
            try {
                String text = por.text();
                if ( text == null ) text = "";
                startToken = new TextToken ( text, por.startPosition () );
            } catch ( IOException e ) {
                throw new RuntimeException ( e );
            }
        }
        MutableParameters<String> parameters = new MutableParameters<> ( startToken );
        for ( Node node : list ) {
            String value = checkNodeForCollection ( node, allowNullValues );
            if ( node instanceof TaggedNode taggedNode ) {

                @NotNull String qualifiedTag = taggedNode.getTag().qualifiedTag();
                if ( parameters.containsName ( qualifiedTag ) ) {
                    throw new InvalidPdmlDataException (
                        "Node '" + qualifiedTag + "' is not unique.",
                        "DUPLICATE_NAME",
                        taggedNode.getTextPositionOrRange() );
                }

                parameters.add ( qualifiedTag, value, null,
                    // taggedNode.getTag().readerResourcePosition (), taggedNode.getChildNodes ().startLocation () );
                    // taggedNode.getTag().readerResourcePosition(), null );
                    null, null );
            }
        }
        return parameters.copyToImmutableOrNull();
    }
     */

    private @Nullable String checkNodeForCollection (
        @NotNull Node node,
        boolean allowNullValues ) throws InvalidPdmlDataException {

        if ( node instanceof TaggedNode taggedNode ) {

            if ( taggedNode.hasNamespaceDefinitions() ) {
                throw new InvalidPdmlDataException (
                    "Namespace definitions are not allowed in this context.",
                    "NAMESPACES_NOT_ALLOWED",
                    taggedNode.getNamespaceDefinitions().getTextLocation () );
            }

            if ( taggedNode.hasAttributes() ) {
                throw new InvalidPdmlDataException (
                    "Attributes are not allowed in this context.",
                    "ATTRIBUTES_NOT_ALLOWED",
                    taggedNode.getStringAttributes().location() );
            }

            @Nullable String value = taggedNode.toTextOrNull();
            if ( value == null && ! allowNullValues ) {
                throw new InvalidPdmlDataException (
                    "Node '" + taggedNode.getTag () + "' is empty, but null values are not allowed.",
                    "NULL_VALUES_NOT_ALLOWED",
                    taggedNode.getTextLocation () );
            }

            return value;

        } else if ( node instanceof TextLeaf textLeaf ) {

            if ( ! textLeaf.getText().isBlank() ) {
                throw new InvalidPdmlDataException (
                    "Non-whitespace text between map entries is not allowed.",
                    "NON_WHITESPACE_TEXT_NOT_ALLOWED",
                    textLeaf.getTextLocation () );
            }
        }

        return null;
    }

    public @Nullable String concatenateTexts() {

        StringBuilder sb = new StringBuilder();
        textStream().forEach ( sb::append );
        return sb.isEmpty() ? null : sb.toString();
    }


    // Updates, NO, done in BranchNode


/*
    public @NotNull ChildNodes append ( @NotNull Node childNode, @NotNull BranchNode parentNode ) {

        // if ( childNode.getParent() != null ) {
        //     throw new IllegalStateException ( "Node '" + childNode + "' is already assigned to a parent node. It cannot be assigned to another parent node, unless it is first removed from the current parent." );
        // }

        // list.add ( childNode );
        // childNode.setParent ( parentNode );

        childNode.appendToChildNodes ( list, parentNode );
        return this;
    }

    public @NotNull ChildNodes appendTextChild (
        @NotNull String childName,
        @NotNull String text,
        @NotNull BranchNode parentNode ) {

        return append ( new BranchNode ( childName ).appendText ( text ), parentNode );
    }

    public @NotNull ChildNodes appendText ( @NotNull String text, @NotNull BranchNode parentNode ) {
        return append ( new TextNode ( text ), parentNode );
    }
 */



    @Override
    public @NotNull String toString() {
        return list.toString();
    }
}
