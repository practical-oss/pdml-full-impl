package dev.ps.pdml.cmdnode;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameters;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;

import java.io.IOException;
import java.util.function.Supplier;

public abstract class CommandNode extends CommandNodeBase {

    public CommandNode (
        @NotNull String name,
        @Nullable Parameters parameters,
        @Nullable Supplier<SimpleDocumentation> documentationSupplier ) {

        super ( name, parameters, documentationSupplier );
    }

    public abstract @Nullable CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException;
}
