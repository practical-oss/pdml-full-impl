package dev.ps.pdml.utils.networking.socket;

import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.networking.socket.SocketUtils;

import java.io.*;
import java.net.Socket;

public class PdmlClient extends PdmlClientServerBase {

    private final @NotNull PdmlClientInputHandler inputHandler;
    private @Nullable Socket clientSocket = null;


    public PdmlClient (
        @NotNull Reader reader,
        @NotNull Writer writer,
        @NotNull PdmlClientInputHandler inputHandler,
        boolean writeMessagesToStdout ) {

        super ( reader, writer, writeMessagesToStdout );
        this.inputHandler = inputHandler;
    }

    public PdmlClient (
        @NotNull InputStream inputStream,
        @NotNull OutputStream outputStream,
        @NotNull PdmlClientInputHandler inputHandler,
        boolean writeMessagesToStdout ) {

        super ( inputStream, outputStream, writeMessagesToStdout );
        this.inputHandler = inputHandler;
    }

    public PdmlClient (
        @NotNull Socket clientSocket,
        @NotNull PdmlClientInputHandler inputHandler,
        boolean writeMessagesToStdout ) throws IOException {

        this (
            clientSocket.getInputStream(),
            clientSocket.getOutputStream(),
            inputHandler, writeMessagesToStdout );
        this.clientSocket = clientSocket;
    }

    public PdmlClient (
        @NotNull String host,
        int port,
        @NotNull PdmlClientInputHandler inputHandler,
        boolean writeMessagesToStdout ) throws IOException {

        this ( new Socket ( host, port), inputHandler, writeMessagesToStdout );
    }


    public void asyncSendDocumentAndHandleResponse ( @NotNull TaggedNode rootNode, boolean useVirtualThread ) {

        Runnable runnable = () -> {
            try {
                sendDocumentAndHandleResponse ( rootNode );
            } catch ( Exception e ) {
                throw new RuntimeException ( e );
            }
        };

        SocketUtils.runInNewThread ( runnable, useVirtualThread );
    }

    public synchronized boolean sendDocumentAndHandleResponse ( @NotNull TaggedNode rootNode ) throws Exception {
        TaggedNode response = sendDocumentAndGetResponse ( rootNode );
        return handleResponse ( response );
    }

    public void asyncSendDocumentAndHandleResponse ( @NotNull String pdmlDocument, boolean useVirtualThread ) {

        Runnable runnable = () -> {
            try {
                sendDocumentAndHandleResponse ( pdmlDocument );
            } catch ( Exception e ) {
                throw new RuntimeException ( e );
            }
        };

        SocketUtils.runInNewThread ( runnable, useVirtualThread );
    }

    // TODO boolean needed?
    public synchronized boolean sendDocumentAndHandleResponse ( @NotNull String pdmlDocument ) throws Exception {
        TaggedNode response = sendDocumentAndGetResponse ( pdmlDocument );
        return handleResponse ( response );
    }

    public void asyncSendEmptyNodeAndHandleResponse ( @NotNull String nodeName, boolean useVirtualThread ) {

        Runnable runnable = () -> {
            try {
                sendEmptyNodeAndHandleResponse ( nodeName );
            } catch ( Exception e ) {
                throw new RuntimeException ( e );
            }
        };

        SocketUtils.runInNewThread ( runnable, useVirtualThread );
    }

    public synchronized boolean sendEmptyNodeAndHandleResponse ( @NotNull String nodeName ) throws Exception {

        return sendDocumentAndHandleResponse (
            CorePdmlConstants.NODE_START_CHAR + nodeName + CorePdmlConstants.NODE_END_CHAR );
    }

    private boolean handleResponse ( @NotNull TaggedNode response ) throws Exception {

        boolean disconnect = inputHandler.handleInput ( response, this );
        if ( disconnect ) disconnect();
        return disconnect;
    }

    private @NotNull TaggedNode sendDocumentAndGetResponse ( @NotNull String pdmlDocument ) throws Exception {
        sendDocument ( pdmlDocument );
        return waitForInput();
    }

    private @NotNull TaggedNode sendDocumentAndGetResponse ( @NotNull TaggedNode rootNode ) throws Exception {
        sendDocument ( rootNode );
        return waitForInput();
    }

    private void disconnect() throws IOException {

        reader.close();
        writer.close();
        if ( clientSocket != null ) {
            clientSocket.close();
        }
    }

    @NotNull String getName() { return "PDML client"; }
}
