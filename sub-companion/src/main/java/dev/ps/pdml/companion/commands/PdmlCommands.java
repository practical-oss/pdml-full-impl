package dev.ps.pdml.companion.commands;

import dev.ps.prt.command.Commands;
import dev.ps.prt.command.builtin.CommandInfoCommand;
import dev.ps.prt.command.cli.CLICommandExecutor;
import dev.ps.pdml.companion.commands.html.PdmlToHtmlCommand;
import dev.ps.pdml.companion.commands.json.JsonToPdmlCommand;
import dev.ps.pdml.companion.commands.json.PdmlToJsonCommand;
import dev.ps.pdml.companion.commands.list.ListNodeNamesCommand;
import dev.ps.pdml.companion.commands.list.ListTextsCommand;
import dev.ps.pdml.companion.commands.scripting.ExploreTreeCommand;
import dev.ps.pdml.companion.commands.tocore.PdmlToCorePdmlCommand;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pdml.companion.commands.xml.PdmlToXMLCommand;
import dev.ps.pdml.companion.commands.xml.XMLToPdmlCommand;
import dev.ps.prt.command.output.UncheckedExceptionCommandOutputHandler;
import dev.ps.prt.command.output.WriteToStdOutErrCommandOutputHandler;

public class PdmlCommands {

    public static final @NotNull String APP_NAME = "pdmlc";

    public static final @NotNull Commands COMMANDS = Commands.builder()
        .append ( ListTextsCommand.COMMAND )
        .append ( ListNodeNamesCommand.COMMAND )
        .append ( CheckPdmlDocsCommand.COMMAND )
        .append ( PdmlToCorePdmlCommand.COMMAND )
        .append ( PdmlToHtmlCommand.COMMAND )
        .append ( PdmlToJsonCommand.COMMAND )
        .append ( PdmlToXMLCommand.COMMAND )
        .append ( JsonToPdmlCommand.COMMAND )
        .append ( XMLToPdmlCommand.COMMAND )
        // .add ( CreateCoreScriptingAPIDocCommand.COMMAND_SPEC )
        // .add ( CreateExtensionsScriptingAPIDocCommand.COMMAND_SPEC )
        .append ( ExploreTreeCommand.COMMAND )
        // .add ( TransformCommand.COMMAND_SPEC );

        .append ( PdmlVersionCommand.COMMAND )
        .append ( PdmlInfoCommand.COMMAND )

        .appendCommandInfoCommand (
            APP_NAME,
            ListTextsCommand.COMMAND.name() )
        .appendCommandsInfoCommand ( APP_NAME )
        .appendHelpCommand (
            APP_NAME,
            APP_NAME + " " + ListTextsCommand.COMMAND.name() + " -i doc.pdml -o result.txt",
            APP_NAME + " " + CommandInfoCommand.NAME + " " + ListTextsCommand.COMMAND.name() +
            "\nor (using short names):\n" +
            APP_NAME + " " + CommandInfoCommand.SHORT_NAME + " " + ListTextsCommand.SHORT_NAME)
        .build();

    public static void runCommand ( String[] args, boolean exitSystem ) {
        CLICommandExecutor.executeAndHandleOutput ( args, COMMANDS,
            new WriteToStdOutErrCommandOutputHandler ( exitSystem ),
            UncheckedExceptionCommandOutputHandler.DEFAULT, APP_NAME, null );
    }
}
