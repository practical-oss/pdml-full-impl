package dev.ps.pdml.companion.commands;

import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.data.PdmlVersion;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.os.OSDirectories;
import dev.ps.shared.basics.utilities.os.OSName;
import dev.ps.shared.basics.utilities.string.HTextAlign;
import dev.ps.shared.basics.utilities.string.StringAligner;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.VoidCommandOutput;

public class PdmlInfoCommand {

    private static final @NotNull String NAME = "info";

    public static final @NotNull Command COMMAND = new Command (
        NAME,
        null,
        () -> new SimpleDocumentation (
            "Info About PDML",
            "Show general information about PDML.",
            PdmlCommands.APP_NAME + " " + NAME ) ) {

        public @NotNull VoidCommandOutput execute ( @Nullable Arguments arguments ) {

            StringBuilder sb = new StringBuilder();

            append ( "Application name", PdmlVersion.APPLICATION_NAME, sb );
            append ( "Short name", PdmlVersion.APPLICATION_SHORT_NAME, sb );
            append ( "Version", PdmlVersion.VERSION, sb );
            append ( "Version date", PdmlVersion.VERSION_DATE, sb );
            // append ( "Shared data dir.", PMLCResources.ROOT_DIRECTORY.toString(), sb );
            append ( "Working dir.", OSDirectories.currentWorkingDirectory().toString(), sb );
            append ( "OS name", OSName.name (), sb );
            append ( "Java version.", System.getProperty ( "java.version" ), sb );

            System.out.println ( sb.toString() );

            return VoidCommandOutput.INSTANCE;
        }
    };

    private static void append ( @NotNull String label, @NotNull String value, @NotNull StringBuilder sb ) {

        sb.append ( StringAligner.align ( label + ":", 20, HTextAlign.RIGHT ) );
        sb.append ( " " );
        sb.append ( value );
        sb.append ( StringConstants.OS_LINE_BREAK );
    }
}
