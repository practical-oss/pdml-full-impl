package dev.ps.pdml.cmdnode;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;

public interface GlobalCommandNodeExecutor {

    @Nullable CommandNodeResult executeCommand (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParser pdmlParser ) throws IOException, PdmlException;
}
