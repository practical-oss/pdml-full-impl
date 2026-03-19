package dev.ps.pdml.utils.networking.socket;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.PdmlParserConfigBuilder;
import dev.ps.pdml.parser.util.ParseASTUtil;
import dev.ps.pdml.writer.node.PdmlNodeWriterConfig;
import dev.ps.pdml.writer.node.PdmlNodeWriterUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.SimpleLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class PdmlClientServerBase {

    final @NotNull Reader reader;
    final @NotNull Writer writer;
    final boolean writeMessagesToStdout;

    PdmlClientServerBase (
        @NotNull Reader reader,
        @NotNull Writer writer,
        boolean writeMessagesToStdout ) {

        this.reader = reader;
        this.writer = writer;
        this.writeMessagesToStdout = writeMessagesToStdout;
    }

    PdmlClientServerBase (
        @NotNull InputStream inputStream,
        @NotNull OutputStream outputStream,
        boolean writeMessagesToStdout ) {

        this (
            new BufferedReader ( new InputStreamReader ( inputStream, StandardCharsets.UTF_8 ) ),
            new BufferedWriter ( new OutputStreamWriter ( outputStream, StandardCharsets.UTF_8 ) ),
            writeMessagesToStdout );
    }


    public void sendDocument ( @NotNull TaggedNode rootNode ) throws IOException {

        SimpleLogger.debug ( getName() + ": sending document with root node '" + rootNode + "'." );

        if ( writeMessagesToStdout ) {
            writeOutputToStdout ( rootNode );
        }

        PdmlNodeWriterUtil.write ( writer, rootNode, false, PdmlNodeWriterConfig.DEFAULT_CONFIG );
        sendLineBreak ();
        flush();
    }

    public void sendDocument ( @NotNull String pdmlDocument ) throws IOException {

        SimpleLogger.debug ( getName() + ": sending document: " + pdmlDocument );

        if ( writeMessagesToStdout ) {
            writeOutputToStdout ( pdmlDocument );
        }

        sendCode ( pdmlDocument );
        sendLineBreak ();
        flush();
    }

/*
    public void sendAndFlush ( @NotNull String pdmlCode ) throws IOException {
        send ( pdmlCode );
        flush();
    }
 */

    void sendCode ( @NotNull String pdmlCode ) throws IOException {
        writer.write ( pdmlCode );
    }

    void sendLineBreak() throws IOException {
        writer.write ( "\n" );
    }

    void flush() throws IOException {
        writer.flush();
    }

    @NotNull TaggedNode waitForInput() throws Exception {

        SimpleLogger.debug ( getName() + ": wait for PDML input." );

/*
        PdmlParserEventHandler_OLD<BranchNode, BranchNode> eventHandler = new CreateTree_ParserEventHandler ();
        PdmlParserBuilder_OLD<BranchNode, BranchNode> builder = new PdmlParserBuilder_OLD<> ( eventHandler );
        builder.ignoreTextAfterEndOfRootNode ( true );
        builder.parseReader ( reader, null, null, null );
        BranchNode rootNode = eventHandler.getResult();
 */

        PdmlParserConfig config = new PdmlParserConfigBuilder()
            .ignoreTextAfterEndOfRootNode ( true )
            .build();

        if ( true) throw new RuntimeException ( "Implementation error: need nonNull ReaderResource" );
        // TaggedNode rootNode = ParseASTUtil.parseReader ( reader, null, config );
        TaggedNode rootNode = null;

        SimpleLogger.debug ( getName() + ": received PDML document with root node " + rootNode.getTag () );

        if ( writeMessagesToStdout ) {
            writeInputToStdout ( rootNode );
        }

        return rootNode;
    }

    abstract @NotNull String getName();

    private void writeOutputToStdout ( @NotNull String pdmlDocument ) {

        System.out.println ( getName() + " sending:" );
        System.out.println ( pdmlDocument );
        System.out.println();
    }

    private void writeOutputToStdout ( @NotNull TaggedNode outputRootNode ) throws IOException {

        System.out.println ( getName() + " sending:" );
        writeRootNodeToStdout ( outputRootNode );
        System.out.println();
    }

    private void writeInputToStdout ( @NotNull TaggedNode inputRootNode ) throws IOException {

        System.out.println ( getName() + " received:" );
        writeRootNodeToStdout ( inputRootNode );
        System.out.println();
    }

    private void writeRootNodeToStdout ( @NotNull TaggedNode rootNode ) throws IOException {

        PdmlNodeWriterUtil.writeToStdout ( rootNode, false, PdmlNodeWriterConfig.DEFAULT_CONFIG );
    }
}
