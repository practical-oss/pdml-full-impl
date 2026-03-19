package dev.ps.pdml.ext;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;

public interface ExtensionNodeHandler {

    @NotNull String getExtensionName();

    // When this method is called, the node tag and a tag/value separator are consumed already
    // (e.g. ^s[exp 1+1] -> the reader is on "1+1"
    // void handleNode ( @NotNull ExtensionNodeHandlerContext context, @NotNull NodeName nodeName )
    //    throws IOException, PdmlException;

    // returns text to be inserted
    // When this method is called, the node tag and a tag/value separator are consumed already
    // (e.g. ^s[exp 1+1] -> the reader is on "1+1"
    @Nullable InsertReaderResourceExtensionResult handleNode (
        @NotNull ExtensionNodeHandlerContext context,
        @NotNull NodeTag nodeName )
            throws IOException, PdmlException;
}
