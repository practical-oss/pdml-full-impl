package dev.ps.pdml.data.node;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.tagged.ChildNodes;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {


    protected @Nullable TaggedNode parent;
    public @Nullable TaggedNode getParent() { return parent; }
    // CAUTION: must only be called by 'TaggedNode' (to ensure data integrity)
    public void setParent ( @Nullable TaggedNode parent ) { this.parent = parent; }

    protected @Nullable TextRange textLocation;
    public @Nullable TextRange getTextLocation() { return textLocation; }
    public void setTextLocation ( @Nullable TextRange textLocation ) { this.textLocation = textLocation; }


    protected Node (
        @Nullable TextRange textLocation ) {

        this.parent = null;
        this.textLocation = textLocation;
    }


    // Positions

    /*
    public @Nullable TextPositionImpl startPosition() {
        return textLocation != null ? textLocation.startLineColumn_OLD () : null;
    }

    public @Nullable TextLineColumn endPosition() {
        return textLocation != null ? textLocation.endLineColumn () : null;
    }
     */


    // isxxx

    public abstract boolean isRootNode();
    public abstract boolean isTaggedNode();
    public abstract boolean isLeafNode();
    public abstract boolean isTextLeaf();
    public abstract boolean isCommentLeaf();


    // Path

    public @NotNull NodePath path() {

        List<Node> nodes;
        if ( parent == null ) {
            nodes = List.of ( this );
        } else {
            nodes = new ArrayList<> ( parent.path().getNodes () );
            nodes.add ( this );
        }

        return new NodePath ( nodes );
    }


    // Index

    public @Nullable Integer childIndex() {

        if ( parent == null ) {
            return null;
        } else {
            return parent.getChildNodes().indexOfChild ( this );
        }
    }

    public @Nullable Integer childIndexForHumans() {

        Integer index = childIndex();
        return index == null ? null : index + 1;
    }


    // Siblings

    public boolean hasSiblings() {
        return parent != null && parent.getChildNodes().size() > 1;
    }

    public @Nullable Node nextSibling() {

        if ( parent == null ) return null;

        Integer thisIndex = childIndex();
        if ( thisIndex == null ) return null;

        ChildNodes childNodes = parent.getChildNodes();
        if ( childNodes.size() > thisIndex + 1 ) {
            return childNodes.get ( thisIndex + 1 );
        } else {
            return null;
        }
    }

    public @Nullable Node previousSibling() {

        if ( parent == null ) return null;

        Integer thisIndex = childIndex ();
        if ( thisIndex == null ) return null;

        if ( thisIndex > 0 ) {
            return parent.getChildNodes().get ( thisIndex - 1 );
        } else {
            return null;
        }
    }

    public @Nullable Node firstSibling() {

        if ( parent == null ) return null;

        ChildNodes childNodes = parent.getChildNodes();
        if ( childNodes.isNotEmpty() ) {
            return childNodes.first();
        } else {
            return null;
        }
    }

    public @Nullable Node lastSibling() {

        if ( parent == null ) return null;

        ChildNodes childNodes = parent.getChildNodes();
        if ( childNodes.isNotEmpty() ) {
            return childNodes.last();
        } else {
            return null;
        }
    }

    // TODO? public @Nullable List<Node> nextSiblings() {
    // TODO? public @Nullable List<Node> previousSiblings() {
}
