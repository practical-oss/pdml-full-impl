package dev.ps.pdml.ext;

import dev.ps.pdml.ext.scripting.*;
import dev.ps.pdml.ext.types.PdmlTypes;
import dev.ps.pdml.ext.types.TypeNodeHandler;
import dev.ps.pdml.ext.utils.InsertConstantHandler;
import dev.ps.pdml.ext.utils.InsertFileHandler;
import dev.ps.pdml.ext.utils.InsertURLHandler;
import dev.ps.pdml.ext.utils.DefineConstantsHandler;
import dev.ps.pdml.ext.types.PdmlType;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO add as parameter to parser config (default = STANDARD_HANDLERS)
public class ExtensionNodeHandlers {


    public static final @NotNull ExtensionNodeHandlers STANDARD_HANDLERS =
        new ExtensionNodeHandlers().addStandardHandlers();


    private final @NotNull Map<String, ExtensionNodeHandler> map;


    public ExtensionNodeHandlers () {
        this.map = new HashMap<> ();
    }


    /*
    public @Nullable ExtensionNodeHandlerDelegate getOrNull (
        @NotNull String extensionKind,
        @NotNull String extensionName ) {

        return getOrNull ( ExtensionNodeHandlerDelegate.createIdentifier (
            extensionKind, extensionName ) );
    }
     */

    public @Nullable ExtensionNodeHandler getOrNull ( @NotNull String identifier ) {
        return map.get ( identifier );
    }

    /*
    public @Nullable Collection<ExtensionNodeHandler> getAll() {

        if ( ! map.isEmpty() ) {
            return map.values();
        } else {
            return null;
        }
    }
     */

    public @Nullable PdmlTypes getTypes() {

        PdmlTypes types = new PdmlTypes();
        for ( ExtensionNodeHandler handler : map.values() ) {
            if ( handler instanceof TypeNodeHandler<?> td ) {
                types.add ( td.getType() );
            }
        }

        return types.isEmpty() ? null : types;
    }

    public ExtensionNodeHandlers add ( @NotNull ExtensionNodeHandler handler ) {

        String name = handler.getExtensionName();
        if ( map.containsKey ( name ) ) {
            throw new IllegalStateException ( "Extension node '" + name + "' exists already." );
        }

        map.put ( name, handler );
        return this;
    }

    public ExtensionNodeHandlers addStandardHandlers() {

        addStandardUtilityHandlers ();
        addStandardTypesHandlers ();
        addStandardScriptingHandlers ();

        return this;
    }

    public ExtensionNodeHandlers addStandardUtilityHandlers() {

        add ( DefineConstantsHandler.INSTANCE );
        add ( InsertConstantHandler.INSTANCE );
        add ( InsertFileHandler.INSTANCE );
        add ( InsertURLHandler.INSTANCE );

        return this;
    }

    public ExtensionNodeHandlers addStandardTypesHandlers() {

        Collection<PdmlType<?>> types = PdmlTypes.STANDARD_TYPES.getAll();
        if ( types != null ) {
            for ( PdmlType<?> type : types ) {
                add ( new TypeNodeHandler<> ( type ) );
            }
        }

        return this;
    }

    public ExtensionNodeHandlers addStandardScriptingHandlers() {

        add ( InsertExpressionHandler.INSTANCE );
        add ( ScriptHandler.INSTANCE );
        add ( DefinitionHandler.INSTANCE );

        return this;
    }
}
