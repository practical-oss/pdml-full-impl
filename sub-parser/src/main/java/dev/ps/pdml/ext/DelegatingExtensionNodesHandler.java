package dev.ps.pdml.ext;

import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.types.PdmlTypes;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.scriptingbase.env.ScriptingEnvironment;
import dev.ps.pdml.reader.PdmlTokenReader;

import java.io.IOException;

public class DelegatingExtensionNodesHandler implements ExtensionNodesHandler {


    private final @NotNull ExtensionNodeHandlers delegates;

    private final @Nullable ScriptingEnvironment scriptingEnvironment;

    private @Nullable ExtensionNodeHandlerContext cachedContext;
    private @NotNull ExtensionNodeHandlerContext requireContext (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParser pdmlParser ) {

        if ( cachedContext == null ) {
            cachedContext = new ExtensionNodeHandlerContext (
                pdmlReader, pdmlParser, scriptingEnvironment );
        }
        return cachedContext;
    }


    public DelegatingExtensionNodesHandler (
        @NotNull ExtensionNodeHandlers delegates,
        @Nullable ScriptingEnvironment scriptingEnvironment ) {

        this.delegates = delegates;
        this.scriptingEnvironment = scriptingEnvironment;
    }


    @Override
    // public void handleExtensionNode (
    public @Nullable InsertReaderResourceExtensionResult handleExtensionNode (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParser pdmlParser ) throws IOException, PdmlException {

        boolean ok = pdmlReader.skipExtensionStartChar ();
        assert ok;

        if ( ! pdmlReader.skipNodeStart () ) {
            throw new MalformedPdmlException (
                "Expecting '" + CorePdmlConstants.NODE_START_CHAR + "'.",
                "EXTENSION_NODE_START_REQUIRED",
                pdmlReader.currentTextPosition() );
        }

        return handleExtension ( pdmlReader, pdmlParser );
    }

    // private void handleExtension (
    private @Nullable InsertReaderResourceExtensionResult handleExtension (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParser pdmlParser ) throws IOException, PdmlException {

        NodeTag nodeName = pdmlParser.requireTag ();
        pdmlParser.requireSeparator ();

        String name = nodeName.toString();
        ExtensionNodeHandler handler = delegates.getOrNull ( name );
        if ( handler == null ) {
            throw new PdmlException (
                "Extension node '" + name + "' doesn't exist.",
                "INVALID_EXTENSION_NAME",
                nodeName.tagPositionOrRange() );
        }

        ExtensionNodeHandlerContext context = requireContext ( pdmlReader, pdmlParser );
        return handler.handleNode ( context, nodeName );
    }

    public @Nullable PdmlTypes getTypes() {
        return delegates.getTypes();
    }
}
