package dev.ps.pdml.companion.commands;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.SuccessCLICommandOutput;

import java.util.List;

public class VersionCommand {

    private static final @NotNull String NAME = "version";

    public static final @NotNull CLICommand INSTANCE = new CLICommand (
        NAME, List.of ( "--version", "-V" ),
        null,
        () -> new SimpleDocumentation (
            "Show PDML Version",
            "Write the PDML version number to the standard output device (STDOUT).",
            PdmlcApplication.CLI_APP_NAME + " " + NAME ) ) {

            public @NotNull SuccessCLICommandOutput<Void> execute ( @Nullable Arguments arguments ) {

                String result = PdmlcApplication.INSTANCE.versionAndDate();
                if ( result == null ) result = "Unknown";
                System.out.println ( result );
                return SuccessCLICommandOutput.ofVoid();
            }
        };
}
