package dev.ps.pdml.companion.commands.scripting;

/*
import dev.pp.pdml.utils.scripting.TreeTransformerScriptUtil;
import dev.pp.core.basics.annotations.NotNull;
import dev.pp.core.basics.annotations.Nullable;
import dev.pp.core.commands.command.CommandSpec;
import dev.pp.core.datatype.CommonDataTypes;
import dev.pp.core.parameters.parameters.Parameters;
import dev.pp.core.parameters.parameterspec.ParameterSpec;
import dev.pp.core.parameters.parameterspecs.MutableParameterSpecs;
import dev.pp.core.parameters.parameterspecs.ParameterSpecs;
import dev.pp.pjse.util.SourceCodeFileUtil;

import java.nio.file.Path;

import static dev.pp.pdml.companion.commands.SharedParameters.OPTIONAL_PDML_INPUT_FILE_OLD;
import static dev.pp.pdml.companion.commands.SharedParameters.OPTIONAL_PDML_OUTPUT_FILE_OLD;
 */

@Deprecated
public class TransformCommand{}
// public class TransformCommand {

    /*
    public static final @NotNull ParameterSpec<Path> PDML_INPUT_FILE = CommonParameterSpecs.optionalInputFile (
        "PDML Input File",
        "The absolute or relative path of the PDML input file.",
        "data/original.pdml" );

    public static final @NotNull ParameterSpec<Path> PDML_OUTPUT_FILE = SharedParameterSpecs.optionalOutputFile (
        "PDML Output File",
        "The absolute or relative path of the PDML output file.",
        "data/transformed.pdml" );
    */

/*
    public static final @NotNull ParameterSpec<Path> JAVA_SOURCE_CODE_TRANSFORMER_FILE =
        ParameterSpec.builder (
            "transformer", CommonDataTypes.FILE_PATH )
            .alternativeName ( "t" )
            .documentation (
                "Java Source Code Transformer File",
                """
                    The absolute or relative path of the transformer file.
                    This file contains Java source code that implements the transformer.
                    """,
                "utils/my-transformer.java" )
            .build ();

    public static final @NotNull ParameterSpecs<Path> PARAMETERS = new MutableParameterSpecs<Path> ()
        .add ( OPTIONAL_PDML_INPUT_FILE_OLD )
        .add ( OPTIONAL_PDML_OUTPUT_FILE_OLD )
        .add ( JAVA_SOURCE_CODE_TRANSFORMER_FILE )
        .makeImmutable();


    public static final @NotNull CommandSpec<Path,Void> COMMAND_SPEC = CommandSpec.<Path,Void>builder (
        "transform", TransformCommand::execute )
        // .alternativeNames ( Set.of ( "pdml2xml" ) )
        .inputParameters ( PARAMETERS )
        .documentation ( "Transform a PDML document",
            "",
            "transform -i data/original.pdml -o data/transformed.pdml -t utils/transformer.java" )
        .build();

    public static Void execute ( @Nullable Parameters<Path> parameters ) throws Exception {

        assert parameters != null;

        @Nullable Path inputFile = parameters.value ( OPTIONAL_PDML_INPUT_FILE_OLD );
        @Nullable Path outputFile = parameters.value ( OPTIONAL_PDML_OUTPUT_FILE_OLD );
        @NotNull Path transformerFile = parameters.nonNullValue ( JAVA_SOURCE_CODE_TRANSFORMER_FILE );
        // execute ( inputFile, outputFile, transformerFile );
        boolean isOnlyJavaMethodBodyCode = SourceCodeFileUtil.isJavaSourceCodeSnippetFile ( transformerFile );
        TreeTransformerScriptUtil.transformFile ( inputFile, outputFile, transformerFile, isOnlyJavaMethodBodyCode );

        return null;
    }
 */

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
// }
