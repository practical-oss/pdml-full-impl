package dev.ps.pdml.ext.utils;

import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.range.TextPosition;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.ExtensionNodeHandlerContext;
import dev.ps.pdml.ext.ExtensionNodeHandler;
import dev.ps.pdml.ext.InsertReaderResourceExtensionResult;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class InsertConstantHandler implements ExtensionNodeHandler {

    private static final @NotNull String NAME = "ins_const";

    public static final InsertConstantHandler INSTANCE = new InsertConstantHandler ();


    private InsertConstantHandler(){}


    @Override
    public @NotNull String getExtensionName() {
        return NAME;
    }

    @Override
    public @Nullable InsertReaderResourceExtensionResult handleNode ( @NotNull ExtensionNodeHandlerContext context, @NotNull NodeTag nodeName )
        throws IOException, PdmlException {

        // now positioned right after the node tag

        TextPosition namePosition = context.currentPosition();
        @Nullable String name = context.parseTrimmedTextAndIgnoreComments();
        if ( name == null ) {
            throw context.error (
                "Expecting the name of a previously declared constant.",
                "MISSING_CONSTANT_NAME",
                namePosition );
        }

        Map<String, String> constants = context.getDeclaredConstants();
        String value = constants.get ( name );
        if ( value == null ) {
            String message = "A constant with name '" + name + "' doesn't exist.";
            if ( constants.isEmpty() ) {
                message = message + " No constants have been defined.";
            } else {
                message = message + " The following constants have been defined: " + constants.keySet();
            }
            throw context.error (
                message,
                "INVALID_CONSTANT_NAME",
                namePosition );
        }

        context.requireExtensionNodeEnd ( nodeName );

        // return new InsertReaderResourceExtensionResult ( value,null, false );
        return new InsertReaderResourceExtensionResult ( new StringReaderResource ( value ), false );
    }
}
