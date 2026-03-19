package dev.ps.pdml.data.node;

import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class NodePath {


    private final @NotNull List<Node> nodes;
    public @NotNull List<Node> getNodes() { return Collections.unmodifiableList ( nodes ); }


    public NodePath ( @NotNull List<Node> nodes ) {
        assert ! nodes.isEmpty();

        this.nodes = nodes;
    }


    public @NotNull Stream<Node> nodeStream() { return nodes.stream(); }

    public @NotNull Stream<TaggedNode> taggedNodeStream() {
        return nodeStream()
            .filter ( Node::isTaggedNode )
            .map ( node -> (TaggedNode) node );
    }

    public int nodesCount() { return nodes.size(); }

    public @NotNull Node get ( int index ) {
        return nodes.get ( index );
    }

    public @NotNull TaggedNode rootNode() {
        return (TaggedNode) nodes.get ( 0 );
    }

    public @NotNull Node targetNode() { return nodes.get ( nodesCount() - 1 ); }

    // public boolean isLeafNodeTarget() { return targetNode().isLeafNode(); }

    public @NotNull List<NodeTag> tags() {
        return taggedNodeStream().map ( TaggedNode::getTag ).toList();
    }

    public @NotNull List<String> tagStrings() {
        return taggedNodeStream().map ( taggedNode -> taggedNode.getTag().toString() ).toList();
    }

    public @NotNull String asString() {

        StringBuilder sb = new StringBuilder();

        for ( Node node : nodes ) {
            sb.append ( CorePdmlConstants.PATH_SEPARATOR );
            if ( node instanceof TaggedNode taggedNode ) {
                // TODO quote tag if it contains '/'
                sb.append ( taggedNode.getTag () );
            }
            Integer index = node.childIndexForHumans();
            if ( index == null && node.isRootNode() ) {
                index = 1;
            }
            if ( index != null ) {
                sb.append ( "[" ).append ( index ).append ( "]" );
            }
        }

        return sb.toString();
    }

    /**
     *
     * @return the level of this node in the tree. Root node is level zero. A direct child of the root node is level one.
     */
    public int level() {
        return nodes.size() - 1;
    }

    // TODO?
    // public @Nullable Path relativize ( @NotNull Path other )
    // public @Nullable RelativePath relativize ( @NotNull Path other )

    @Override
    public @NotNull String toString() { return asString(); }
}
