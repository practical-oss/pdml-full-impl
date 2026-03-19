package dev.ps.pdml.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public class JsonTreeToPdmlTreeConverter {


    public record JsonToPdmlConfig (
        @NotNull String rootNodeName,
        @NotNull String listElementNodeName
    ) {}


    public static final @NotNull String DEFAULT_ROOT_NODE_NAME = "root";
    public static final @NotNull String DEFAULT_LIST_ELEMENT_NODE_NAME = "el";
    public static final @NotNull JsonToPdmlConfig DEFAULT_CONFIG =
        new JsonToPdmlConfig ( DEFAULT_ROOT_NODE_NAME, DEFAULT_LIST_ELEMENT_NODE_NAME );


    private final @NotNull JsonToPdmlConfig config;


    public JsonTreeToPdmlTreeConverter ( @NotNull JsonToPdmlConfig config ) {
        this.config = config;
    }

    public JsonTreeToPdmlTreeConverter() {
        this ( DEFAULT_CONFIG );
    }


    public @NotNull TaggedNode convert ( @NotNull JsonNode jsonNode ) {

        TaggedNode rootNode = new TaggedNode ( config.rootNodeName );
        appendJsonNode ( jsonNode, rootNode );
        return rootNode;
    }

    private void appendJsonNode (
        @NotNull JsonNode jsonNode,
        @NotNull TaggedNode pdmlParentNode ) {

        // use Java 'switch' when supported
        if ( jsonNode instanceof ObjectNode objectNode ) {
            appendJsonObject ( objectNode, pdmlParentNode );

        } else if ( jsonNode instanceof ArrayNode arrayNode ) {
            appendJsonArray ( arrayNode, pdmlParentNode );

        } else if ( jsonNode instanceof ValueNode valueNode ) {
            appendJsonValue ( valueNode, pdmlParentNode );

        } else {
            throw new IllegalStateException ( "Type '" + jsonNode.getClass() + "' not supported." );
        }
    }

    private void appendJsonObject (
        @NotNull ObjectNode jsonObject,
        @NotNull TaggedNode pdmlParentNode ) {

        Iterator<Map.Entry<String,JsonNode>> fieldsIterator = jsonObject.fields();
        while ( fieldsIterator.hasNext() ) {
            Map.Entry<String,JsonNode> field = fieldsIterator.next();
            // TODO check if valid PDML tag
            TaggedNode fieldNode = new TaggedNode ( field.getKey() );
            appendJsonNode ( field.getValue(), fieldNode );
            pdmlParentNode.appendChild ( fieldNode );
        }
    }

    private void appendJsonArray (
        @NotNull ArrayNode jsonArray,
        @NotNull TaggedNode pdmlParentNode ) {

        jsonArray.forEach ( jsonNode -> {
            TaggedNode elementNode = new TaggedNode ( config.listElementNodeName );
            appendJsonNode ( jsonNode, elementNode );
            pdmlParentNode.appendChild ( elementNode );
        } );
    }

    private void appendJsonValue (
        @NotNull ValueNode jsonValue,
        @NotNull TaggedNode pdmlParentNode ) {

        // use Java 'switch' when supported
        if ( jsonValue instanceof TextNode ||
            jsonValue instanceof NumericNode ||
            jsonValue instanceof BooleanNode ||
            jsonValue instanceof BinaryNode ) {
            pdmlParentNode.appendText ( jsonValue.asText() );

        } else if ( jsonValue instanceof NullNode ) {
            // do nothing

        /* TODO ?
        //  } else if ( jsonValue instanceof MissingNode ) {
            // do nothing (like NullNode)

        } else if ( jsonValue instanceof POJONode pojoNode ) {
            Object pojo = pojoNode.getPojo();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString ( pojo );
            JsonNode node = mapper.readTree ( jsonString );
            appendJsonNode ( node, pdmlParentNode );
         */

        } else {
            // TODO other subtypes of ValueNode: MissingNode, POJONode
            throw new IllegalStateException ( "Type '" + jsonValue.getClass() + "' not supported." );
        }
    }
}
