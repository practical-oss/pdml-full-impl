package dev.ps.pdml.data.namespace;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.range.TextRange;

import java.util.*;

public class NodeNamespaces {



    private final @Nullable TextRange textLocation;
    public @Nullable TextRange getTextLocation () { return textLocation; }

    private final @NotNull Map<String, NodeNamespace> URIMap;
    private final @NotNull Map<String, NodeNamespace> namePrefixMap;

    public NodeNamespaces ( @Nullable TextRange textLocation ) {

        this.textLocation = textLocation;
        this.URIMap = new HashMap<>();
        this.namePrefixMap = new HashMap<>();
    }


    // Queries

    public boolean isEmpty() { return URIMap.isEmpty(); }

    public boolean isNotEmpty() { return ! URIMap.isEmpty(); }

    public long count() { return URIMap.size(); }

    public @NotNull Iterator<NodeNamespace> iterator() { return URIMap.values().iterator(); }

    public @Nullable Collection<NodeNamespace> list() {
        return URIMap.isEmpty() ? null : Collections.unmodifiableCollection ( URIMap.values() );
    }

    public boolean containsURI ( @NotNull String URI ) { return URIMap.containsKey ( URI ); }

    public boolean containsNamePrefix ( @NotNull String namePrefix ) { return namePrefixMap.containsKey ( namePrefix ); }

    public boolean containsNamespace ( @NotNull NodeNamespace namespace ) {
        return containsNamePrefix ( namespace.namePrefix() );
    }

    public @NotNull NodeNamespace getByURI ( @NotNull String URI ) {
        checkURIExists ( URI );
        return URIMap.get ( URI );
    }

    public @NotNull NodeNamespace getByPrefix ( @NotNull String namePrefix ) {
        checkNamePrefixExists ( namePrefix );
        return namePrefixMap.get ( namePrefix );
    }

    public @Nullable NodeNamespace getByPrefixOrNull ( @NotNull String namePrefix ) {
        return namePrefixMap.get ( namePrefix );
    }


    // Add

    public void add ( @NotNull NodeNamespace namespace ) {
        checkURIDoesNotExist ( namespace.URI() );
        checkNamePrefixDoesNotExist ( namespace.namePrefix() );

        URIMap.put ( namespace.URI(), namespace );
        namePrefixMap.put ( namespace.namePrefix(), namespace );
    }

    public void add ( @NotNull String URI, @NotNull String namePrefix ) {
        add ( new NodeNamespace ( namePrefix, URI ) );
    }


    // Remove

    public void remove ( @NotNull NodeNamespace namespace ) {

        checkNamespaceExists ( namespace );
        URIMap.remove ( namespace.URI() );
        namePrefixMap.remove ( namespace.namePrefix() );
    }

    public void removeByPrefix ( @NotNull String prefix ) {

        @NotNull NodeNamespace namespace = getByPrefix ( prefix );
        remove ( namespace );
    }

    // TODO


    // Replace
    // TODO


    @Override
    public @NotNull String toString() { return count() + " namespace(s)"; }


    // Private helpers

    private void checkNamespaceExists ( @NotNull NodeNamespace namespace ) {
        checkURIExists ( namespace.URI () );
    }

    private void checkURIExists ( @NotNull String URI ) {
        if ( ! containsURI ( URI ) )
            throw new IllegalArgumentException ( "URI '" + URI + "' does not exist." );
    }

    private void checkNamePrefixExists ( @NotNull String namePrefix ) {
        if ( ! containsNamePrefix ( namePrefix ) )
            throw new IllegalArgumentException ( "Name prefix '" + namePrefix + "' does not exist." );
    }

    private void checkURIDoesNotExist ( @NotNull String URI ) {
        if ( containsURI ( URI ) )
            throw new IllegalArgumentException ( "URI '" + URI + "' exists already." );
    }

    private void checkNamePrefixDoesNotExist ( @NotNull String namePrefix ) {
        if ( containsNamePrefix ( namePrefix ) )
            throw new IllegalArgumentException ( "Name prefix '" + namePrefix + "' exists already." );
    }
}
