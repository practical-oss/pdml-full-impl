package dev.ps.pdml.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.pdml.data.node.leaf.UntaggedLeafNode;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

/*
See
https://cowtowncoder.medium.com/jackson-2-12-improved-xml-b9487889a23f
 */

public class PdmlTreeToJsonTreeConverter {

// TODO config param to include "path" (default = false)
/*
    public record PdmlToJsonConfig (
        // @NotNull String rootNodeName,
        // @NotNull String listElementNodeName
    ) {}


    public static final @NotNull PdmlToJsonConfig DEFAULT_CONFIG =
        new PdmlToJsonConfig();


    private final @NotNull PdmlToJsonConfig config;

    public PdmlTreeToJsonTreeConverter ( @NotNull PdmlToJsonConfig config ){

        this.config = config;
        this.objectMapper = new ObjectMapper();
    }
 */


    private final @NotNull ObjectMapper objectMapper;


    public PdmlTreeToJsonTreeConverter() {
        this.objectMapper = new ObjectMapper();
    }


    public @NotNull ObjectNode convert ( @NotNull TaggedNode pdmlRootNode ) {
        return convertPdmlTaggedNode ( pdmlRootNode );
    }


    private @NotNull ObjectNode convertPdmlTaggedNode (
        @NotNull TaggedNode pdmlTaggedNode ) {

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put ( "type", "tagged" );
        jsonNode.put ( "tag", pdmlTaggedNode.qualifiedTag () );
        jsonNode.put ( "path", pdmlTaggedNode.path().asString() );

        @Nullable ObjectNode attributesNode = convertAttributes ( pdmlTaggedNode );
        if ( attributesNode != null ) {
            jsonNode.set ( "attributes", attributesNode );
        }

        @Nullable ArrayNode childNodesNode = convertPdmlChildNodes ( pdmlTaggedNode );
        if ( childNodesNode != null ) {
            jsonNode.set ( "childNodes", childNodesNode );
        }

        return jsonNode;
    }

    private @Nullable ObjectNode convertAttributes (
        @NotNull TaggedNode pdmlTaggedNode ) {

        if ( pdmlTaggedNode.hasNoAttributes() ) {
            return null;
        } else {
            ObjectNode jsonAttributesObject = objectMapper.createObjectNode();
            pdmlTaggedNode.getStringAttributes().forEach ( attribute -> {
                jsonAttributesObject.put (
                    attribute.name(),
                    attribute.value() );
            } );
            return jsonAttributesObject;
        }
    }

    private @Nullable ArrayNode convertPdmlChildNodes (
        @NotNull TaggedNode pdmlTaggedNode ) {

        if ( pdmlTaggedNode.hasNoChildNodes() ) {
            return null;
        } else {
            ArrayNode childNodesArray = objectMapper.createArrayNode ();
            pdmlTaggedNode.getChildNodes().forEach ( childNode -> {

                ObjectNode jsonChildNode;

                if ( childNode instanceof TaggedNode taggedChild ) {
                    jsonChildNode = convertPdmlTaggedNode ( taggedChild );
                } else if ( childNode instanceof TextLeaf textChild ) {
                    jsonChildNode = convertPdmlLeafNode ( textChild, "text" );
                } else if ( childNode instanceof CommentLeaf commentChild ) {
                    jsonChildNode = convertPdmlLeafNode ( commentChild, "comment" );
                } else {
                    throw new IllegalStateException (
                        "Unexpected childNode type " + childNode.getClass () );
                }

                childNodesArray.add ( jsonChildNode );
            } );
            return childNodesArray;
        }
    }

    private @NotNull ObjectNode convertPdmlLeafNode (
        @NotNull UntaggedLeafNode pdmlLeafNode,
        @NotNull String type ) {

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put ( "type", type );
        jsonNode.put ( "text", pdmlLeafNode.getText() );
        jsonNode.put ( "path", pdmlLeafNode.path().asString() );
        return ( jsonNode );
    }
}
