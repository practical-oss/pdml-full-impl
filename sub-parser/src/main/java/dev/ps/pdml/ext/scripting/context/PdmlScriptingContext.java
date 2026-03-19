package dev.ps.pdml.ext.scripting.context;

import dev.ps.shared.basics.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PdmlScriptingContext {


    private final @NotNull DocScriptingContext docScriptingContext;
    private final @NotNull Map<String, Object> userAttributes;


    public PdmlScriptingContext ( @NotNull DocScriptingContext docScriptingContext ) {
        this.docScriptingContext = docScriptingContext;
        this.userAttributes = new HashMap<>();
    }


    public DocScriptingContext doc() { return docScriptingContext; }

    public Map<String, Object> atts() { return userAttributes; }
}
