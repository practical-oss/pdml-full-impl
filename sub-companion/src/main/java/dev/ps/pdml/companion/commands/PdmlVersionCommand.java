package dev.ps.pdml.companion.commands;

import dev.ps.prt.command.Command;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.data.PdmlVersion;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.VoidCommandOutput;

public class PdmlVersionCommand {

    private static final @NotNull String NAME = "version";

    public static final @NotNull Command COMMAND = new Command (
        NAME,
        null,
        () -> new SimpleDocumentation (
            "Show PDML Version",
            "Write the PDML version number to the standard output device (STDOUT).",
            PdmlCommands.APP_NAME + " " + NAME ) ) {

            public @NotNull VoidCommandOutput execute ( @Nullable Arguments arguments ) {
                System.out.println ( PdmlVersion.VERSION_AND_DATE );
                return VoidCommandOutput.INSTANCE;
            }
        };
}
