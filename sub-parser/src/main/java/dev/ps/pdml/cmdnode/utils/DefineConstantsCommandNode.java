package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.cmdnode.SpecificParsingCommandNode;
import dev.ps.pdml.cmdnode.CommandNodeExecutorContext;
import dev.ps.pdml.cmdnode.CommandNodeResult;
import dev.ps.pdml.parser.util.TextNodesUtil;
import dev.ps.prt.argument.StringArgument;
import dev.ps.prt.argument.StringArguments;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.shared.text.inspection.InvalidDataException;

import java.io.IOException;
import java.util.Map;

public class DefineConstantsCommandNode extends SpecificParsingCommandNode {


    private static final @NotNull String NAME = "const";

    public static final DefineConstantsCommandNode INSTANCE = new DefineConstantsCommandNode();


    private DefineConstantsCommandNode() {
        super ( NAME, null, () -> new SimpleDocumentation (
            "Define Constants",
            "",
            "" ) );
    }


    @Override
    public @Nullable CommandNodeResult execute (
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeName ) throws IOException, PdmlException {

        // now positioned right after the node tag

        context.skipWhitespaceAndComments();

        @Nullable StringArguments stringArguments;
        if ( context.pdmlReader().isAtChar ( CorePdmlConstants.NODE_START_CHAR ) ) {
            // [name value] syntax
            try {
                stringArguments = TextNodesUtil.parseAsStringArguments ( context.pdmlParser (), true );
            } catch ( InvalidDataException e ) {
                throw new PdmlException ( e );
            }
        } else {
            // name = value syntax
            stringArguments = context.parseStringArgumentAssignments ( false );
            if ( stringArguments == null ) {
                throw context.errorAtCurrentLocation (
                    "Expecting one or more constant declarations (e.g. c1 = v1 ...).",
                    "MISSING_CONSTANT_DECLARATION" );
            }
        }

        context.skipWhitespaceAndComments();
        context.requireNodeEnd ( nodeName );

        Map<String, String> declaredConstants = context.declaredConstants ();

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
