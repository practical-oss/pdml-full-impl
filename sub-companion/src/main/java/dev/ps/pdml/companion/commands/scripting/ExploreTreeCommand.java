package dev.ps.pdml.companion.commands.scripting;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.prt.command.cli.CLICommand;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.utils.scripting.TreeExplorerScriptUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pjse.PjseConfig;
import dev.ps.pjse.util.SourceCodeFileUtil;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.prt.util.ReaderResourceUtil;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.prt.command.output.FailureCLICommandOutput;
import dev.ps.prt.command.output.SuccessCLICommandOutput;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.SharedParameters.PDML_INPUT_FILE_OR_STDIN;

public class ExploreTreeCommand extends CLICommand {

    public static final @NotNull Parameter<Path> EXPLORER_JAVA_SOURCE_CODE_FILE =
        Parameter.<Path>builder()
            .names ( "explorer", "e" )
            .typePath()
            .title ( "Explorer Java Source Code File" )
            .description ( "The path of the file that contains the Java source code of the explorer." )
            .examples ( "-e utils/my-explorer.java" ).build();

    public static final @NotNull ExploreTreeCommand INSTANCE = new ExploreTreeCommand();


    private ExploreTreeCommand() {
        super (
            "explore-tree", "et",
            new Parameters ( PDML_INPUT_FILE_OR_STDIN, EXPLORER_JAVA_SOURCE_CODE_FILE ),
            () -> new SimpleDocumentation (
                "Explore a PDML AST Using Java Source Code",
                "",
                PdmlcApplication.CLI_APP_NAME + " explore-tree -i data/data.pdml -e utils/my-explorer.java" ) );
    }


    public @NotNull CLICommandOutput execute ( @Nullable Arguments arguments ) {

        try {
            assert arguments != null;
            ReaderResource pdmlReaderResource = ReaderResourceUtil.createForFileOrStdinArgument (
                arguments, PDML_INPUT_FILE_OR_STDIN );
            @NotNull Path explorerJavaFile = arguments.nonNullCastedValue ( EXPLORER_JAVA_SOURCE_CODE_FILE );
            ReaderResource javaReaderResource = new FileReaderResource ( explorerJavaFile );
            boolean isJavaSourceCodeSnippetFile = SourceCodeFileUtil.isJavaSourceCodeSnippetFile ( explorerJavaFile );

            TreeExplorerScriptUtil.exploreCode (
                pdmlReaderResource, javaReaderResource, isJavaSourceCodeSnippetFile, PjseConfig.DEFAULT_CONFIG );

        } catch ( Exception e ) {
            return FailureCLICommandOutput.ofException ( e );
        }

        return SuccessCLICommandOutput.ofVoid();
    }

/*
    public static void execute (
        @Nullable Path pdmlInputFile,
        @Nullable Path pdmlOutputFile,
        @NotNull Path javaSourceCodeTransformerFile ) throws Exception {

        // TODO
        assert pdmlInputFile != null;
        assert pdmlOutputFile != null;
        @NotNull TaggedNode originalRoot = PdmlTreeParserUtil.parseFileToTree ( pdmlInputFile );

        @Nullable TaggedNode transformedRoot =  FunctionalInterfaceUtil.executeMethodInSourceCodeFile (
            javaSourceCodeTransformerFile,
            PdmlTreeTransformer.class,
            new String[]{"node"},
            new Object[]{originalRoot},
            null,
            "pdml",
            "TransformerImpl",
            PjseConfig.DEFAULT_CONFIG );

        @Nullable TaggedNode transformedRoot = FunctionUtil.executeApplyMethodInSourceCodeFile (
            javaSourceCodeTransformerFile,
            "pdml.Transformer",
            originalRoot,
            "node",
            TaggedNode.class,
            TaggedNode.class,
            PjseConfig.DEFAULT_CONFIG );

        if ( transformedRoot != null ) {
            PdmlDataWriterUtil.writeToFile ( pdmlOutputFile, transformedRoot,
                new PdmlDataWriterConfig ( true, true ) );
        }
    }
*/
}
