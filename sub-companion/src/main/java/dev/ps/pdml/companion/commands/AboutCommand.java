package dev.ps.pdml.companion.commands;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.application.CLIApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.prt.command.cli.CLICommandBuilder;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.os.OSDirectories;
import dev.ps.shared.basics.utilities.os.OSName;
import dev.ps.shared.basics.utilities.string.HTextAlign;
import dev.ps.shared.basics.utilities.string.StringAligner;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.prt.argument.Arguments;

public class AboutCommand {

    private static final @NotNull String NAME = "about";

    public static final @NotNull CLICommand INSTANCE = new CLICommandBuilder ( NAME )
        .executor ( AboutCommand::execute )
        .title ( "Info About PDML" )
        .description ( "Show general information about PDML." )
        .examples ( PdmlcApplication.CLI_APP_NAME + " " + NAME )
        .build();


    private static @NotNull SuccessCLICommandOutput<Void> execute ( @Nullable Arguments arguments ) {

        StringBuilder sb = new StringBuilder();
        CLIApplication app = PdmlcApplication.INSTANCE;

        append ( "Application", app.name(), sb );
        append ( "CLI name", app.cliAppName(), sb );
        append ( "Version", app.version(), sb );
        append ( "Published", app.published(), sb );
        append ( "Author", app.author(), sb );
        append ( "Website", app.websiteURL (), sb );
        append ( "Source code (GPL2)", app.sourceCodeURL (), sb );
        // append ( "Shared data dir.", PMLCResources.ROOT_DIRECTORY.toString(), sb );
        append ( "Working dir.", OSDirectories.currentWorkingDirectory().toString(), sb );
        append ( "OS name", OSName.name (), sb );
        append ( "Program path", ProcessHandle.current().info().command().orElse ( "unknown" ), sb );
        // TODO? append ( "Program args", Arrays.toString ( ProcessHandle.current().info().arguments().orElse ( new String[]{"none"} ) ), sb );
        append ( "Java version", System.getProperty ( "java.version" ), sb );

        System.out.println ( sb.toString() );

        return SuccessCLICommandOutput.ofVoid();
    }

    private static void append ( @NotNull String label, @Nullable Object value, @NotNull StringBuilder sb ) {

        sb.append ( StringAligner.align ( label + ":", 20, HTextAlign.RIGHT ) );
        sb.append ( " " );
        sb.append ( value != null ? value.toString() : "Unknown" );
        sb.append ( StringConstants.OS_LINE_BREAK );
    }
}
