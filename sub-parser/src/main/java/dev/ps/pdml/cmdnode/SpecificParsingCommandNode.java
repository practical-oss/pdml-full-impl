package dev.ps.pdml.cmdnode;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.prt.parameter.Parameters;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;

import java.io.IOException;
import java.util.function.Supplier;

public abstract class SpecificParsingCommandNode extends CommandNodeBase {

    public SpecificParsingCommandNode (
        @NotNull String name,
        @Nullable Parameters parameters,
        @Nullable Supplier<SimpleDocumentation> documentationSupplier ) {

        super ( name, parameters, documentationSupplier );
    }

    // returns text to be inserted
    // When this method is called, the node tag and a tag/value separator are consumed already
    // (e.g. ^s[exp 1+1] -> the reader is on "1+1"
    public abstract @Nullable CommandNodeResult execute (
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException;
}
