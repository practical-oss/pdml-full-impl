package dev.ps.pdml.cmdnode.scripting;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.cmdnode.CommandNodeExecutorContext;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.scriptingbase.env.ScriptingException;

public class ScriptingCommandsUtil {

    protected static void scriptingError (
        @NotNull String message,
        @NotNull String id,
        @NotNull NodeTag nodeName,
        @NotNull ScriptingException scriptingException,
        @NotNull CommandNodeExecutorContext context ) throws PdmlException {

        // TODO explore 'scriptingException' to provide a better error message and precise error position
        throw context.error ( message, id, nodeName.location () );
    }
}
