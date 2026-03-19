package dev.ps.pdml.data.util;

import dev.ps.pdml.data.node.Node;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class TreeIterator implements Iterator<Node> {

    private @Nullable Node nextNode;
    private @Nullable Iterator<Node> currentChildNodeIterator;
    private final @NotNull Stack<Iterator<Node>> pendingChildNodeIteratorsStack;


    public TreeIterator ( @NotNull TaggedNode rootNode, boolean includeRootNode ) {

        currentChildNodeIterator = rootNode.getChildNodes().iterator();
        pendingChildNodeIteratorsStack = new Stack<>();

        if ( includeRootNode ) {
            nextNode = rootNode;
        } else {
            nextNode = advance();
        }
    }

    public TreeIterator ( @NotNull TaggedNode rootNode ) {
        this ( rootNode, true );
    }


    public boolean hasNext() {
        return nextNode != null;
    }

    public @NotNull Node next() throws NoSuchElementException {

        if ( nextNode == null ) {
            throw new NoSuchElementException ( "There are no more elements. next() should not be called after hasNext() returns false." );
        }
        Node result = nextNode;
        nextNode = advance();
        return result;
    }

    private @Nullable Node advance() {

        if ( currentChildNodeIterator == null ) return null;

        if ( currentChildNodeIterator.hasNext() ) {
            return nextNodeFromCurrentChildNodeIterator();

        } else {
            if ( pendingChildNodeIteratorsStack.isEmpty() ) {
                currentChildNodeIterator = null;
                return null;
            } else {
                currentChildNodeIterator = pendingChildNodeIteratorsStack.pop();
                return nextNodeFromCurrentChildNodeIterator();
            }
        }
    }

    private @NotNull Node nextNodeFromCurrentChildNodeIterator() {

        assert currentChildNodeIterator != null;
        assert currentChildNodeIterator.hasNext();

        Node nextNode = currentChildNodeIterator.next();

/*
        if ( nextNode instanceof LeafNode leafNode ) {
            return leafNode;

        } else if ( nextNode instanceof BranchNode branchNode ) {
            Iterator<Node> newIterator = branchNode.childNodesIterator();
            if ( newIterator != null ) {
                if ( currentChildNodeIterator.hasNext() ) {
                    pendingChildNodeIteratorsStack.push ( currentChildNodeIterator );
                }
                currentChildNodeIterator = newIterator;
            }
            return branchNode;
        } else {
            throw new IllegalStateException ( "Unexpected node type." );
        }
 */
        if ( nextNode instanceof TaggedNode taggedNode ) {
            /*
            Iterator<Node> newIterator = branchNode.childNodesIterator();
            if ( newIterator != null ) {
                if ( currentChildNodeIterator.hasNext() ) {
                    pendingChildNodeIteratorsStack.push ( currentChildNodeIterator );
                }
                currentChildNodeIterator = newIterator;
            }
             */
            if ( taggedNode.hasChildNodes() ) {
                Iterator<Node> newIterator = taggedNode.getChildNodes().iterator();
                if ( currentChildNodeIterator.hasNext() ) {
                    pendingChildNodeIteratorsStack.push ( currentChildNodeIterator );
                }
                currentChildNodeIterator = newIterator;
            }
        }

        return nextNode;
    }
}
