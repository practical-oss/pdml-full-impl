package dev.ps.pdml.companion.commands.blackboxtest;

import dev.ps.pdml.companion.PdmlcApplication;
import dev.ps.pdml.companion.commands.list.ListTextsCommand;
import dev.ps.pdml.companion.commands.tocore.FullPdmlToCorePdmlCommand;
import dev.ps.pdml.data.util.DemoDocs;
import dev.ps.prt.command.output.CLICommandOutput;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.directory.TempDirectoryUtil;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;

import java.nio.file.Path;

import static dev.ps.pdml.companion.commands.blackboxtest.ApplicationBlackboxTester.*;

public class PdmlBlackboxTester {

    private static final @NotNull Path TEMP_DIR = TempDirectoryUtil.createTempDirectoryOrThrow ( null, true );

    // simplest-demo.pdml
    private static final @NotNull String SIMPLEST_PDML_DEMO_FILE_NAME = "simplest-demo.pdml";
    private static final @NotNull Path SIMPLEST_PDML_DEMO_FILE = Path.of ( TEMP_DIR.toString(), SIMPLEST_PDML_DEMO_FILE_NAME );
    static {
        TextFileWriterUtil.writeStringToUTF8FileOrThrow ( "[root]", SIMPLEST_PDML_DEMO_FILE, false );
    }

    // core-demo.pdml
    private static final @NotNull String CORE_PDML_DEMO_FILE_NAME = DemoDocs.CORE_PDML_DEMO_FILE_NAME.toString();
    private static final @NotNull Path CORE_PDML_DEMO_FILE = TEMP_DIR.resolve ( CORE_PDML_DEMO_FILE_NAME );
    static {
        TextFileWriterUtil.writeStringToUTF8FileOrThrow ( DemoDocs.corePdmlDemoDoc(), CORE_PDML_DEMO_FILE, false );
    }

    // full-demo.pdml
    private static final @NotNull String FULL_PDML_DEMO_FILE_NAME = DemoDocs.PDML_EXTENSIONS_DEMO_FILE_NAME.toString();
    private static final @NotNull Path FULL_PDML_DEMO_FILE = TEMP_DIR.resolve ( FULL_PDML_DEMO_FILE_NAME );
    static {
        TextFileWriterUtil.writeStringToUTF8FileOrThrow ( DemoDocs.pdmlExtensionsDemoDoc(), FULL_PDML_DEMO_FILE, false );
    }


    private final @NotNull ApplicationBlackboxTester appTester;


    public PdmlBlackboxTester ( @NotNull Path pdmlExeFilePath ) {
        this.appTester = new ApplicationBlackboxTester (
            PdmlcApplication.CLI_APP_NAME, pdmlExeFilePath, TEMP_DIR );
    }


    public void runTests ( boolean includeErrorTests ) {

        try {
            basicCommands(); askPressEnter();
            pdmlCommands(); askPressEnter();
            validPdmlDocuments(); askPressEnter();
            inputOutputCLIArguments(); askPressEnter();

            if ( includeErrorTests ) {
                CLIErrors(); askPressEnter();
                invalidCorePDMLErrors(); askPressEnter();
                // Extension Errors
                commentErrors(); askPressEnter();
                unicodeEscapeErrors(); askPressEnter();
                extensionNodeErrors(); askPressEnter();
                attributesErrors(); askPressEnter();
                stringLiteralErrors(); askPressEnter();
                /* TODO
                typeErrors(); askPressEnter();
                scriptingErrors(); askPressEnter();
                 */
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void basicCommands() {

        writeGroupTitle ( "Basics Commands");

        assertOutputContains ( PdmlcApplication.INSTANCE.name(), "about" );
        assertOutputContains ( PdmlcApplication.INSTANCE.version(), "version" );

        assertOutputContains ( "Usage", "help" );
        assertOutputContains ( "version", "help", "--command", "version" );
        assertOutputContains ( "version", "help", "-c", "help" );
        assertOutputContains ( "version", "help", "version" );

        assertOutputContains ( "version", "version", "--help" );
        assertOutputContains ( "version", "version", "-h" );

        assertOutputContains ( ListTextsCommand.INSTANCE.name(), "command-info", "list-texts" );
        assertOutputContains ( FullPdmlToCorePdmlCommand.INSTANCE.name(), "commands-info" );
    }

    private void pdmlCommands() {

        writeGroupTitle ( "PDMLC Commands");

        assertExitCodeZero ( "create-demo-docs" );

        assertExitCodeZero ( "check-pdml-docs", "-i", FULL_PDML_DEMO_FILE_NAME );
        assertExitCodeZero ( "check-pdml-docs", "-i", "\"" + FULL_PDML_DEMO_FILE_NAME + ", " + CORE_PDML_DEMO_FILE_NAME + "\"" );
        assertExitCodeZeroWithInput ( "[root]", "check-pdml-docs", "-i", "-" );
        assertExitCodeZeroWithInput ( "[root]", "check-pdml-docs" );

        assertExitCodeZero ( "texts", "-i", CORE_PDML_DEMO_FILE_NAME, "-o", "output.txt" );
        assertExitCodeZero ( "tags", "-i", CORE_PDML_DEMO_FILE_NAME, "-o", "output.txt" );
        assertExitCodeZero ( "tags", "--sort", "--distinct", "-i", CORE_PDML_DEMO_FILE_NAME, "-o", "-" );
        assertExitCodeZero ( "tags", "-sd", CORE_PDML_DEMO_FILE_NAME );

        // TODO check content (e.g. substring) of files created
        assertExitCodeZero ( "pdml-to-core-pdml", "-i", FULL_PDML_DEMO_FILE_NAME, "-o", "output.pdml" );
        assertExitCodeZero ( "pdml-to-html-tree", "-i", CORE_PDML_DEMO_FILE_NAME, "-o", "output.html" );
        assertExitCodeZero ( "pdml-to-json", "-i", CORE_PDML_DEMO_FILE_NAME, "-o", "output.json" );
        // TODO use CORE_PDML_DEMO_FILE_NAME (after fixing bug with invalid XML tag)
        assertExitCodeZero ( "pdml-to-xml", "-i", SIMPLEST_PDML_DEMO_FILE_NAME, "-o", "output.xml" );
        // TODO use more complex JSON code
        assertExitCodeZeroWithInput ( "{\"root\":\"text\"}", "json-to-pdml", "-o", "output.pdml" );
        // TODO use more complex XML code
        assertExitCodeZeroWithInput ( "<?xml version=\"1.0\"?><root />", "xml-to-pdml", "-o", "output.pdml" );

        Path javaFile = Path.of ( TEMP_DIR.toString(), "TreeExplorer.java" );
        String javaCode = """
            package pdml;

            import dev.ps.pjse.util.interfaces.FailableConsumer;
            import dev.ps.pdml.data.node.tagged.TaggedNode;

            public class TreeExplorer implements FailableConsumer<TaggedNode> {

                public void accept ( TaggedNode rootNode ) throws Exception {

                    System.out.println ( "Hi from explorer (Java source code that explores a PDML document)" );

                    System.out.println();
                    System.out.println ( "Root node: " + rootNode.toString() );

                    System.out.println();
                    System.out.println ( "Tagged nodes:" );
                    rootNode.treeTaggedNodeStream ( true ).forEach ( node -> {
                        System.out.println ( node.toString() );
                    });

                    System.out.println();
                    System.out.println ( "Concatenated text: " + rootNode.concatenateTreeTexts() );
                }
            }
            """;
        TextFileWriterUtil.writeStringToUTF8FileOrThrow ( javaCode, javaFile, false );
        assertExitCodeZero ( "explore-tree", "-i", SIMPLEST_PDML_DEMO_FILE_NAME, "--explorer", "TreeExplorer.java" );
        assertExitCodeZeroWithInput ( "[doc [i Hi] [b there]]", "explore-tree", "-i", "-", "--explorer", "TreeExplorer.java" );
    }

    private void validPdmlDocuments() {

        writeGroupTitle ( "Valid PDML");

        assertExitCodeZero ( "p2c", "-i", CORE_PDML_DEMO_FILE_NAME, "-o", "output.pdml" );
        assertExitCodeZero ( "p2c", "-i", FULL_PDML_DEMO_FILE_NAME, "-o", "output.pdml" );
    }

    private void inputOutputCLIArguments() {

        writeGroupTitle ( "CLI Input/Output Arguments");

        String inputFile = SIMPLEST_PDML_DEMO_FILE_NAME;
        String outputFile = "output.pdml";

        // long args
        assertExitCodeZero ( "p2c", "--input", inputFile, "--output", outputFile );
        assertExitCodeZero ( "p2c", "--input=" + inputFile, "--output=" + outputFile );
        // short args
        assertExitCodeZero ( "p2c", "-i", inputFile, "-o", outputFile );
        assertExitCodeZero ( "p2c", "-i=" + inputFile, "-o=" +outputFile );
        // positional args
        assertExitCodeZero ( "p2c", inputFile, outputFile );
        assertExitCodeZero ( "p2c", SIMPLEST_PDML_DEMO_FILE.toString(), TEMP_DIR.resolve ( outputFile ).toString() );
        // STDIN, STDOUT
        assertOutput ( "[root]", "[root]", "p2c", "--input", "-", "--output", "-" );
        assertOutput ( "[root]", "[root]", "p2c", "--input=-", "--output=-" );
        assertOutput ( "[root]", "[root]", "p2c", "-i", "-", "-o", "-" );
        assertOutput ( "[root]", "[root]", "p2c", "-i=-", "-o=-" );
        assertOutput ( "[root]", "[root]", "p2c", "-", "-" );
        assertOutput ( "[root]", "[root]", "p2c" );
    }

    private void CLIErrors() {

        writeGroupTitle ( "CLI Errors");

        // Missing Command
        assertErrorContains ( "A command name is required", CLICommandOutput.COMMAND_NOT_FOUND_OS_EXIT_CODE, "" );
        // Invalid Command
        assertErrorContains ( "Invalid command", CLICommandOutput.COMMAND_NOT_FOUND_OS_EXIT_CODE, "invalid-command" );

        // No arguments allowed
        assertErrorContains ( "Arguments are not allowed", "version", "--name", "value" );
        // Missing argument
        assertError ( null, "command-info" );
        // Invalid argument
        assertError ( null, "p2c", "--foo", "bar" );
        // Missing argument value
        assertError ( null, "command-info", "--command-name" );
        assertError ( null, "command-info", "-c" );
        // Invalid argument value
        assertError ( null, "command-info", "--command-name", "invalid-command-name" );
        assertError ( null, "p2c", "-i", "non-existent-file.pdml" );
       // Duplicate arguments
        assertError ( null, "p2c", "-i", "file.pdml", "-i", "file.pdml" );
        assertError ( null, "p2c", "--input", "file1.pdml", "--input", "file2.pdml" );
        assertError ( null, "p2c", "-i", "file.pdml", "--input", "file.pdml" );
    }

    private void invalidCorePDMLErrors() {

        writeGroupTitle ( "Invalid Core PDML Errors");

        assertErrors ( new String[] {
            "root]",
            "[root",
            "[]",
            "[",
            "]",
            "",
            "[[root]",
            "[root]]a\nbc",
            "[root ]",
            "[a[b]]",
            "[a\\1b]",
            "[root te\\1xt]"
        } );
    }

    // Extensions

    private void commentErrors() {

        writeGroupTitle ( "Comment Errors");

        assertErrors ( new String[] {
            // single-line comment
            "[root ^//]",
            "[root ^/]",
            "[root ^/\n]",
            "[root ^/",

            // block comment
            "[root ^/* comment]",
            "[root ^/*comment\n]",
            "[root ^/* comment",
            "[root ^/* comment *",
            "[root ^/* comment ^/* nested */ ]",
        } );
    }

    private void unicodeEscapeErrors() {

        writeGroupTitle ( "Unicode Escape Errors");

        assertErrors ( new String[] {
            // invalid syntax
            "[root \\u]",
            "[root \\uq",
            "[root \\u{]",
            "[root \\u{}]",
            "[root \\u{41]",
            // invalid values
            "[root \\u{0}]",
            "[root \\u{1 0}]",
            "[root \\u{1F0001}]",
            "[root \\u{FFFFFFFFFFFF}]",
            "[root \\u{4g}]",
            "[root \\u{41 4g 43}]",
            "[root \\u",
            "[root \\u{",
            "[root \\u{41"
        } );
    }

    private void extensionNodeErrors() {

        writeGroupTitle ( "Extension Node Errors");

        assertErrors ( new String[] {
            // missing [
            "[root ^ins-file]",
            // missing tag
            "[root ^[]]",
            // invalid tag
            "[root ^[invalid_extension foo]]",
            "[root ^[ns|ins-file]]",
            // missing arg
            "[root ^[ins-file]]",
            "[root ^[ins-file [default foo]]]",
            // invalid arg name
            "[root ^[ins-file [foo bar]]]",
            // missing arg value
            "[root ^[ins-file [escape-text]]",
            // invalid arg value
            "[root ^[ins-file [escape-text foo]]",
            "[root ^[ins-file path/to/invalid_file.txt]]",
            "[root ^[ins-file [path path/to/invalid_file.txt]]]",
            "[root ^[ins-file \"C:\\\\file with spaces.txt\"]]",
            "[root ^[ins-file ~C:\\file with spaces.txt~]]",
            "[root ^[ins-file C\\:\\\\file\\swith\\sspaces.txt]]",
            // duplicate args
            "[root ^[ins-file [default foo] [default bar]]]",
            // "^[ins-file [default foo] [def bar]]"
            """
            [doc
                ^[const id_att = "ida=p2"]
                [p (^[ins-const id_att_c]) text]
            ]
            """
        } );
    }

    private void attributesErrors() {

        writeGroupTitle ( "Attributes Errors");

        assertErrors ( new String[] {
            // missing )
            "[root ^(n=v]",
            "[root ^(]",
            "[root ^(",
            // missing value
            "[root ^(n=)]",
            "[root ^(n)]",
            "[root ^(n1=v1 n2=   )]",
            // missing name
            "[root ^(=v)]",
            "[root ^(=)]",
            // invalid name
            "[root ^(n]n=v)]",
            "[root ^(ns|n=v)]",
            // invalid value
            "[root ^(n = v]v)]",
            // invalid :
            "[root ^(n:v)]",
        } );
    }

    private void stringLiteralErrors() {

        writeGroupTitle ( "String Literal Errors");

        // TODO add more tests

        // Quoted String Literal
        assertErrors ( new String[] {
            "[root ^\"abc]",
            "[root ^\"a\\mbc\"]"
        } );

        // Multi-line String Literal
        assertErrors ( new String[] {
            """
            [root ^\"""
                line 1
                line 2
                ""
            ]"""
        } );

        // Raw String Literal
        assertErrors ( new String[] {
            "[root ^~abc]"
        } );
    }


    private void assertOutput (
        @Nullable String input,
        @Nullable String expectedOut,
        @Nullable String... commandLine ) {

        try {
            appTester.assertOutput ( input, expectedOut, commandLine );
        } catch ( Exception e ) {
            handleException ( e );
        }
    }

    private void assertOutputContains (
        @Nullable String expectedOut,
        @Nullable String... commandLine ) {

        assertOutput ( null, ".." + expectedOut + "..", commandLine );
    }

    private void assertErrors ( @NotNull String[] invalidInputs ) {

        for ( String invalidInput : invalidInputs ) {
            assertErrorContains ( invalidInput, "Message", 1, "check" );
        }
    }

    private void assertError (
        @Nullable String input,
        @Nullable String expectedErr,
        int expectedExitCode,
        @Nullable String... commandLine ) {

        try {
            appTester.assertError ( input, expectedErr, expectedExitCode, commandLine );
        } catch ( Exception e ) {
            handleException ( e );
        }
    }

    private void assertError (
        @Nullable String expectedErr,
        @Nullable String... commandLine ) {

        assertError ( null, expectedErr, 1, commandLine );
    }

    private void assertErrorContains (
        @Nullable String input,
        @Nullable String expectedErr,
        int expectedExitCode,
        @Nullable String... commandLine ) {

        assertError ( input, ".." + expectedErr + "..", expectedExitCode, commandLine );
    }

    private void assertErrorContains (
        @Nullable String expectedErr,
        int expectedExitCode,
        @Nullable String... commandLine ) {

        assertErrorContains ( null, expectedErr, expectedExitCode, commandLine );
    }

    private void assertErrorContains (
        @Nullable String expectedErr,
        @Nullable String... commandLine ) {

        assertErrorContains ( expectedErr, 1, commandLine );
    }

    private void assertExitCodeZeroWithInput (
        @Nullable String input,
        @Nullable String... commandLine ) {

        try {
            appTester.testApp ( input, null, null, 0, commandLine );
        } catch ( Exception e ) {
            handleException ( e );
        }
    }

    private void assertExitCodeZero (
        @Nullable String... commandLine ) {

        assertExitCodeZeroWithInput ( null, commandLine );
    }

    private void handleException ( Exception e ) {
        throw new RuntimeException ( e );
    }
}
