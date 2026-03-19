package dev.ps.pdml.companion.commands.list;

import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.PdmlCommandsHelper;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.utils.lists.NodeNamesWriterUtil;
import dev.ps.pdml.utils.lists.TextLeavesWriterUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
import dev.ps.shared.text.ioresource.writer.TextResourceWriter;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CommandOutput;
import dev.ps.prt.command.output.ErrorCommandOutput;
import dev.ps.prt.command.output.VoidCommandOutput;
import dev.ps.prt.parameter.NewParam;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.nio.file.Path;
import java.util.List;

import static dev.ps.pdml.companion.commands.PdmlCommands.APP_NAME;
import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class ListTextsCommand {

    public static final @NotNull String NAME = "list_texts";
    public static final @NotNull String SHORT_NAME = "lt";

    private static final @NotNull Parameter<Boolean> SORT_PARAMETER = NewParam.ofBoolean (
        "sort",
        List.of ( "s" ),
        () -> false,
        () -> new SimpleDocumentation (
            "Sort",
            "Sort alphabetically",
            "--sort true" ) );

    private static final @NotNull Parameter<Boolean> DISTINCT_PARAMETER = NewParam.ofBoolean (
        "distinct",
        List.of ( "d" ),
        () -> false,
        () -> new SimpleDocumentation (
            "Distinct",
            "List only distinct values (i.e. if the same value occurs several times, only the first occurrence is included in the list).",
            "--distinct true" ) );

    private static final @NotNull Parameter<String> SEPARATOR_PARAMETER = NewParam.stringOrNull (
        "separator",
        List.of ( "p" ),
        () -> StringConstants.OS_LINE_BREAK,
        () -> new SimpleDocumentation (
            "Separator",
            "The separator used to separate list elements (default is line break).",
            "--separator \", \"" ) );

    static final @NotNull Parameters PARAMETERS = new Parameters (
        OPTIONAL_PDML_INPUT_FILE, OPTIONAL_TEXT_OUTPUT_FILE,
        SORT_PARAMETER, DISTINCT_PARAMETER, SEPARATOR_PARAMETER );

    public static final @NotNull Command COMMAND = new Command (
        NAME, SHORT_NAME,
        PARAMETERS,
        () -> new SimpleDocumentation (
            "List Text Leaves in a PDML Document",
            "Create a list of text leaves contained in a PDML document.",
            APP_NAME + " lt -i input/document.pdml -o output/texts.txt" ) ) {

        public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {
            return executeForTextsOrNames ( false, arguments );
        }
    };

    public static @NotNull CommandOutput executeForTextsOrNames (
        boolean forNames,
        @Nullable Arguments arguments ) {

        assert arguments != null;

        // DebugUtils.writeNameValue ( "arguments", arguments );

        @Nullable Path inputFile = arguments.nullableCastedValue ( OPTIONAL_PDML_INPUT_FILE );
        @Nullable Path outputFile = arguments.nullableCastedValue ( OPTIONAL_TEXT_OUTPUT_FILE );
        boolean sort = arguments.nonNullCastedValue ( SORT_PARAMETER );
        boolean distinct = arguments.nonNullCastedValue ( DISTINCT_PARAMETER );
        @Nullable String separator = arguments.nullableCastedValue ( SEPARATOR_PARAMETER );

        try ( TextResourceReader pdmlReader =
                  TextResourceReader.createForOptionalFilePathOrStdin ( inputFile );
              TextResourceWriter textWriter =
                  TextResourceWriter.createForOptionalFilePathOrStdout ( outputFile, true ) ) {

            if ( forNames ) {
                NodeNamesWriterUtil.writeNames (
                    pdmlReader, PdmlParserConfig.defaultConfig(),
                    textWriter, separator,
                    sort, distinct );
            } else {
                TextLeavesWriterUtil.writeTexts (
                    pdmlReader, PdmlParserConfig.defaultConfig (),
                    textWriter, separator,
                    sort, distinct );
            }

            PdmlCommandsHelper.fileCreatedMessageToStdout ( textWriter );
        } catch ( Exception e ) {
            return new ErrorCommandOutput ( e );
        }

        return VoidCommandOutput.INSTANCE;
    }
}
