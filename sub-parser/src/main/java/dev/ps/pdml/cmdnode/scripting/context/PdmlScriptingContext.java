package dev.ps.pdml.cmdnode.scripting.context;

import dev.ps.pdml.cmdnode.CommandNode;
import dev.ps.pdml.cmdnode.MutableCommandNodes;
import dev.ps.shared.basics.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PdmlScriptingContext {


    private final @NotNull MutableCommandNodes commandNodes;
    private final @NotNull DocScriptingContext docScriptingContext;
    private final @NotNull Map<String, Object> userAttributes;


    public PdmlScriptingContext (
        @NotNull MutableCommandNodes commandNodes,
        @NotNull DocScriptingContext docScriptingContext ) {

        this.commandNodes = commandNodes;
        this.docScriptingContext = docScriptingContext;
        this.userAttributes = new HashMap<>();
    }


    public void addCommandNode ( @NotNull CommandNode commandNode ) {
        commandNodes.append ( commandNode );
    }

    public void removeCommandNode ( @NotNull String commandName ) {
        commandNodes.remove ( commandName );
    }

    public DocScriptingContext doc() { return docScriptingContext; }

    public Map<String, Object> atts() { return userAttributes; }
}
