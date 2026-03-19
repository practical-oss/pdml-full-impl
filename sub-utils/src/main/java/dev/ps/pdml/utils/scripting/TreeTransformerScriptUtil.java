package dev.ps.pdml.utils.scripting;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.nio.file.Path;

@Deprecated
public class TreeTransformerScriptUtil {

    public static void transformFile (
        @Nullable Path pdmlInputFile,
        @Nullable Path pdmlOutputFile,
        @NotNull Path javaSourceCodeTransformerFile,
        boolean isOnlyJavaMethodBodyCode ) throws Exception {

        // TODO see TreeExplorerScriptUtil for better version
        throw new RuntimeException ( "Not yet implemented" );

/*
        Reader pdmlInputReader = null;
        Writer pdmlOutputWriter = null;
        Reader javaSourceCodeReader = null;
        try {
            // pdmlInputReader = TextFileIO.getUTF8FileOrStdinReader ( pdmlInputFile );
            pdmlInputReader = TextFileReaderUtil.createUTF8FileOrStdinReader ( pdmlInputFile );
            pdmlOutputWriter = TextFileIO.getUTF8FileOrStdoutWriter ( pdmlOutputFile );
            javaSourceCodeReader = TextFileReaderUtil.getUTF8FileReader ( javaSourceCodeTransformerFile );

            transform (
                pdmlInputReader,
                pdmlInputFile != null ? new File_TextResource ( pdmlInputFile ) : null,
                pdmlOutputWriter,
                javaSourceCodeReader,
                isOnlyJavaMethodBodyCode );
        } finally {
            // don't close if STDIN is used
            if ( pdmlInputFile != null && pdmlInputReader != null ) {
                pdmlInputReader.close();
            }
            // don't close if STDOUT is used
            if ( pdmlOutputFile != null && pdmlOutputWriter != null ) {
                pdmlOutputWriter.close();
            }
            if ( javaSourceCodeReader != null ) {
                javaSourceCodeReader.close();
            }
        }
 */
    }

/*
    public static void transform (
        @NotNull Reader pdmlInputReader,
        @Nullable TextResource pdmlInputTextResource,
        @NotNull Writer pdmlOutputWriter,
        @NotNull Reader javaSourceCodeTransformerReader,
        // @Nullable TextResource javaSourceCodeTransformerTextResource,
        boolean isOnlyJavaMethodBodyCode ) throws Exception {

        // @NotNull BranchNode originalRoot = PdmlTreeParserUtil.parseToTree ( pdmlInputReader, pdmlInputTextResource );
        @NotNull BranchNode originalRoot = PdmlParserUtil.parseReader ( pdmlInputReader, pdmlInputTextResource );
        @Nullable String transformerCode = StringUtils.stringFromReader ( javaSourceCodeTransformerReader );
        if ( transformerCode == null ) {
            // TODO error
            return;
        }

        PjseConfig config = PjseConfig.DEFAULT_CONFIG;
        @Nullable BranchNode transformedRoot;
        if ( isOnlyJavaMethodBodyCode ) {
            transformedRoot = FailableFunctionUtil.callApplyMethodFromMethodBodySourceCode (
                transformerCode, "node", BranchNode.class, BranchNode.class, originalRoot, config );
        } else {
            transformedRoot = FailableFunctionUtil.callApplyMethodInClassSourceCode (
                transformerCode, "pdml.Transformer", originalRoot, config );
        }

        if ( transformedRoot != null ) {
            PdmlNodeWriterUtil.write (
                pdmlOutputWriter, transformedRoot, false, new PdmlNodeWriterConfig ( true, PdmlWriterConfig.DEFAULT_CONFIG ) );
        }
    }
 */
}
