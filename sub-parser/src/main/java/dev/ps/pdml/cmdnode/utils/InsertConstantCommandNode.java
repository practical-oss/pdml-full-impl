package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.cmdnode.SpecificParsingCommandNode;
import dev.ps.pdml.cmdnode.CommandNodeExecutorContext;
import dev.ps.pdml.cmdnode.CommandNodeResult;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.location.FromToTextRangeImpl;
import dev.ps.shared.text.location.TextPosition;
import dev.ps.shared.text.location.TextRange;
import dev.ps.shared.text.unicode.reader.CodePointReader;

import java.io.IOException;
import java.util.Map;

public class InsertConstantCommandNode extends SpecificParsingCommandNode {

    private static final @NotNull String NAME = "ins-const";

    public static final InsertConstantCommandNode INSTANCE = new InsertConstantCommandNode ();


    private InsertConstantCommandNode() {
        super ( NAME, null, () -> new SimpleDocumentation (
            "Insert Constant",
            "",
            "" ) );
    }


    @Override
    public @Nullable CommandNodeResult execute (
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeName ) throws IOException, PdmlException {

        // now positioned right after the node tag

        @NotNull TextPosition nameStartPosition = context.currentPosition();
        @Nullable String name = context.parseTrimmedTextAndIgnoreComments();
        if ( name == null ) {
            throw context.error (
                "Expecting the name of a previously declared constant.",
                "MISSING_CONSTANT_NAME",
                nameStartPosition );
        }

        Map<String, String> constants = context.declaredConstants ();
        String value = constants.get ( name );
        if ( value == null && ! constants.containsKey ( name ) ) {
            String message = "A constant with name '" + name + "' doesn't exist.";
            if ( constants.isEmpty() ) {
                message = message + " No constants have been defined.";
            } else {
                message = message + " The following constants have been defined: " + constants.keySet();
            }
            @NotNull CodePointReader cpReader = context.pdmlReader ().codePointReader();
            TextRange nameRange = new FromToTextRangeImpl ( nameStartPosition.readerResource(),
                nameStartPosition.line(), nameStartPosition.column(),
                cpReader.previousLine(), cpReader.previousColumn(), nameStartPosition.parentLocation() );
            throw context.error (
                message,
                "INVALID_CONSTANT_NAME",
                nameRange );
        }

        context.requireNodeEnd ( nodeName );

        // return new InsertReaderResourceExtensionResult ( value,null, false );
        return value == null ? null : new CommandNodeResult ( new StringReaderResource ( value ), false );
    }
}
