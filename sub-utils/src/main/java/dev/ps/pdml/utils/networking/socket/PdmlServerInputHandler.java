package dev.ps.pdml.utils.networking.socket;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.shared.basics.annotations.NotNull;

import java.io.IOException;

public interface PdmlServerInputHandler {

    /**
     *
     * @param inputRootNode
     * @param server
     * @return true if connection should be closed
     * @throws IOException
     */
    boolean handleInput (
        @NotNull TaggedNode inputRootNode,
        @NotNull PdmlServer server ) throws IOException;
}
