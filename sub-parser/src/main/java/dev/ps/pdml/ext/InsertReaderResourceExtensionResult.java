package dev.ps.pdml.ext;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.ReaderResource;

public record InsertReaderResourceExtensionResult(
    // @Nullable String string,
    @NotNull ReaderResource readerResource,
    boolean escapeText ) {
}
