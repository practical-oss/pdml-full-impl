package dev.ps.pdml.ext.types;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.ExtensionNodeHandlerContext;
import dev.ps.pdml.ext.ExtensionNodeHandler;
import dev.ps.pdml.ext.InsertReaderResourceExtensionResult;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;

public class TypeNodeHandler<T> implements ExtensionNodeHandler {


    // public static final @NotNull String EXTENSION_KIND = "t";


    private final @NotNull PdmlType<T> type;
    public @NotNull PdmlType<T> getType() { return type; }


    public TypeNodeHandler ( @NotNull PdmlType<T> type ) {
        this.type = type;
    }


    public @NotNull String getExtensionName() {
        return type.getName();
    }

    @Override
    public @Nullable InsertReaderResourceExtensionResult handleNode (
        @NotNull ExtensionNodeHandlerContext context,
        @NotNull NodeTag nodeName ) throws IOException, PdmlException {

        type.parseValidateAndHandleObject (
            context.getPdmlParser(), null, true );
        return null;
    }
}
