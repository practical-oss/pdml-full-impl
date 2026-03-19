package dev.ps.pdml.companion.commands.scriptingapidoc;

/*
import dev.pp.core.basics.annotations.NotNull;
import dev.pp.core.basics.annotations.Nullable;
import dev.pp.core.commands.command.CommandSpec;
import dev.pp.core.parameters.parameters.Parameters;

import java.nio.file.Path;
 */

@Deprecated
public class CreateExtensionsScriptingAPIDocCommand{}
/*
public class CreateExtensionsScriptingAPIDocCommand {

    public static final @NotNull CommandSpec<Path,Void> COMMAND_SPEC = CommandSpec.<Path,Void>builder (
        "create_extensions_scripting_API_doc", CreateExtensionsScriptingAPIDocCommand::execute )
        .alternativeName ( "ead" )
        .inputParameters ( ScriptingAPIDocParameters.SPECS )
        .documentation (
        "Create Extensions Scripting API Documentation",
            null,
            null )
        .build();

    public static Void execute ( @Nullable Parameters<Path> parameters ) throws Exception {

        assert parameters != null;

        @NotNull Path sourcesDirectory = parameters.nonNullValue ( ScriptingAPIDocParameters.SOURCE_DIRECTORY );
        @NotNull Path outputFile = parameters.nonNullValue ( ScriptingAPIDocParameters.OUTPUT_FILE );

        // ScriptingAPIDocCreator.createExtensionsDoc ( sourcesDirectory, outputFile );
        throw new RuntimeException ( "This operation isn't implemented anymore." );

        // return null;
    }
}
 */
