package dev.ps.pdml.companion.commands;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.pdml.data.util.DemoDocs;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.prt.command.cli.CLICommandBuilder;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.prt.command.output.FailureCLICommandOutput;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;

import java.io.IOException;
import java.nio.file.Path;

public class CreateDemoDocsCommand {

    public static final @NotNull CLICommand INSTANCE = new CLICommandBuilder ( "create-demo-docs", "demos" )
        .executor ( CreateDemoDocsCommand::execute )
        .title ( "Create PDML Demo Documents" )
        .description ( """
            This command creates two .pdml demo files in the current working directory.
            One file uses only Core PDML, while the other file uses PDML extensions.""" )
        .examples ( PdmlcApplication.CLI_APP_NAME + " demos" )
        .build();


    private static @NotNull CLICommandOutput execute ( @Nullable Arguments arguments )  {

        try {
            writeDemoDoc ( DemoDocs.corePdmlDemoDoc(), DemoDocs.CORE_PDML_DEMO_FILE_NAME );
            writeDemoDoc ( DemoDocs.pdmlExtensionsDemoDoc(), DemoDocs.PDML_EXTENSIONS_DEMO_FILE_NAME );

            System.out.println ( "The following files have been created in the current working directory (" +
                System.getProperty ( "user.dir" ) + "):" );
            System.out.println ( DemoDocs.CORE_PDML_DEMO_FILE_NAME );
            System.out.println ( DemoDocs.PDML_EXTENSIONS_DEMO_FILE_NAME );

            return SuccessCLICommandOutput.ofVoid();

        } catch ( IOException e ) {
            return FailureCLICommandOutput.ofException ( e );
        }
    }

    private static void writeDemoDoc ( @NotNull String doc, @NotNull Path filePath ) throws IOException {
        TextFileWriterUtil.writeStringToUTF8File ( doc, filePath, false );
    }
}
