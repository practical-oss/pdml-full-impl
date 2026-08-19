package dev.ps.pdml.cmdnode.types;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.cmdnode.SpecificParsingCommandNode;
import dev.ps.pdml.cmdnode.CommandNodeExecutorContext;
import dev.ps.pdml.cmdnode.CommandNodeResult;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;

import java.io.IOException;

public class TypeCommandNode<T> extends SpecificParsingCommandNode {


    private final @NotNull PdmlType<T> type;
    public @NotNull PdmlType<T> type() { return type; }


    public TypeCommandNode ( @NotNull PdmlType<T> type ) {

        super ( type.getName(), null, () ->  new SimpleDocumentation (
            "Type " + type.getName(),
            "",
            "" ) );

        this.type = type;
    }


    @Override
    public @Nullable CommandNodeResult execute (
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeName ) throws IOException, PdmlException {

        type.parseValidateAndHandleObject (
            context.pdmlParser (), null, true );
        return null;
    }
}
