package dev.ps.pdml.data.namespace;

import dev.ps.shared.text.range.TextRange;
import dev.ps.pdml.data.PdmlExtensionsConstants;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public record NodeNamespace (
    @NotNull String namePrefix,
    @Nullable TextRange namePrefixPositionOrRange,
    @NotNull String URI,
    @Nullable TextRange URIPositionOrRange ) {


    public NodeNamespace (
        @NotNull String namePrefix,
        @NotNull String URI ) {

        this ( namePrefix, null, URI, null );
    }

    // TODO? public boolean isDefaultNamespace() { return namePrefix.equals ( DEFAULT_NAMESPACE_PREFIX ); }

    @Override
    public @NotNull String toString() {
        return namePrefix + PdmlExtensionsConstants.ATTRIBUTE_ASSIGN_CHAR + URI;
    }
}
