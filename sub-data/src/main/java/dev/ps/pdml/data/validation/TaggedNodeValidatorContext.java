package dev.ps.pdml.data.validation;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.handler.TextInspectionMessageHandler;

public class TaggedNodeValidatorContext {

/*
    public static @NotNull TextInspectionMessageHandler createDefaultMessageHandler() {
        return TextInspectionMessageHandler.newDefaultHandler();
    }
 */


    private final @NotNull TextInspectionMessageHandler messageHandler;
    public @NotNull TextInspectionMessageHandler getMessageHandler() { return messageHandler; }


    public TaggedNodeValidatorContext ( @NotNull TextInspectionMessageHandler messageHandler ) {
        this.messageHandler = messageHandler;
    }

    public TaggedNodeValidatorContext () {
        // this ( createDefaultMessageHandler() );
        this ( TextInspectionMessageHandler.newDefaultHandler() );
    }


    public boolean checkHasChildNodes ( @NotNull TaggedNode taggedNode ) {

        boolean success = taggedNode.hasChildNodes();
        if ( ! success ) {
            errorDetected (
                taggedNode,
                "Node '" + taggedNode.qualifiedTag () + "' must have child nodes.",
                "CHILD_NODES_REQUIRED" );

        }
        return success;
    }

    public boolean checkParentName (
        @NotNull TaggedNode taggedNode,
        @NotNull NodeTag parentNodeName ) {

        @Nullable TaggedNode parentNode = taggedNode.getParent();
        if ( parentNode == null ) {
            errorDetected (
                taggedNode,
                "Node '" + taggedNode.qualifiedTag () + "' must have a parent node.",
                "PARENT_NODE_REQUIRED" );
            return false;
        }

        boolean success = parentNode.getTag ().equals ( parentNodeName );
        if ( ! success ) {
            errorDetected (
                taggedNode,
                "Invalid parent node '" + parentNode.qualifiedTag () + "'. Node '"
                    + taggedNode.getTag ().qualifiedTag () + "' must be a child node of '" + parentNodeName.qualifiedTag () + "'.",
                "INVALID_PARENT_NODE" );

        }
        return success;
    }

    public void errorDetected (
        @NotNull String message,
        @NotNull String id,
        @Nullable TextRange textLocation ) {

        messageHandler.handleError ( message, id, textLocation );
    }

    public void errorDetected (
        @NotNull TaggedNode taggedNode,
        @NotNull String message,
        @NotNull String id ) {

        errorDetected ( message, id, taggedNode.getTextLocation () );
    }
}
