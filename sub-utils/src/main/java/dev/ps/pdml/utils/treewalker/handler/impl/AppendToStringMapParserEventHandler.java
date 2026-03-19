package dev.ps.pdml.utils.treewalker.handler.impl;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.utils.treewalker.handler.TaggedNodeEndEvent;
import dev.ps.pdml.utils.treewalker.handler.TaggedNodeStartEvent;
import dev.ps.pdml.utils.treewalker.handler.PdmlTreeWalkerEventHandler;
import dev.ps.pdml.utils.treewalker.handler.TextEvent;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.prt.argument.StringArguments;

import java.util.HashMap;
import java.util.Map;

public class AppendToStringMapParserEventHandler implements PdmlTreeWalkerEventHandler<TaggedNodeStartEvent, Map<String,String>> {


    private final @NotNull Map<String,String> map;
    private @Nullable TaggedNodeStartEvent rootNode;
    private @Nullable String currentKey;


    public AppendToStringMapParserEventHandler ( @NotNull Map<String,String> map ) {

        this.map = map;
        this.rootNode = null;
        this.currentKey = null;
    }

    public AppendToStringMapParserEventHandler () {

        this ( new HashMap<>() );
    }


    @Override
    public @NotNull TaggedNodeStartEvent onRootNodeStart ( @NotNull TaggedNodeStartEvent event ) {

        rootNode = event;
        return rootNode;
    }

    @Override
    public @NotNull TaggedNodeStartEvent onTaggedNodeStart ( @NotNull TaggedNodeStartEvent startEvent, @NotNull TaggedNodeStartEvent parent ) throws PdmlException {

        if ( parent != rootNode ) {
            throw new PdmlException (
                "Node '" + parent + "' cannot have child nodes.",
                "CHILD_NODE_NOT_ALLOWED",
                startEvent.tag().tagPositionOrRange() );
        }

        @NotNull String key = startEvent.tag ().qualifiedTag ();
        if ( map.containsKey ( key ) ) {
            throw new PdmlException (
                "Node '" + key + "' has already been defined.",
                "KEY_EXISTS_ALREADY",
                startEvent.tag().tagPositionOrRange() );
        }

        currentKey = key;

        addAttributes ( startEvent.attributes(), parent );

        return startEvent;
    }

    public void addAttributes (
        @Nullable StringArguments attributes,
        @NotNull TaggedNodeStartEvent parent ) throws PdmlException {

        if ( attributes == null ) return;

        // Allow attributes only in root node

        if ( parent != rootNode ) {
            throw new PdmlException (
                "Node '" + parent + "' cannot have attributes.",
                "ATTRIBUTES_NOT_ALLOWED",
                attributes.location() );
        }

        attributes.forEach ( attribute -> map.put ( attribute.name(), attribute.value() ) );
    }


    @Override
    public void onTaggedNodeEnd ( @NotNull TaggedNodeEndEvent event, @NotNull TaggedNodeStartEvent start ) {

        if ( currentKey != null ) {
            // It's an empty node (no text defined)
            map.put ( currentKey, null );
        }
    }

    @Override
    public void onText ( @NotNull TextEvent event, @NotNull TaggedNodeStartEvent parent ) throws PdmlException {

        String text = event.text();

        if ( parent == rootNode ) {
            if ( text.trim().isEmpty() ) {
                return;
            } else {
                throw new PdmlException (
                    "Node '" + parent + "' cannot contain text.",
                    "TEXT_NOT_ALLOWED",
                    parent.tag().tagPositionOrRange() );
            }
        }

        map.put ( currentKey, text );
        currentKey = null;
    }

    public @Nullable Map<String,String> getResult() {
        return map.isEmpty() ? null : map;
    }
}
