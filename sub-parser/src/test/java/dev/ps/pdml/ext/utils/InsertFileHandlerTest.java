package dev.ps.pdml.ext.utils;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.utilities.file.TextFileWriterUtil;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.util.ParseASTUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class InsertFileHandlerTest {

    /* This directory and the files created in it will be deleted after
     * tests are run, even in the event of failures or exceptions.
     */
    // @TempDir Path tempDir;

    @Test
    void handleNode() throws IOException, PdmlException {

        Path tempDir = Files.createTempDirectory ( null );
        tempDir.toFile().deleteOnExit();

        // sub_dir/file_to_insert.txt
        Path pdmlFile = tempDir.resolve ( "test.pdml" );
        // DebugUtils.writeNameValue ( "pdmlFile", pdmlFile );
        TextFileWriterUtil.writeStringToUTF8File (
            "[root ^[ins_file   sub_dir/file_to_insert.txt  ]]", pdmlFile, false );

        Path fileToInsert = tempDir.resolve ( "sub_dir/file_to_insert.txt" );
        // DebugUtils.writeNameValue ( "fileToInsert", fileToInsert );
        TextFileWriterUtil.writeStringToUTF8File (
            "file content", fileToInsert, true );

        @NotNull TaggedNode rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertEquals ( "file content", rootNode.toText() );

        // [path sub_dir/file_to_insert.txt]
        TextFileWriterUtil.writeStringToUTF8File (
            "[root ^[ins_file [path sub_dir/file_to_insert.txt]]]", pdmlFile, false );

        rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertEquals ( "file content", rootNode.toText() );

        // Escaped already
        TextFileWriterUtil.writeStringToUTF8File (
            "file \\[content\\]", fileToInsert, true );
        rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertEquals ( "file [content]", rootNode.toText() );

        // [path sub_dir/file_to_insert.txt] [escape_text yes]
        TextFileWriterUtil.writeStringToUTF8File (
            "[root ^[ins_file [path sub_dir/file_to_insert.txt] [escape_text yes]]]", pdmlFile, false );
        TextFileWriterUtil.writeStringToUTF8File (
            "file [content]", fileToInsert, true );
        rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertEquals ( "file [content]", rootNode.toText() );

        // sub_dir/file_to_insert.txt [escape_text yes]
        TextFileWriterUtil.writeStringToUTF8File (
            "[root ^[ins_file sub_dir/file_to_insert.txt [escape_text yes]]]", pdmlFile, false );
        TextFileWriterUtil.writeStringToUTF8File (
            "file [content]", fileToInsert, true );
        rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertEquals ( "file [content]", rootNode.toText() );

        // [escape_text yes] sub_dir/file_to_insert.txt
        TextFileWriterUtil.writeStringToUTF8File (
            "[root ^[ins_file [escape_text yes] sub_dir/file_to_insert.txt]]", pdmlFile, false );
        TextFileWriterUtil.writeStringToUTF8File (
            "file [content]", fileToInsert, true );
        rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertEquals ( "file [content]", rootNode.toText() );

        // Use default value if file doesn't exist
        TextFileWriterUtil.writeStringToUTF8File (
            "[root ^[ins_file inexistent_file.txt [default foo]]]", pdmlFile, false );
        TextFileWriterUtil.writeStringToUTF8File (
            "file [content]", fileToInsert, true );
        rootNode = ParseASTUtil.parseFile ( pdmlFile );
        assertEquals ( "foo", rootNode.toText() );

        // Error: files doesn't exist
        TextFileWriterUtil.writeStringToUTF8File (
            "[root ^[ins_file inexistent_file.txt]", pdmlFile, false );
        assertThrows ( PdmlException.class, () -> ParseASTUtil.parseFile ( pdmlFile ) );

        // Error: files is empty
    }
}
