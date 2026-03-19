package dev.ps.pdml.companion.commands.list;

import dev.ps.prt.command.Command;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CommandOutput;

import static dev.ps.pdml.companion.commands.PdmlCommands.APP_NAME;

public class ListNodeNamesCommand {

    public static final @NotNull Command COMMAND = new Command (
        "list_node_names", "ln",
        ListTextsCommand.PARAMETERS,
        () -> new SimpleDocumentation (
            "List Node Names in a PDML Document",
            "Create a list of node names contained in a PDML document.",
            APP_NAME + " ln -i input/document.pdml -o output/names.txt" ) ) {

        public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {
            return ListTextsCommand.executeForTextsOrNames ( true, arguments );
        }
    };
}
