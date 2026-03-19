package dev.ps.pdml.data.node;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public record NodeTag(
    @NotNull String tag,
    @Nullable TextRange tagPositionOrRange,
    @Nullable String namespacePrefix,
    @Nullable TextRange namespacePrefixPositionOrRange ) {


    public static @NotNull NodeTag create (
        @NotNull String qualifiedTag,
        @Nullable TextRange tagPositionOrRange,
        @Nullable TextRange namespacePrefixPositionOrRange ) {

        int separatorIndex = qualifiedTag.indexOf ( PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR );
        if ( separatorIndex == -1 || separatorIndex == 0 || separatorIndex == qualifiedTag.length() -1 ) {
            return new NodeTag ( qualifiedTag, tagPositionOrRange, null, namespacePrefixPositionOrRange );
        } else {
            String namespacePrefix = qualifiedTag.substring ( 0, separatorIndex );
            String tag = qualifiedTag.substring ( separatorIndex + 1 );
            return new NodeTag ( tag, tagPositionOrRange, namespacePrefix, namespacePrefixPositionOrRange );
        }
    }

    public static @NotNull NodeTag create ( @NotNull String qualifiedTag ) {
        return create ( qualifiedTag, null, null );
    }

    public NodeTag (
        @NotNull String tag,
        @NotNull String namespacePrefix ) {

        this ( tag, null, namespacePrefix, null );
    }

    public NodeTag ( @NotNull String tag ) {
        this ( tag, null, null, null );
    }


    public boolean hasNamespacePrefix() { return namespacePrefix != null; }

    /*
    public @Nullable TextPositionImpl startPosition() {

        TextPositionImpl position = namespacePrefixPositionOrRange != null
            ? namespacePrefixPositionOrRange.startLineColumn_OLD ()
            : null;
        if ( position != null ) {
            return position;
        } else {
            return tagPositionOrRange != null
                ? tagPositionOrRange.startLineColumn_OLD ()
                : null;
        }
    }
     */

    public @Nullable TextRange startLocation() {
        return namespacePrefixPositionOrRange != null
            ? namespacePrefixPositionOrRange : tagPositionOrRange;
    }

    public @NotNull String qualifiedTag() {

        StringBuilder sb = new StringBuilder();
        if ( namespacePrefix != null ) {
            sb.append ( namespacePrefix );
            sb.append ( PdmlExtensionsConstants.NAMESPACE_SEPARATOR_CHAR );
        }

        sb.append ( tag );

        return sb.toString();
    }

/*
    @Deprecated
    public @NotNull TextToken qualifiedTagToken() {

        @Nullable TextPosition startPosition = startPosition();
        TextResourcePosition rrp = null;
        if ( startPosition instanceof TextResourcePosition rrp_ ) {
            rrp = rrp_;
        }
        return new TextToken ( qualifiedTag(), rrp );
    }
 */


    @Override
    public boolean equals ( Object other ) {

        if ( other instanceof NodeTag o ) {
            return qualifiedTag ().equals ( o.qualifiedTag () );
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
