package dev.ps.pdml.ext;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pdml.reader.PdmlTokenReader;

import java.io.IOException;

public interface ExtensionNodesHandler {

    // void handleExtensionNode (
    @Nullable InsertReaderResourceExtensionResult handleExtensionNode (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParser pdmlParser ) throws IOException, PdmlException;
}
