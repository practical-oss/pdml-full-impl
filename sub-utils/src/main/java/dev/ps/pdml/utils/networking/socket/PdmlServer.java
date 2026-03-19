package dev.ps.pdml.utils.networking.socket;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class PdmlServer extends PdmlClientServerBase {

    private final @NotNull PdmlServerInputHandler inputHandler;


    public PdmlServer (
        @NotNull Reader reader,
        @NotNull Writer writer,
        @NotNull PdmlServerInputHandler inputHandler,
        boolean writeMessagesToStdout ) {

        super ( reader, writer, writeMessagesToStdout );
        this.inputHandler = inputHandler;
    }

    public PdmlServer (
        @NotNull InputStream inputStream,
        @NotNull OutputStream outputStream,
        @NotNull PdmlServerInputHandler inputHandler,
        boolean writeMessagesToStdout ) {

        super ( inputStream, outputStream, writeMessagesToStdout );
        this.inputHandler = inputHandler;
    }

/*
    public void disconnect() throws IOException {

        // TODO use pp-libs util
        if ( closeable != null ) {
            closeable.close();
        }
        reader.close();
        writer.close();
    }
 */

    public boolean waitForInputAndHandleIt() throws Exception {
        TaggedNode rootNode = waitForInput();
        return inputHandler.handleInput ( rootNode, this );
    }

    @NotNull String getName() { return "PDML server"; }
}
