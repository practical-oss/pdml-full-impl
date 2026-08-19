package dev.ps.pdml.companion.commands.tocore;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.prt.command.cli.CLICommandBuilder;
import dev.ps.pdml.companion.commands.CommandsHelper;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.utils.FullPdmlToCorePdmlUtil;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.prt.util.ReaderResourceUtil;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.prt.command.output.FailureCLICommandOutput;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.shared.text.ioresource.writer.WriterResource;
import dev.ps.prt.util.WriterResourceUtil;

import static dev.ps.pdml.companion.commands.SharedParameters.*;

public class FullPdmlToCorePdmlCommand {

    // TODO
    // add flag remove-pretty-printing-whitespace
    // add flag trim-whitespace
    // add flag use-space-separator ('\n' -> ' ' for all separators)

    public static final @NotNull CLICommand INSTANCE = new CLICommandBuilder()
        .names ( "pdml-to-core-pdml", "p2c" )
        .parameter ( PDML_INPUT_FILE_OR_STDIN )
        .parameter ( PDML_OUTPUT_FILE_OR_STDOUT )
        .executor ( FullPdmlToCorePdmlCommand::execute )
        .title ( "Convert PDML (With Extensions) to Core PDML" )
        .description ( "Convert a PDML document using extensions to a Core PDML document (i.e. a standalone PDML document that doesn't use PDML extensions)." )
        .examples ( PdmlcApplication.CLI_APP_NAME + " p2c -i input/doc.pdml -o output/core_doc.pdml" )
        .build();

    private static @NotNull CLICommandOutput execute ( @Nullable Arguments arguments )  {

        try {
            assert arguments != null;
            ReaderResource readerResource = ReaderResourceUtil.createForFileOrStdinArgument (
                arguments, PDML_INPUT_FILE_OR_STDIN );
            WriterResource writerResource = WriterResourceUtil.createForFileOrStdoutArgument (
                arguments, PDML_OUTPUT_FILE_OR_STDOUT, true );

            FullPdmlToCorePdmlUtil.fullPdmlToCorePdml (
                readerResource, writerResource,
                PdmlParserConfig.defaultConfig(), PdmlNodeWriterConfig.DEFAULT_CONFIG,
                false );

            CommandsHelper.fileCreatedMessageToStdout ( writerResource );

        } catch ( Exception e ) {
            return FailureCLICommandOutput.ofException ( e );
        }

        return SuccessCLICommandOutput.ofVoid();
    }
}
