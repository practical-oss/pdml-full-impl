package dev.ps.pdml.data.node;

import dev.ps.shared.text.location.FromToTextRangeImpl;
import dev.ps.shared.text.location.TextLocation;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.location.TextPosition;

public record NodeTag(
    @NotNull String tag,
    @Nullable TextLocation tagLocation,
    @Nullable String namespacePrefix,
    @Nullable TextLocation namespacePrefixLocation) {


    public NodeTag (
        @NotNull String tag,
        @NotNull String namespacePrefix ) {

        this ( tag, null, namespacePrefix, null );
    }

    public NodeTag ( @NotNull String tag ) {
        this ( tag, null, null, null );
    }

    public static @NotNull NodeTag create (
        @NotNull String qualifiedTag,
        @Nullable TextLocation tagLocation,
        @Nullable TextLocation namespacePrefixLocation ) {

        int separatorIndex = qualifiedTag.indexOf ( PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR );
        if ( separatorIndex == -1 || separatorIndex == 0 || separatorIndex == qualifiedTag.length() -1 ) {
            return new NodeTag ( qualifiedTag, tagLocation, null, namespacePrefixLocation );
        } else {
            String namespacePrefix = qualifiedTag.substring ( 0, separatorIndex );
            String tag = qualifiedTag.substring ( separatorIndex + 1 );
            return new NodeTag ( tag, tagLocation, namespacePrefix, namespacePrefixLocation );
        }
    }

    public static @NotNull NodeTag create ( @NotNull String qualifiedTag ) {
        return create ( qualifiedTag, null, null );
    }


    public boolean hasNamespacePrefix() { return namespacePrefix != null; }

    public @Nullable TextLocation location() {

        if ( namespacePrefixLocation == null ) {
            return tagLocation;
        } else if ( tagLocation == null ) {
            return namespacePrefixLocation;
        } else {
            TextPosition start = namespacePrefixLocation.startPosition();
            TextPosition end = tagLocation.endPosition();
            if ( start != null && end != null ) {
                return new FromToTextRangeImpl ( tagLocation.readerResource(),
                    start, end, tagLocation.parentLocation() );
            } else {
                return null;
            }
        }
    }

    public @NotNull String qualifiedTag() {
        return namespacePrefix == null
            ? tag
            : namespacePrefix + PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR + tag;
    }


    @Override
    public boolean equals ( Object other ) {

        if ( other instanceof NodeTag o ) {
            return qualifiedTag().equals ( o.qualifiedTag() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return qualifiedTag().hashCode();
    }

    @Override
    public @NotNull String toString() { return qualifiedTag(); }
}
