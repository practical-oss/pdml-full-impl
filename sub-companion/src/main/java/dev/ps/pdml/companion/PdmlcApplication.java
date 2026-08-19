package dev.ps.pdml.companion;

import dev.ps.pdml.companion.commands.*;
import dev.ps.pdml.companion.commands.blackboxtest.PdmlBlackboxTestCommand;
import dev.ps.pdml.companion.commands.html.PdmlToHtmlTreeViewCommand;
import dev.ps.pdml.companion.commands.json.JsonToPdmlCommand;
import dev.ps.pdml.companion.commands.json.PdmlToJsonCommand;
import dev.ps.pdml.companion.commands.list.ListTagsCommand;
import dev.ps.pdml.companion.commands.list.ListTextsCommand;
import dev.ps.pdml.companion.commands.scripting.ExploreTreeCommand;
import dev.ps.pdml.companion.commands.tocore.FullPdmlToCorePdmlCommand;
import dev.ps.pdml.companion.commands.xml.PdmlToXMLCommand;
import dev.ps.pdml.companion.commands.xml.XMLToPdmlCommand;
import dev.ps.prt.application.CLIApplication;
import dev.ps.prt.command.cli.MutableCLICommands;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.URLUtil;

import java.time.LocalDate;

public class PdmlcApplication {

    public static final @NotNull String CLI_APP_NAME = "pdmlc";

    public static final @NotNull CLIApplication INSTANCE = createApp();

    private static @NotNull CLIApplication createApp() {

        MutableCLICommands commands = new MutableCLICommands();

        CLIApplication app = new CLIApplication (
            "PDML Companion",
            null,
            "0.81.0",
            LocalDate.parse ( "2026-08-19" ),
            "Christian Neumanns",
            URLUtil.create ( "https://pdml-lang.dev/" ),
            URLUtil.create ( "https://github.com/practical-oss/pdml-full-impl" ),
            null,
            commands,
            CLI_APP_NAME,
            CLI_APP_NAME + " " + ListTextsCommand.INSTANCE.name() + " --input doc.pdml --output result.txt",
            "pdmlc.color" );

        commands
            .append ( ListTextsCommand.INSTANCE )
            .append ( ListTagsCommand.INSTANCE )
            .append ( CreateDemoDocsCommand.INSTANCE )
            .append ( CheckPdmlDocsCommand.INSTANCE )
            .append ( FullPdmlToCorePdmlCommand.INSTANCE )
            .append ( PdmlToHtmlTreeViewCommand.INSTANCE )
            .append ( PdmlToJsonCommand.INSTANCE )
            .append ( PdmlToXMLCommand.INSTANCE )
            .append ( JsonToPdmlCommand.INSTANCE )
            .append ( XMLToPdmlCommand.INSTANCE )
            /*
            .add ( CreateCoreScriptingAPIDocCommand.COMMAND_SPEC )
            .add ( CreateExtensionsScriptingAPIDocCommand.COMMAND_SPEC )
             */
            .append ( ExploreTreeCommand.INSTANCE )
            // .add ( TransformCommand.COMMAND_SPEC );
            .append ( PdmlBlackboxTestCommand.INSTANCE )

            .append ( VersionCommand.INSTANCE )
            .append ( AboutCommand.INSTANCE )

            .appendCommandInfoCommand ( app, ListTextsCommand.INSTANCE.name() )
            .appendCommandsInfoCommand ( app )
            .appendHelpCommand ( app );

        return app;
    }
}
