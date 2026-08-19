package dev.ps.pdml.companion.commands.list;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CLICommandOutput;

public class ListTagsCommand {

    public static final @NotNull CLICommand INSTANCE = new CLICommand (
        "list-tags", "tags",
        ListTextsCommand.PARAMETERS,
        () -> new SimpleDocumentation (
            "List Tags in a PDML Document",
            "Create a list of node tags contained in a PDML document.",
            PdmlcApplication.CLI_APP_NAME + " tags -i input/document.pdml -o output/names.txt" ) ) {

        public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {
            return ListTextsCommand.executeForTextsOrNames ( true, arguments );
        }
    };
}
