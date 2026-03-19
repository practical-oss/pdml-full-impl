package dev.ps.pdml.ext.scripting;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.ExtensionNodeHandlerContext;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.scriptingbase.env.ScriptingException;

public class ScriptingHandlerUtil {

    protected static void scriptingError (
        @NotNull String message,
        @NotNull String id,
        @NotNull NodeTag nodeName,
        @NotNull ScriptingException scriptingException,
        @NotNull ExtensionNodeHandlerContext context ) throws PdmlException {

        // TODO explore 'scriptingException' to provide a better error message and precise error position
        throw context.error ( message, id, nodeName.startLocation() );
    }
}
