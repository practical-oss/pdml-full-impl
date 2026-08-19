package dev.ps.pdml.cmdnode;

import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.MalformedPdmlException;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameters;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.scriptingbase.env.ScriptingEnvironment;

import java.io.IOException;

public class DelegatingCommandNodeExecutor implements GlobalCommandNodeExecutor {


    private final @NotNull MutableCommandNodes commandNodes;

    private final @Nullable ScriptingEnvironment scriptingEnvironment;

    private @Nullable CommandNodeExecutorContext cachedContext;
    private @NotNull CommandNodeExecutorContext requireContext (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParser pdmlParser ) {

        if ( cachedContext == null ) {
            cachedContext = new CommandNodeExecutorContext (
                // pdmlReader, pdmlParser, commandNodes, scriptingEnvironment );
                pdmlReader, pdmlParser, scriptingEnvironment );
        }
        return cachedContext;
    }


    public DelegatingCommandNodeExecutor (
        @NotNull MutableCommandNodes commandNodes,
        @Nullable ScriptingEnvironment scriptingEnvironment ) {

        this.commandNodes = commandNodes;
        this.scriptingEnvironment = scriptingEnvironment;
    }


    public @Nullable CommandNodeResult executeCommand (
        @NotNull PdmlTokenReader pdmlReader,
        @NotNull PdmlParser pdmlParser ) throws IOException, PdmlException {

        if ( ! pdmlReader.skipNodeStart() ) {
            throw new MalformedPdmlException (
                "Expecting '" + CorePdmlConstants.NODE_START_CHAR + "' as the start of a command node.",
                "COMMAND_NODE_START_REQUIRED",
                pdmlReader.currentTextPosition() );
        }

        NodeTag nodeTag = pdmlParser.requireTag();
        // pdmlParser.requireSeparator();
        pdmlParser.parseSeparator();

        String name = nodeTag.toString();
        CommandNodeBase commandNodeBase = commandNodes.getOrNull ( name );
        if ( commandNodeBase == null ) {
            throw new PdmlException (
                "Command node '" + name + "' doesn't exist.\nThe following command nodes are available: " +
                    commandNodes.sortedNamesAsString(),
                "INVALID_COMMAND_NODE_NAME",
                nodeTag.location() );
        }

        CommandNodeExecutorContext context = requireContext ( pdmlReader, pdmlParser );

        switch ( commandNodeBase ) {
            case CommandNode commandNode -> {
                @Nullable Parameters parameters = commandNode.inputParameters();
                @Nullable Arguments arguments = parameters != null
                    ? context.parseArguments ( parameters, nodeTag )
                    : null;
                context.skipWhitespaceAndComments();
                context.requireNodeEnd ( nodeTag );
                return commandNode.execute ( arguments, context, nodeTag );
            }
            case SpecificParsingCommandNode commandNode -> {
                return commandNode.execute ( context, nodeTag );
            }
            default -> throw new IllegalStateException ( "Unexpected type: " + commandNodeBase.getClass() );
        }
    }
}
