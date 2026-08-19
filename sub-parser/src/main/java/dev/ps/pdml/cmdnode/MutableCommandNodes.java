package dev.ps.pdml.cmdnode;

import dev.ps.pdml.cmdnode.scripting.DefinitionCommandNode;
import dev.ps.pdml.cmdnode.scripting.InsertExpressionCommandNode;
import dev.ps.pdml.cmdnode.scripting.ScriptCommandNode;
import dev.ps.pdml.cmdnode.utils.*;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.namedobject.MutableMultiNamedObjects;

public class MutableCommandNodes extends MutableMultiNamedObjects<MutableCommandNodes, CommandNodes, CommandNodeBase> {

    public static final @NotNull MutableCommandNodes STANDARD_COMMAND_NODES = new MutableCommandNodes()
        // Utilities
        .append ( DefineConstantsCommandNode.INSTANCE )
        .append ( InsertConstantCommandNode.INSTANCE )
        .append ( InsertFileCommandNode.INSTANCE )
        .append ( InsertURLCommandNode.INSTANCE )
        .append ( InsertOSEnvVarCommandNode.INSTANCE )
        .append ( InsertOSCommandOutputCommandNode.INSTANCE )
        .append ( InsertGUIInputCommandNode.INSTANCE )
        // Scripting
        .append ( InsertExpressionCommandNode.INSTANCE )
        .append ( ScriptCommandNode.INSTANCE )
        .append ( DefinitionCommandNode.INSTANCE );


    public MutableCommandNodes() {
        super();
        this.methodResultObject = this;
    }


    public @Nullable CommandNodes toImmutableOrNull () {
        return new CommandNodes ( list );
    }
}
