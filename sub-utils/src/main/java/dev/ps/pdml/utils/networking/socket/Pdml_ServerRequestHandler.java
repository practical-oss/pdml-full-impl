package dev.ps.pdml.utils.networking.socket;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.networking.socket.ServerRequestsHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class Pdml_ServerRequestHandler implements ServerRequestsHandler {

    final @NotNull PdmlServerInputHandler inputHandler;
    final boolean writeMessagesToStdout;


    public Pdml_ServerRequestHandler (
        @NotNull PdmlServerInputHandler inputHandler,
        boolean writeMessagesToStdout ) {

        this.inputHandler = inputHandler;
        this.writeMessagesToStdout = writeMessagesToStdout;
    }

    public void handleRequests ( @NotNull InputStream inputStream, @NotNull OutputStream outputStream ) {

        PdmlServer pdmlServer = new PdmlServer ( inputStream, outputStream, inputHandler, writeMessagesToStdout );

        boolean isStopped = false;
        while ( ! isStopped ) {
            try {
                isStopped = pdmlServer.waitForInputAndHandleIt();
            // TODO
            } catch ( Exception e ) {
                throw new RuntimeException ( e );
            }
        }
    }
}
