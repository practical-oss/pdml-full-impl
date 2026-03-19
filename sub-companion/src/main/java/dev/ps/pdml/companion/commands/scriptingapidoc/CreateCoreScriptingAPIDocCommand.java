package dev.ps.pdml.companion.commands.scriptingapidoc;

/*
import dev.pp.core.basics.annotations.NotNull;
import dev.pp.core.basics.annotations.Nullable;
import dev.pp.core.commands.command.CommandSpec;
import dev.pp.core.parameters.parameters.Parameters;

import java.nio.file.Path;
 */

@Deprecated
public class CreateCoreScriptingAPIDocCommand{}

/*
public class CreateCoreScriptingAPIDocCommand {

    public static final @NotNull CommandSpec<Path,Void> COMMAND_SPEC = CommandSpec.builder (
        "create_core_scripting_API_doc", CreateCoreScriptingAPIDocCommand::execute )
        .alternativeName ( "cad" )
        .inputParameters ( ScriptingAPIDocParameters.SPECS )
        .documentation (
        "Create Core Scripting API Documentation",
            null,
            null )
        .build();

    public static Void execute ( @Nullable Parameters<Path> parameters ) throws Exception {

        assert parameters != null;

        @NotNull Path sourcesDirectory = parameters.nonNullValue ( ScriptingAPIDocParameters.SOURCE_DIRECTORY );
        @NotNull Path outputFile = parameters.nonNullValue ( ScriptingAPIDocParameters.OUTPUT_FILE );

        // ScriptingAPIDocCreator.createCoreDoc ( sourcesDirectory, outputFile );
        throw new RuntimeException ( "This operation isn't implemented anymore." );

        // return null;
    }
}

 */
