package dev.ps.pdml.cmdnode;

import dev.ps.prt.command.Command;
import dev.ps.prt.parameter.Parameters;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;

import java.util.List;
import java.util.function.Supplier;

public class CommandNodeBase extends Command {

    public CommandNodeBase (
        @NotNull String name,
        @Nullable Parameters parameters,
        @Nullable Supplier<SimpleDocumentation> documentationSupplier ) {

        super ( name, (List<String>) null, parameters, documentationSupplier );
    }
}
