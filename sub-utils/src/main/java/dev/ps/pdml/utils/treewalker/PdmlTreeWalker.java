package dev.ps.pdml.utils.treewalker;

/*
import dev.pp.pdml.data.exception.PdmlException;
import dev.pp.pdml.data.node.Node;
import dev.pp.pdml.data.node.tagged.TaggedNode;
import dev.pp.pdml.data.node.leaf.CommentLeaf;
import dev.pp.pdml.data.node.leaf.TextLeaf;
import dev.pp.core.basics.annotations.NotNull;
import dev.pp.pdml.utils.treewalker.handler.*;

import java.io.IOException;
 */

@Deprecated // not used
public class PdmlTreeWalker{}
/*
public class PdmlTreeWalker<N, R> {


    private final @NotNull PdmlTreeWalkerEventHandler<N, R> eventHandler;


    public PdmlTreeWalker (
        @NotNull PdmlTreeWalkerEventHandler<N, R> eventHandler ) {

        this.eventHandler = eventHandler;
    }


    public void walkTree ( @NotNull TaggedNode rootNode) throws IOException, PdmlException {

        eventHandler.onStart();

        TaggedNodeStartEvent startEvent = taggedNodeStartEvent ( rootNode );
        N resultObject = eventHandler.onRootNodeStart ( startEvent );

        walkChildNodes ( rootNode, resultObject, startEvent );

        eventHandler.onRootNodeEnd (
            new TaggedNodeEndEvent ( rootNode.endPosition(), startEvent ),
            resultObject );

        eventHandler.onEnd();
    }


    private void walkChildNodes (
        @NotNull TaggedNode parentNode,
        @NotNull N parentObject,
        @NotNull TaggedNodeStartEvent startEvent ) throws IOException, PdmlException {

        for ( Node node : parentNode.getChildNodes() ) {

            if ( node instanceof TaggedNode taggedNode ) {
                walkTaggedNode ( taggedNode, parentObject );

            } else if ( node instanceof TextLeaf textLeaf ) {
                eventHandler.onText (
                    new TextEvent ( textLeaf.getText(), textLeaf.startPosition(), startEvent ),
                    parentObject );

            } else if ( node instanceof CommentLeaf commentLeaf ) {
                eventHandler.onComment (
                    new CommentEvent ( commentLeaf.getText(), commentLeaf.startPosition(), startEvent ),
                    parentObject );

            } else {
                throw new IllegalStateException ( "Unexpected node type: " + node );
            }
        }
    }

    private void walkTaggedNode (
        @NotNull TaggedNode taggedNode,
        @NotNull N parentObject ) throws IOException, PdmlException {

        TaggedNodeStartEvent startEvent = taggedNodeStartEvent ( taggedNode );
        N resultObject = eventHandler.onTaggedNodeStart ( startEvent, parentObject );

        walkChildNodes ( taggedNode, resultObject, startEvent );

        eventHandler.onTaggedNodeEnd (
            new TaggedNodeEndEvent ( taggedNode.endPosition(), startEvent ),
            resultObject );
    }

    private @NotNull TaggedNodeStartEvent taggedNodeStartEvent (
        @NotNull TaggedNode taggedNode ) {

        return new TaggedNodeStartEvent (
            taggedNode.startPosition(),
            taggedNode.getTag (),
            taggedNode.getNamespaceDefinitions(),
            taggedNode.getStringAttributes(),
            taggedNode.isEmpty(),
            taggedNode.getSpec() );
    }
}
 */
