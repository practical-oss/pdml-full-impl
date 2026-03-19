package dev.ps.pdml.ext.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.ExtensionNodeHandlerContext;
import dev.ps.pdml.ext.ExtensionNodeHandler;
import dev.ps.pdml.ext.InsertReaderResourceExtensionResult;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.prt.argument.StringArgument;
import dev.ps.prt.argument.StringArguments;

import java.io.IOException;
import java.util.Map;

public class DefineConstantsHandler implements ExtensionNodeHandler {


    private static final @NotNull String NAME = "const";

    public static final DefineConstantsHandler INSTANCE = new DefineConstantsHandler ();


    private DefineConstantsHandler (){}


    @Override
    public @NotNull String getExtensionName() {
        return NAME;
    }

    @Override
    public @Nullable InsertReaderResourceExtensionResult handleNode ( @NotNull ExtensionNodeHandlerContext context, @NotNull NodeTag nodeName )
        throws IOException, PdmlException {

        // now positioned right after the node tag

        context.skipWhitespaceAndComments();

        @Nullable StringArguments stringArguments = context.parseStringArgumentAssignments ( false );
        if ( stringArguments == null ) {
            throw context.errorAtCurrentLocation (
                "Expecting one or more constant declarations (e.g. c1 = v1 ...).",
                "MISSING_CONSTANT_DECLARATION" );
        }

        context.skipWhitespaceAndComments();
        context.requireExtensionNodeEnd ( nodeName );

        Map<String, String> declaredConstants = context.getDeclaredConstants();

        for ( StringArgument stringArgument : stringArguments.list() ) {

            String name = stringArgument.name();

            if ( ! declaredConstants.containsKey ( name ) ) {
                declaredConstants.put ( name, stringArgument.value() );
            } else {
                throw context.error (
                    "Constant '" + name + "' has already been defined with value '" + declaredConstants.get ( name ) + "'.",
                    "DUPLICATE_CONSTANT_DEFINITION",
                    stringArgument.nameLocation() );
            }
        }

        return null;
    }
}
