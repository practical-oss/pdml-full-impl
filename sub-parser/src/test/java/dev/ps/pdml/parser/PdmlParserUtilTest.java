package dev.ps.pdml.parser;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.Node;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.pdml.data.util.TestDoc;
import dev.ps.pdml.parser.util.ParseASTUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdmlParserUtilTest {

    @Test
    void parseTestDoc() {

        String pdmlTestDoc = TestDoc.getPdmlTestDoc();
        // assertDoesNotThrow ( () -> PdmlParserUtil.parseString ( pdmlTestDoc ) );
    }

    @Test
    void parseString() throws Exception {

        TaggedNode rootNode = ParseASTUtil.parseString ( "[root]" );
        assertEquals ( "root", rootNode.getTag ().qualifiedTag () );

        assertThrows ( PdmlException.class, () ->
            ParseASTUtil.parseString ( "[root ]" ) );

        rootNode = ParseASTUtil.parseString ( "[root  ]" );
        assertEquals ( " ", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( "[root  a ]" );
        assertEquals ( " a ", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root
                [child_1
                    [child_11]
                    [child_12 text_12]
                ]
                [child_2
                    [child_21
                        [child_211 text 211]text_21[child_213]
                    ]
                ]
            ]
            """ );

        StringBuilder names = new StringBuilder();
        for ( Node node : rootNode ) {
            if ( node instanceof TaggedNode taggedNode ) {
                names.append ( taggedNode.getTag () ).append ( ", " );
            }
        }
        assertEquals (
            "root, child_1, child_11, child_12, child_2, child_21, child_211, child_213, ",
            names.toString() );

        // Markup
        rootNode = ParseASTUtil.parseString (
            "[p Text contains [b bold], [i italic], and [b [i bold/italic]] words.]" );
        assertEquals ( "Text contains bold, italic, and bold/italic words.", rootNode.concatenateTreeTexts() );
    }

    @Test
    void testSinglelineCommet() throws Exception {

        TaggedNode rootNode = ParseASTUtil.parseString ( """
            [root text ^// comment
            ]""" );
        assertEquals ( 1, rootNode.getChildNodes().size() );
        assertEquals ( "text ", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root text ^/ comment
            ]""" );
        assertEquals ( 1, rootNode.getChildNodes().size() );
        assertEquals ( "text \n", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root
                before
                ^// comment
                after
            ]""" );
        assertEquals ( 1, rootNode.getChildNodes().size() );
        assertEquals ( "    before\n        after\n", rootNode.toText() );
    }


    @Test
    void testMultilineComment() throws Exception {

        TaggedNode rootNode = ParseASTUtil.parseString ( """
            [root text before ^/* comment */ text after]""" );

        assertEquals ( 1, rootNode.getChildNodes().size() );
        TextLeaf textLeaf = (TextLeaf) rootNode.childAt ( 0 );
        assertEquals ( "text before  text after", textLeaf.getText() );

        rootNode = ParseASTUtil.parseString ( """
            [root text before ^/* included comment */ text after]""",
            new PdmlParserConfigBuilder ().ignoreComments ( false ).build() );
        assertEquals ( 3, rootNode.getChildNodes().size() );
        textLeaf = (TextLeaf) rootNode.childAt ( 0 );
        assertEquals ( "text before ", textLeaf.getText() );
        CommentLeaf commentLeaf = (CommentLeaf) rootNode.childAt ( 1 );
        assertEquals ( "^/* included comment */", commentLeaf.getText() );
        assertEquals ( " included comment ", commentLeaf.textWithoutDelimiters() );
        textLeaf = (TextLeaf) rootNode.childAt ( 2 );
        assertEquals ( " text after", textLeaf.getText() );

        rootNode = ParseASTUtil.parseString ( """
            [root text before^/* comment
                    ^/* nested comment */
                */text after]""" );
        // This test is valid if comments are not ignored
        // assertEquals ( 3, rootNode.getChildNodes().size() );
        // commentLeaf = (CommentLeaf) rootNode.childAt ( 1 );
        // assertEquals ( """
        //     ^/* comment
        //             ^/* nested comment */
        //         */""", commentLeaf.getText() );
        assertEquals ( 1, rootNode.getChildNodes().size() );
        textLeaf = (TextLeaf) rootNode.childAt ( 0 );
        assertEquals ( "text beforetext after", textLeaf.getText() );

        rootNode = ParseASTUtil.parseString (
            "[root ^/** comment including */ **/]" );
        // This test is valid if comments are not ignored
        // assertEquals ( 1, rootNode.getChildNodes().size() );
        // commentLeaf = (CommentLeaf) rootNode.childAt ( 0 );
        // assertEquals ( "^/** comment including */ **/", commentLeaf.getText() );
        // assertEquals ( " comment including */ ", commentLeaf.textWithoutDelimiters() );
        assertEquals ( 0, rootNode.getChildNodes().size() );
        assertTrue ( rootNode.isEmpty() );

        rootNode = ParseASTUtil.parseString ( """
            [root a^/* comment */^/* */b]""" );
        assertEquals ( 1, rootNode.getChildNodes().size() );
        textLeaf = (TextLeaf) rootNode.childAt ( 0 );
        assertEquals ( "ab", textLeaf.getText() );

        assertThrows ( PdmlException.class, () ->
            ParseASTUtil.parseString ( """
                [root ^(a1 = "v ^/** comment **/ 1")]
                """ ) );
    }

    @Test
    void testAttributes() throws Exception {

        var code = """
            [root ^(a1=v1 a2=v2)  foo bar]""";
        TaggedNode rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "v1", rootNode.getStringAttributes().getValue ( "a1" ) );
        assertEquals ( "v2", rootNode.getStringAttributes().getValue ( "a2" ) );
        assertEquals ( " foo bar", rootNode.toText() );
        // writtenCode = PdmlNodeWriterUtil.writeToString ( rootnode, false );
        // assertEquals ( code, writtenCode );

        code = """
            [root ^(a1="v1 v1" a2="")foo bar]""";
        rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "v1 v1", rootNode.getStringAttributes().getValue ( "a1" ) );
        assertNull ( rootNode.getStringAttributes().getValue ( "a2" ) );
        assertEquals ( "foo bar", rootNode.toText() );
    }

    @Test
    void testconstGetExtensions() throws Exception {

        String code = """
            [root
                ^[const c1=v1]
                ^[const c3   =  "v 3"  ]

                ^[const a=a]
                ^[const b=b]
                ^[const ab=^[ins_const a]^[ins_const b]]
                ^[const abc=^[ins_const ab]c]

                [n1 ^[ins_const abc]]
                [n_^[ins_const abc] text ^[ins_const abc] text]
            ]""";
        TaggedNode rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "abc", rootNode.child ( "n1" ).toText() );
        assertEquals ( "text abc text", rootNode.child ( "n_abc" ).toText() );
    }

    @Test
    void testScripting() throws Exception {

        String code = "[tag\\[^[ins_exp 1+1]\\] text\\\\^[ins_exp 2+3] end]";
        TaggedNode rootNode = ParseASTUtil.parseString ( code );
        assertEquals ( "tag[2]", rootNode.getTag ().qualifiedTag () );
        assertEquals ( "text\\5 end", rootNode.toText() );
    }

    @Test
    void testStringLiteralExtension() throws Exception {

        TaggedNode rootNode = ParseASTUtil.parseString ( "[root ^\"[[]]\"]" );
        assertEquals ( "[[]]", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( "[root before ^~|[[]]|~ after]" );
        assertEquals ( "before [[]] after", rootNode.toText() );

        rootNode = ParseASTUtil.parseString ( """
            [root ^""\"
                line 1
                    line 2
                ""\"]""" );
        assertEquals ( "line 1\n    line 2", rootNode.toText() );
    }
}
