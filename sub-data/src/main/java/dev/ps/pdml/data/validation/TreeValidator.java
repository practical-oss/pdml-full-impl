package dev.ps.pdml.data.validation;

@Deprecated
public class TreeValidator{}

/*
import dev.pp.pdml.data.node.tagged.TaggedNode;
import dev.pp.pdml.data.nodespec.PdmlNodeSpec;
import dev.pp.pdml.data.nodespec.PdmlNodeSpecs;
import dev.pp.core.basics.annotations.NotNull;
import dev.pp.core.basics.annotations.Nullable;
import dev.pp.core.parameters.parameters.MutableParameters;
import dev.pp.core.parameters.parameters.Parameters;
import dev.pp.core.parameters.parameters.ParametersCreator;
import dev.pp.core.parameters.parameterspecs.ParameterSpecs;
import dev.pp.core.text.inspection.handler.TextInspectionMessageHandler;
import dev.pp.core.text.inspection.message.TextInspectionError;

public class TreeValidator {

    public static boolean validate (
        @NotNull TaggedNode rootNode,
        @NotNull PdmlNodeSpecs nodeSpecs,
        boolean checkAttributes,
        @NotNull TaggedNodeValidatorContext context ) {

        // AtomicBoolean success = new AtomicBoolean ( true );
        @NotNull TextInspectionMessageHandler messageHandler = context.getMessageHandler();
        @Nullable TextInspectionError initialLastError = messageHandler.lastError();

        rootNode.treeTaggedNodeStream ( true ).forEach ( taggedNode -> {

            // nodeSpec might have been assigned already (e.g. by parser)
            @Nullable PdmlNodeSpec nodeSpec = taggedNode.getSpec();
            // DebugUtils.writeLineBreak();
            // DebugUtils.writeNameValue ( "branchNode", branchNode );
            // DebugUtils.writeNameValue ( "parser nodeSpec", nodeSpec );

            if ( nodeSpec == null ) {
                nodeSpec = nodeSpecs.getOrNull ( taggedNode.getTag () );
                taggedNode.setSpec ( nodeSpec );
            }
            // DebugUtils.writeNameValue ( "nodeSpec", nodeSpec );

            if ( nodeSpec == null ) {
                context.errorDetected (
                    taggedNode,
                    "Invalid node tag '" + taggedNode.qualifiedTag () + "'.",
                    "INVALID_NODE_NAME" );
                // success.set ( false );
                return;
            }

            if ( checkAttributes ) {
                // if ( ! checkAndSetAttributes ( branchNode, nodeSpec, context.getMessageHandler () ) ) {
                //     success.set ( false );
                // }
                checkAndSetAttributes ( taggedNode, nodeSpec, context.getMessageHandler () );
            }

            // if ( ! nodeSpec.validate ( branchNode, context ) ) {
            //     success.set ( false );
            // }
            nodeSpec.validate ( taggedNode, context );
        } );

        // return success.get ();
        TextInspectionError newError = messageHandler.newestErrorAfterInitialLastError ( initialLastError );
        return newError == null;
    }

    public static boolean checkAndSetAttributes (
        @NotNull TaggedNode taggedNode,
        @NotNull PdmlNodeSpec nodeSpec,
        @NotNull TextInspectionMessageHandler messageHandler ) {

        throw new RuntimeException ( "Not yet implemented" );

        // TODO rewrite
        // @NotNull MutableParameters<String> stringAttributes = taggedNode.getStringAttributes ();
        // @Nullable ParameterSpecs<?> attributeSpecs = nodeSpec.getAttributeSpecs();

        // @Nullable TextInspectionError initialLastError = messageHandler.lastError();
        // @Nullable Parameters<?> validatedAttributes = ParametersCreator.createFromStringParameters (
        //     stringAttributes,
        //     stringAttributes.getStartToken(),
        //     attributeSpecs,
        //     messageHandler );
        // taggedNode.setTypedAttributes ( validatedAttributes );
        // TextInspectionError newError = messageHandler.newestErrorAfterInitialLastError ( initialLastError );
        // return newError != null;
    }
}
 */
