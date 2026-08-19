package dev.ps.pdml.companion.commands.list;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.companion.commands.CommandsHelper;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.utils.lists.NodeNamesWriterUtil;
import dev.ps.pdml.utils.lists.TextLeavesWriterUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.prt.util.ReaderResourceUtil;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.prt.command.output.FailureCLICommandOutput;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;
import dev.ps.shared.text.ioresource.writer.WriterResource;
import dev.ps.prt.util.WriterResourceUtil;

import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class ListTextsCommand {

    public static final @NotNull String SHORT_NAME = "texts";

    private static final @NotNull Parameter<@NotNull Boolean> SORT_PARAMETER =
        ParameterBuilder.ofFlag ( "sort", "s" )
            .title ( "Sort" )
            .description ( "If this parameter is set to true then the text leaves are sorted alphabetically." )
            .examples ( "--sort\n-s" ).build();

    private static final @NotNull Parameter<@NotNull Boolean> DISTINCT_PARAMETER =
        ParameterBuilder.ofFlag ( "distinct", "d" )
            .title ( "Distinct" )
            .description ( "If this parameter is set to true then only distinct values are listed (i.e. if the same value occurs several times, only the first occurrence is included in the list)." )
            .examples ( "--distinct\n-d" ).build();

    private static final @NotNull Parameter<@Nullable String> SEPARATOR_PARAMETER =
        ParameterBuilder.ofStringOrNull ( "separator", "p" )
            .defaultValue ( StringConstants.OS_LINE_BREAK )
            .title ( "Separator" )
            .description ( "The separator used to separate list elements (default is line break)." )
            .examples ( "--separator \", \"" ).build();

    /* TODO
    private static final @NotNull Parameter<Boolean> INCLUDE_WHITESPACE_TEXTS = ParameterBuilder
        .ofFlag ( "include_whitespace_texts", "w" )
        .title ( "Include Whitespace Texts" )
        .description ( "A flag to specify whether texts that consist only of whitespace (spaces, tabs, and line breaks) should be included or excluded in the result" ).build();
     */

    static final @NotNull Parameters PARAMETERS = new Parameters (
        PDML_INPUT_FILE_OR_STDIN, TEXT_OUTPUT_FILE_OR_STDOUT,
        SORT_PARAMETER, DISTINCT_PARAMETER, SEPARATOR_PARAMETER );

    public static final @NotNull CLICommand INSTANCE = new CLICommand (
        "list-texts", SHORT_NAME,
        PARAMETERS,
        () -> new SimpleDocumentation (
            "List Texts in a PDML Document",
            "Create a list of text leaves contained in a PDML document.",
            PdmlcApplication.CLI_APP_NAME + " list-texts -i input/document.pdml -o output/texts.txt" ) ) {

        public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {
            return executeForTextsOrNames ( false, arguments );
        }
    };

    public static @NotNull CLICommandOutput executeForTextsOrNames (
        boolean forNames,
        @Nullable Arguments arguments ) {

        assert arguments != null;

        boolean sort = arguments.nonNullCastedValue ( SORT_PARAMETER );
        boolean distinct = arguments.nonNullCastedValue ( DISTINCT_PARAMETER );
        @Nullable String separator = arguments.nullableCastedValue ( SEPARATOR_PARAMETER );

        try {
            ReaderResource pdmlReaderResource = ReaderResourceUtil.createForFileOrStdinArgument (
                arguments, PDML_INPUT_FILE_OR_STDIN );
            WriterResource textWriterResource = WriterResourceUtil.createForFileOrStdoutArgument (
                arguments, TEXT_OUTPUT_FILE_OR_STDOUT, true );

            if ( forNames ) {
                NodeNamesWriterUtil.writeNames (
                    pdmlReaderResource, PdmlParserConfig.defaultConfig(),
                    textWriterResource, separator,
                    sort, distinct );
            } else {
                TextLeavesWriterUtil.writeTexts (
                    pdmlReaderResource, PdmlParserConfig.defaultConfig (),
                    textWriterResource, separator,
                    sort, distinct );
            }

            CommandsHelper.fileCreatedMessageToStdout ( textWriterResource );

        } catch ( Exception e ) {
            return FailureCLICommandOutput.ofException ( e );
        }

        return SuccessCLICommandOutput.ofVoid();
    }
}
