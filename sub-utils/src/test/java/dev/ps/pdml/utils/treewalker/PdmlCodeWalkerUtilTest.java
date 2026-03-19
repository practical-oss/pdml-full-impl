package dev.ps.pdml.utils.treewalker;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.utils.treewalker.handler.impl.CreateTree_ParserEventHandler;
import dev.ps.pdml.writer.node.PdmlNodeWriterUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdmlCodeWalkerUtilTest {

    @Test
    void walkCode() throws Exception {

        var code = "[root]";
        var eventHandler = new CreateTree_ParserEventHandler();
        TaggedNode rootnode = PdmlCodeWalkerUtil.walkCode ( code, eventHandler );
        assertEquals ( "root", rootnode.getTag ().qualifiedTag () );

        code = "[root2 [child foo bar]]";
        rootnode = PdmlCodeWalkerUtil.walkCode ( code, eventHandler );
        assertEquals ( "root2", rootnode.getTag ().qualifiedTag () );
        TaggedNode childNode = (TaggedNode) rootnode.getChildNodes().first();
        assertEquals ( "child", childNode.getTag ().qualifiedTag () );
        String text = childNode.toText();
        assertEquals ( "foo bar", text );

        String writtenCode = PdmlNodeWriterUtil.writeToString ( rootnode, false );
        assertEquals ( code, writtenCode );

        code = """
            [root [@ a1="v1" a2="v2"] foo bar]""";
        rootnode = PdmlCodeWalkerUtil.walkCode ( code, eventHandler );
        writtenCode = PdmlNodeWriterUtil.writeToString ( rootnode, false );
        assertEquals ( code, writtenCode );
    }

/*
    @Test
    void temp() throws Exception {

        ScriptingEnvironment scriptingEnvironment = new JavaScriptScriptingEnvironment ( true );
        ExtensionNodesHandler extensionNodesHandler = StandardExtensionNodeHandler.get ( scriptingEnvironment );
        // PdmlNodeSpecs<?> pdmlNodeSpecs = nodeSpecs.toPdmlNodeSpecs();
        PdmlNodeSpecs<?> pdmlNodeSpecs = null;

        PdmlParserConfig pdmlParserConfig = new PdmlParserConfigBuilder ()
            // .messageHandler ( messageHandler )
            .allowParenthesisAsAttributesDelimiters ( true )
            .nodeSpecs ( pdmlNodeSpecs )
            .extensionsHandler ( extensionNodesHandler )
            .build();

        // PMLCodeWalkerEventHandler eventHandler = new PMLCodeWalkerEventHandler (
        //     nodeSpecs, messageHandler );
        var eventHandler = new CreateTree_ParserEventHandler();


        // PdmlCodeWalker<PMLNode, DocumentNode> pdmlCodeWalker = new PdmlCodeWalker<> (
        //    PMLCodeReader, resource, currentLineNumber, currentColumnNumber, pdmlParserConfig, eventHandler );
        var code = """
            [doc
                Test
            ]
            """;
        var PMLCodeReader = new StringReader ( code );
        PdmlCodeWalker<BranchNode, BranchNode> pdmlCodeWalker = new PdmlCodeWalker<> (
            PMLCodeReader, new String_TextResource ( code ), null, null, pdmlParserConfig, eventHandler );

        PdmlReader pdmlReader = pdmlCodeWalker.getPdmlParser().getPdmlReader();
        scriptingEnvironment.addBinding ( new DocBinding ( pdmlReader ) );
        // scriptingEnvironment.addBinding ( new NodesBinding ( nodeSpecs ) );

        pdmlCodeWalker.walk();
        // return eventHandler.getResult();
        BranchNode docNode = eventHandler.getResult();
        assertEquals ( "doc", docNode.getName().qualifiedName() );
    }
 */
}
