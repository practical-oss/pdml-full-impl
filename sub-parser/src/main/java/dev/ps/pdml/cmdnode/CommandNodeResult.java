package dev.ps.pdml.cmdnode;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;

public record CommandNodeResult(
    @NotNull ReaderResource readerResource,
    boolean escapeText ) {

    public CommandNodeResult ( @NotNull String string, boolean escapeText ) {
        this ( new StringReaderResource ( string ), escapeText );
    }
}
