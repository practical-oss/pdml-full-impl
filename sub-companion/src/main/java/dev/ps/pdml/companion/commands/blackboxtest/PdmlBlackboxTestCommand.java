package dev.ps.pdml.companion.commands.blackboxtest;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.prt.parameter.Parameters;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;

import java.nio.file.Path;

public class PdmlBlackboxTestCommand extends CLICommand {

    private static final @NotNull Parameter<Path> PDML_EXECUTION_FILE_PATH_PARAMETER =
        Parameter.<Path>builder()
            .names ( "pdml-execution-file-path", "f" )
            .typePath()
            .isPositional ( true )
            .title ( "PDML Execution File Path" )
            .description ( "The path to an executable file that starts PDML." )
            .examples ( "path/to/pdml/bin/pdmlc.bat" ).build();

    private static final @NotNull Parameter<Boolean> INCLUDE_ERROR_TESTS_PARAMETER =
        ParameterBuilder.ofFlag ( "include_error_tests", "e" )
            .title ( "Include Error Tests" ).build();
            // .description ( "The path to an executable file that starts PDML." )


    public static final @NotNull PdmlBlackboxTestCommand INSTANCE = new PdmlBlackboxTestCommand ();

    private PdmlBlackboxTestCommand () {
        super (
            "blackbox-test", "bt",
            new Parameters ( PDML_EXECUTION_FILE_PATH_PARAMETER, INCLUDE_ERROR_TESTS_PARAMETER ),
            () -> new SimpleDocumentation (
                "Run PDML Blackbox Test",
                "Run PDMLC tests to see if everything works fine.",
                PdmlcApplication.CLI_APP_NAME + " bt -e path/to/pdml/bin/pdmlc.bat" ) );
    }


    public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @NotNull Path executableFilePath = arguments.nonNullCastedValue ( PDML_EXECUTION_FILE_PATH_PARAMETER );
        boolean includeErrorTests = arguments.nonNullCastedValue ( INCLUDE_ERROR_TESTS_PARAMETER );

        PdmlBlackboxTester tester = new PdmlBlackboxTester ( executableFilePath );
        tester.runTests ( includeErrorTests );

        // TODO
        return SuccessCLICommandOutput.ofVoid();
    }
}
