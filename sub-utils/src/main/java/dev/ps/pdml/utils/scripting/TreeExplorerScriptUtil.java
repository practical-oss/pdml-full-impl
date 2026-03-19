package dev.ps.pdml.utils.scripting;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pjse.PjseConfig;
import dev.ps.pjse.util.interfaces.FailableConsumerUtil;
import dev.ps.shared.text.ioresource.reader.TextResourceReader;

import java.nio.file.Path;

public class TreeExplorerScriptUtil {

    /*
    public static void explorePdmlFile (
        @NotNull Path pdmlCodeFile,
        @NotNull Path explorerJavaSourceCodeFile,
        boolean isOnlyJavaMethodBodyCode ) throws Exception {

        try ( Reader pdmlReader = TextFileReaderUtil.getUTF8FileReader ( pdmlCodeFile );
              Reader javaSourceCodeReader = TextFileReaderUtil.getUTF8FileReader ( explorerJavaSourceCodeFile ) ) {

            exploreCode (
                pdmlReader,
                new File_TextResource ( pdmlCodeFile ),
                javaSourceCodeReader,
                isOnlyJavaMethodBodyCode );
        }
    }
     */

    public static void explorePdmlFile (
        @NotNull Path pdmlCodeFile,
        @NotNull Path explorerJavaSourceCodeFile,
        boolean isOnlyJavaMethodBodyCode,
        @NotNull PjseConfig psjeConfig ) throws Exception {

        try ( TextResourceReader pdmlReader = new TextResourceReader ( pdmlCodeFile );
              TextResourceReader javaSourceCodeReader = new TextResourceReader ( explorerJavaSourceCodeFile ) ) {

            exploreCode ( pdmlReader, javaSourceCodeReader, isOnlyJavaMethodBodyCode, psjeConfig );
        }
    }

    /*
    public static void exploreCode (
        @NotNull Reader pdmlCodeReader,
        @Nullable TextResource pdmlCodeTextResource,
        @NotNull Reader javaSourceCodeReader,
        // @Nullable TextResource javaSourceCodeTransformerTextResource,
        boolean isOnlyJavaMethodBodyCode ) throws Exception {

        @NotNull BranchNode rootNode = PdmlParserUtil.parseReader ( pdmlCodeReader, pdmlCodeTextResource );
        exploreTree ( rootNode, javaSourceCodeReader, isOnlyJavaMethodBodyCode );
    }
     */

    public static void exploreCode (
        @NotNull TextResourceReader pdmlCodeReader,
        @NotNull TextResourceReader javaSourceCodeReader,
        boolean isOnlyJavaMethodBodyCode,
        @NotNull PjseConfig psjeConfig ) throws Exception {

        @NotNull TaggedNode rootNode = ParseASTUtil.parseReader (
            pdmlCodeReader, PdmlParserConfig.defaultConfig() );
        exploreTree ( rootNode, javaSourceCodeReader, isOnlyJavaMethodBodyCode, psjeConfig );
    }

    public static void exploreTree (
        @NotNull TaggedNode rootNode,
        @NotNull TextResourceReader javaSourceCodeReader,
        boolean isOnlyJavaMethodBodyCode,
        @NotNull PjseConfig psjeConfig ) throws Exception {

        if ( isOnlyJavaMethodBodyCode ) {
            FailableConsumerUtil.callAcceptMethodFromMethodBodySourceCode (
                javaSourceCodeReader, "rootNode", TaggedNode.class, rootNode, psjeConfig );
        } else {
            FailableConsumerUtil.callAcceptMethodInClassSourceCode (
                javaSourceCodeReader, "pdml.TreeExplorer", rootNode, psjeConfig );
        }
    }
}
