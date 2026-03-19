package dev.ps.pdml.data.node.tagged;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.exception.InvalidPdmlDataException;
import dev.ps.pdml.data.namespace.NodeNamespaces;
import dev.ps.pdml.data.namespace.NodeNamespace;
import dev.ps.pdml.data.node.Node;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.pdml.data.nodespec.PdmlNodeSpec;
import dev.ps.pdml.data.util.TreeIterator;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.argument.StringArguments;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TaggedNode extends Node implements Iterable<Node> {

    private @NotNull NodeTag tag;
    public @NotNull NodeTag getTag() { return tag; }
    public void setTag ( @NotNull NodeTag tag ) { this.tag = tag; }

    private @NotNull ChildNodes childNodes;
    public @NotNull ChildNodes getChildNodes() { return childNodes; }
    public void setChildNodes ( @NotNull ChildNodes childNodes ) { this.childNodes = childNodes; }

/*
    private @NotNull NodeAttributes stringAttributes;
    public @NotNull NodeAttributes getStringAttributes() { return stringAttributes; }
    public void setStringAttributes ( @NotNull NodeAttributes stringAttributes ) { this.stringAttributes = stringAttributes; }
 */

    private @Nullable StringArguments stringAttributes;
    public @Nullable StringArguments getStringAttributes() { return stringAttributes; }
    public void setStringAttributes ( @Nullable StringArguments stringAttributes ) { this.stringAttributes = stringAttributes; }

    private @Nullable Arguments typedAttributes;
    public @Nullable Arguments getTypedAttributes() { return typedAttributes; }
    public void setTypedAttributes ( @Nullable Arguments typedAttributes ) { this.typedAttributes = typedAttributes; }

    private @NotNull NodeNamespaces namespaceDefinitions;
    public @NotNull NodeNamespaces getNamespaceDefinitions() { return namespaceDefinitions; }
    public void setNamespaceDefinitions ( @NotNull NodeNamespaces namespaces ) { this.namespaceDefinitions = namespaces; }

/*
    protected @Nullable ReaderResourcePosition endLocation;
    public @Nullable ReaderResourcePosition getEndLocation() { return endLocation; }
    public void setEndLocation ( @Nullable ReaderResourcePosition endLocation ) { this.endLocation = endLocation; }
 */

 /*
    public @Nullable TextLineColumn endPosition() {
        return textLocation != null ? textLocation.endLineColumn () : null;
    }

    public void setEndPosition ( @Nullable TextPosition endPosition ) {
        // TODO
    }
*/

    private @Nullable PdmlNodeSpec spec;
    public @Nullable PdmlNodeSpec getSpec() { return spec; }
    public void setSpec ( @Nullable PdmlNodeSpec spec ) { this.spec = spec; }

    private @Nullable String separator;
    public @Nullable String getSeparator() { return separator; }
    public void setSeparator ( @Nullable String separator ) { this.separator = separator; }

    /**
     * If a type is defined for this node, then this field might contain
     * the Java Object returned by executing a 'parse' method of the type
     * example: Node 'birthdate' is of type 'local_date'
     * [birthdate 2000-01-01]
     * -> This field will contain an object of type 'java.time.LocalDate'
     */
    private @Nullable Object javaObjectContained;
    public @Nullable Object getJavaObjectContained() { return javaObjectContained; }
    @SuppressWarnings ( "unchecked" )
    public <T> @Nullable T getCastedJavaObjectContained() { return (T) javaObjectContained; }
    public void setJavaObjectContained ( @Nullable Object javaObjectContained ) { this.javaObjectContained = javaObjectContained; }


    public TaggedNode (
        @NotNull NodeTag tag,
        @Nullable ChildNodes childNodes,
        @Nullable StringArguments stringAttributes,
        @Nullable NodeNamespaces namespaceDefinitions,
        @Nullable PdmlNodeSpec spec,
        // @Nullable ReaderResourcePosition startLocation,
        // @Nullable ReaderResourcePosition endLocation ) {
        @Nullable TextRange textLocation ) {

        super ( textLocation );

        this.tag = tag;
        this.childNodes =
            childNodes == null ? new ChildNodes() : childNodes;
        this.stringAttributes = stringAttributes;
            // stringAttributes == null ? new NodeAttributes ( null ) : stringAttributes;
        this.typedAttributes = null;
        this.namespaceDefinitions =
            namespaceDefinitions == null ? new NodeNamespaces ( null ) : namespaceDefinitions;
        // this.endLocation = endLocation;
        this.spec = spec;
        this.separator = null;
        this.javaObjectContained = null;
    }

    /*
    public TaggedNode (
        @NotNull NodeTag tag,
        @Nullable TextRange textLocation ) {

        this ( tag, null, null, null, null, textLocation );
    }
     */

    public TaggedNode ( @NotNull NodeTag tag ) {
        this ( tag, null, null, null, null, null );
    }

    public TaggedNode (
        @NotNull String tag ) {

        this ( NodeTag.create ( tag ) );
    }


    // Queries

    public boolean isEmpty() { return ! isNotEmpty(); }
    public boolean isNotEmpty() { return hasChildNodes() || hasNamespaceDefinitions() || hasAttributes(); }

    public boolean isRootNode() { return parent == null; }

    public boolean isTaggedNode () { return true; }

    public boolean isLeafNode() { return hasNoChildNodes(); }

    public boolean isTextLeaf() { return false; }

    public boolean isCommentLeaf() { return false; }


    // Tag

    public @NotNull String qualifiedTag() { return tag.qualifiedTag(); }


    // Child Nodes Queries

    public boolean hasChildNodes() { return ! childNodes.isEmpty(); }

    public boolean hasNoChildNodes() { return childNodes.isEmpty(); }

    public int childNodesCount() { return childNodes.size(); }


    // Iterator

    public @NotNull Iterator<Node> iterator() {
        return treeIterator ( true );
    }

    public @NotNull Iterator<Node> treeIterator ( boolean includeRootNode ) {
        return new TreeIterator ( this, includeRootNode );
    }


    // Streams

    public @NotNull Stream<Node> treeNodeStream ( boolean includeRootNode ) {
        // return StreamSupport.stream ( this.spliterator(), false );
        Spliterator<Node> spliterator =
            Spliterators.spliteratorUnknownSize ( treeIterator ( includeRootNode), 0 );
        return StreamSupport.stream ( spliterator, false );
    }

    public @NotNull Stream<TaggedNode> treeTaggedNodeStream ( boolean includeRootNode ) {

        return treeNodeStream ( includeRootNode )
            .filter ( Node::isTaggedNode )
            .map ( node -> (TaggedNode) node );
    }

    public @NotNull Stream<TextLeaf> treeTextLeafStream() {

        return treeNodeStream ( true )
            .filter ( Node::isTextLeaf )
            .map ( node -> (TextLeaf) node );
    }

    public @NotNull Stream<String> treeTextStream() {
        return treeTextLeafStream()
            .map ( TextLeaf::getText );
    }

    public @NotNull Stream<NodeTag> treeNodeTagStream ( boolean includeRootNode ) {
        return treeTaggedNodeStream ( includeRootNode )
            .map ( taggedNode -> taggedNode.tag );
    }

    public @NotNull Stream<String> treeQualifiedTagStream ( boolean includeRootNode ) {
        return treeTaggedNodeStream ( includeRootNode )
            .map ( taggedNode -> taggedNode.tag.qualifiedTag () );
    }

    public @NotNull Stream<String> treeTagStream ( boolean includeRootNode ) {
        return treeTaggedNodeStream ( includeRootNode )
            .map ( taggedNode -> taggedNode.tag.tag () );
    }


    // Get Child

    public @NotNull TaggedNode child ( @NotNull String tag ) throws InvalidPdmlDataException {
        return childNodes.uniqueChildByQualifiedTag ( tag );
    }

    public @Nullable TaggedNode childOrNull ( @NotNull String tag ) {
        return childNodes.uniqueChildByQualifiedTagOrNull ( tag );
    }

    public @NotNull Node childAt ( int index ) {
        return childNodes.get ( index );
    }

    public @Nullable Node getSingletonChildOrNull() {

        if ( childNodes.size() != 1 ) return null;
        return childNodes.first();
    }

    public @NotNull Node getSingletonChild() throws InvalidPdmlDataException {

        Node result = getSingletonChildOrNull ();
        if ( result != null ) {
            return result;
        } else {
            String message = "The content of node '" + tag + "' is not a singleton because ";
            if ( hasChildNodes() ) {
                message = message + "it contains " + childNodes.size() + " child nodes.";
            } else {
                message = message + "it doesn't contain child nodes.";
            }
            throw new InvalidPdmlDataException (
                message,
                "SINGLETON_REQUIRED",
                textLocation );
        }
    }

    // This method is used only in PDPP
    @Deprecated
    public @Nullable String childAsTextOrNull ( @NotNull String childTag ) {

        @Nullable TaggedNode child = childOrNull ( childTag );
        if ( child == null ) return null;

        try {
            return child.toText();
        } catch ( InvalidPdmlDataException e ) {
            return null;
        }
    }


    // toXXX

    public @NotNull TextLeaf toTextLeaf() throws InvalidPdmlDataException {

        @NotNull Node singletonChild = getSingletonChild();
        if ( singletonChild instanceof TextLeaf textLeaf ) {
            return textLeaf;
        } else {
            throw new InvalidPdmlDataException (
                "Node '" + tag + "' doesn't contain a single text.",
                "SINGLETON_TEXT_REQUIRED",
                textLocation );
        }
    }

    public @Nullable TextLeaf toTextLeafOrNull() throws InvalidPdmlDataException {

        if ( ! hasChildNodes() ) {
            return null;
        } else {
            return toTextLeaf();
        }
    }

    /*
    public boolean containsSingletonText() {
        if ( childNodes.size() != 1 ) return false;
        return childNodes.first() instanceof TextLeaf;
    }
     */

    public @NotNull String toText() throws InvalidPdmlDataException {
        return toTextLeaf().getText();
    }

    public @Nullable String toTextOrNull() throws InvalidPdmlDataException {

        @Nullable TextLeaf textLeaf = toTextLeafOrNull();
        return textLeaf != null ? textLeaf.getText() : null;
    }

    public @NotNull String toTextOrDefault ( @NotNull String defaultText ) throws InvalidPdmlDataException {

        if ( hasChildNodes() ) {
            return toText();
        } else {
            return defaultText;
        }
    }

    public int toInt() throws InvalidPdmlDataException {
        return toTextLeaf().toInt();
    }

    public @Nullable Integer ToIntegerOrNull() throws InvalidPdmlDataException {

        if ( hasChildNodes() ) {
            return toInt();
        } else {
            return null;
        }
    }

    public int ToIntOrDefault ( int defaultInt ) throws InvalidPdmlDataException {

        if ( hasChildNodes() ) {
            return toInt();
        } else {
            return defaultInt;
        }
    }

    public @Nullable List<String> toStringListOrNull (
        boolean allowNullElements ) throws InvalidPdmlDataException {

        return childNodes.toStringListOrNull ( allowNullElements );
    }

    public @NotNull List<String> toStringList (
        boolean allowNullElements ) throws InvalidPdmlDataException {

        @Nullable List<String> list = toStringListOrNull ( allowNullElements );
        if ( list != null ) {
            return list;
        } else {
            throw new InvalidPdmlDataException (
                "There are no list elements defined in node '" + tag + "'.",
                "LIST_ELEMENTS_REQUIRED",
                textLocation );
        }
    }

    public @Nullable Map<String, String> toStringMapOrNull (
        boolean allowNullValues ) throws InvalidPdmlDataException {

        return childNodes.toStringMapOrNull ( allowNullValues );
    }

    public @NotNull Map<String, String> toStringMap (
        boolean allowNullValues ) throws InvalidPdmlDataException {

        @Nullable Map<String, String> map = toStringMapOrNull ( allowNullValues );
        if ( map != null ) {
            return map;
        } else {
            throw new InvalidPdmlDataException (
                "There are no map entries defined in node '" + tag + "'.",
                "MAP_ENTRIES_REQUIRED",
                textLocation );
        }
    }

    /*
    @Deprecated
    public @Nullable Parameters<String> toStringParametersOrNull (
        boolean allowNullValues ) throws InvalidPdmlDataException {

        return childNodes.toStringParametersOrNull ( allowNullValues );
    }
     */

    /*
    public @NotNull Parameters<String> toStringParameters (
        boolean allowNullValues ) throws InvalidPdmlDataException {

        @Nullable Parameters<String> parameters = toStringParametersOrNull ( allowNullValues );
        if ( parameters != null ) {
            return parameters;
        } else {
            throw new InvalidPdmlDataException (
                "There are no string parameters defined in node '" + tag + "'.",
                "PARAMETERS_REQUIRED",
                textLocation );
        }
    }
     */

    // TODO add more types (boolean, all numbers, char, date, time, set, bytes, ...)


    public @Nullable String concatenateTreeTexts() {

        StringBuilder sb = new StringBuilder();
        treeTextStream().forEach ( sb::append );
        return sb.isEmpty() ? null : sb.toString();
    }


    // Child Nodes Updates

    public TaggedNode appendChild ( @NotNull Node childNode ) {
/*
        if ( childNode.getParent() != null ) {
            throw new IllegalStateException ( "Node '" + childNode + "' is already assigned to parent node '" + childNode.getParent() + "'. It cannot be assigned to another parent node, unless it is first removed from the current parent." );
        }

        childNodes.getList().add ( childNode );
        childNode.setParent ( this );

        return this;
 */
        return insertChild ( childNodes.size(), childNode );
    }

    public TaggedNode prependChild ( @NotNull Node childNode ) {
        return insertChild ( 0, childNode );
    }

    public TaggedNode insertChild ( int index, @NotNull Node childNode ) {

        if ( childNode.getParent() != null ) {
            throw new IllegalStateException ( "Node '" + childNode + "' is already assigned to parent node '" + childNode.getParent() + "'. It cannot be assigned to another parent node, unless it is first removed from the current parent." );
        }

        childNodes.getList().add ( index, childNode );
        childNode.setParent ( this );

        return this;
    }

    public TaggedNode appendTaggedLeafChild ( @NotNull String childTag ) {
        return appendChild ( new TaggedNode ( childTag ) );
    }

    public TaggedNode prependTaggedLeafChild ( @NotNull String childTag ) {
        return prependChild ( new TaggedNode ( childTag ) );
    }

    public TaggedNode insertTaggedLeafChild ( int index, @NotNull String childTag ) {
        return insertChild ( index, new TaggedNode ( childTag ) );
    }

    // appendText

    public TaggedNode appendTextChild ( @NotNull String childTag, @NotNull String text ) {
        return appendChild ( new TaggedNode ( childTag ).appendText ( text ) );
    }

    public TaggedNode prependTextChild ( @NotNull String childTag, @NotNull String text ) {
        return prependChild ( new TaggedNode ( childTag ).appendText ( text ) );
    }

    public TaggedNode insertTextChild ( int index, @NotNull String childTag, @NotNull String text ) {
        return insertChild ( index, new TaggedNode ( childTag ).appendText ( text ) );
    }

    public TaggedNode appendText ( @NotNull String text ) {
        return appendChild ( new TextLeaf ( text, null ) );
    }

    public TaggedNode prependText ( @NotNull String text ) {
        return prependChild ( new TextLeaf ( text, null ) );
    }

    public TaggedNode insertText ( int index, @NotNull String text ) {
        return insertChild ( index, new TextLeaf ( text, null ) );
    }

    // appendComment

    public TaggedNode appendComment ( @NotNull String comment ) {
        return appendChild ( new CommentLeaf ( comment, null ) );
    }


    // Remove Child

    public @NotNull Node removeChildAtIndex ( int index ) {

        Node removedNode = childNodes.getList().remove ( index );
        removedNode.setParent ( null );
        return removedNode;
    }

    public @NotNull Node removeFirstChild() {
        return removeChildAtIndex ( 0 );
    }

    public @NotNull Node removeLastChild() {
        return removeChildAtIndex ( getChildNodes().size() - 1 );
    }

    public boolean removeChildIfExists ( @NotNull Node childNode ) {

        Integer index = childNodes.indexOfChild ( childNode );
        if ( index == null ) return false;

        // childNodes.getList().remove ( (int) index );
        // childNode.setParent ( null );
        removeChildAtIndex ( index );
        return true;
    }

    public void removeChild ( @NotNull Node childNode ) {

        if ( ! removeChildIfExists ( childNode ) ) {
            throw new IllegalArgumentException ( "Child node doesn't exist." );
        }
    }

    public boolean removeNodesInTree ( @NotNull Predicate<Node> nodePredicate ) {

        List<Node> nodesToRemove = treeNodeStream ( false )
            .filter ( nodePredicate )
            .toList();

        if ( ! nodesToRemove.isEmpty() ) {
            for ( Node node : nodesToRemove ) {
                TaggedNode parentNode = node.getParent();
                assert parentNode != null;
                parentNode.removeChild ( node );
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean removeWhitespaceTextLeafsInTree ( boolean includeTextLeafSingletons ) {

        return removeNodesInTree (
            node -> node instanceof TextLeaf textLeaf &&
                textLeaf.isWhitespace()
                && ( includeTextLeafSingletons || textLeaf.hasSiblings() ) );
    }

    public boolean removeCommentLeafsInTree() {
        return removeNodesInTree ( node -> node instanceof CommentLeaf );
    }

    // TODO prependChild, insertChildBefore, insertChildAfter
    // TODO replaceChildAtIndex, replaceFirstChild, replaceLastChild


    // Attributes

/*
    private TaggedNode addAttribute ( @NotNull NodeAttribute attribute ) {

        stringAttributes.add ( attribute );
        return this;
    }

    public TaggedNode addAttribute ( @NotNull String name, @Nullable String value ) {
        return addAttribute ( new NodeAttribute ( name, value ) );
    }
 */

    // public boolean hasAttributes() { return stringAttributes.isNotEmpty(); }
    public boolean hasAttributes() { return stringAttributes != null; }

    public boolean hasNoAttributes() { return ! hasAttributes(); }

    public long replaceAttributesWithTextNodes() {

        if ( stringAttributes == null ) {
            return 0;
        }
        long count = stringAttributes.count();

        stringAttributes.forEachReversed ( attribute -> {
            @NotNull String name = attribute.name();
            @Nullable String value = attribute.value();
            if ( value == null || value.isEmpty() ) {
                prependTaggedLeafChild ( name );
            } else {
                prependTextChild ( name, value );
            }
        } );

        // stringAttributes.removeAll();
        stringAttributes = null;
        typedAttributes = null;

        return count;
    }


    // Namespaces

    public TaggedNode addNamespaceDefinition ( @NotNull NodeNamespace namespaceDefinition ) {

        namespaceDefinitions.add ( namespaceDefinition );
        return this;
    }

    public boolean hasNamespaceDefinitions() { return namespaceDefinitions.isNotEmpty(); }

    public boolean hasNoNamespaceDefinitions() { return ! hasNamespaceDefinitions(); }


    // TODO
    // public @NotNull BranchNode deepCopy(); // must set parent to null!
    // public boolean equals ( @NotNull BranchNode other );
    // hashCode


    @Override
    public @NotNull String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append ( tag );

        sb.append ( " " );
        int count = childNodes.size();
        switch ( count ) {
            case 0 -> sb.append ( "(no child nodes)" );
            case 1 -> sb.append ( "(1 child-node)" );
            default -> sb.append ( "(" ).append ( count ).append ( " child-nodes)" );
        }
        return sb.toString();
    }
}
