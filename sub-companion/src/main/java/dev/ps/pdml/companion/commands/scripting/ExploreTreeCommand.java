package dev.ps.pdml.companion.commands.scripting;

import dev.ps.prt.command.Command;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.pdml.utils.scripting.TreeExplorerScriptUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pjse.PjseConfig;
import dev.ps.pjse.util.SourceCodeFileUtil;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;
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
import static dev.ps.pdml.companion.commands.SharedParameters.OPTIONAL_PDML_INPUT_FILE;

public class ExploreTreeCommand extends Command {

    public static final @NotNull Parameter<Path> EXPLORER_JAVA_SOURCE_CODE_FILE =
        NewParam.filePath (
            "explorer",
            List.of ( "e" ),
            null,
            () -> new SimpleDocumentation (
                "Explorer Java Source Code File",
                "The path of the file that contains the Java source code of the explorer.",
                "utils/my-explorer.java" ) );

    public static final @NotNull ExploreTreeCommand COMMAND = new ExploreTreeCommand();


    private ExploreTreeCommand() {
        super (
            "explore_tree", "et",
            new Parameters ( OPTIONAL_PDML_INPUT_FILE, EXPLORER_JAVA_SOURCE_CODE_FILE ),
            () -> new SimpleDocumentation (
                "Explore a PDML AST With Java Source Code",
                "",
                APP_NAME + " explore_tree -i data/data.pdml -e utils/my-explorer.java" ) );
    }


    public @NotNull CommandOutput execute ( @Nullable Arguments arguments ) {

        assert arguments != null;

        @Nullable Path inputFile = arguments.nullableCastedValue ( OPTIONAL_PDML_INPUT_FILE.name() );
        @NotNull Path explorerFile = arguments.nonNullCastedValue ( EXPLORER_JAVA_SOURCE_CODE_FILE.name() );
        // execute ( inputFile, outputFile, transformerFile );

        // DebugUtils.writeNameValue ( "inputFile", inputFile );

        boolean isOnlyJavaMethodBodyCode = SourceCodeFileUtil.isJavaSourceCodeSnippetFile ( explorerFile );
        // DebugUtils.writeNameValue ( "isOnlyJavaMethodBodyCode", isOnlyJavaMethodBodyCode );
        // TextResourceReader textResourceReader = TextFileReaderUtil.getUTF8FileOrStdinReader ( inputFile );
        // ExplorerUtil.explorePdmlFile ( inputFile, explorerFile, isOnlyJavaMethodBodyCode );
        try {
            TreeExplorerScriptUtil.exploreCode (
                TextResourceReader.createForOptionalFilePathOrStdin ( inputFile ),
                new TextResourceReader ( explorerFile ), isOnlyJavaMethodBodyCode, PjseConfig.DEFAULT_CONFIG );
        } catch ( Exception e ) {
            return new ErrorCommandOutput ( e );
        }

        return VoidCommandOutput.INSTANCE;
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
