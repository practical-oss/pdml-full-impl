package dev.ps.pdml.data.nodespec;

import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.validation.TaggedNodeValidator;
import dev.ps.pdml.data.validation.TaggedNodeValidatorContext;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.prt.parameter.Parameters;

import java.util.function.Supplier;

// TODO? implements NamedObject, DocumentedObject
public class PdmlNodeSpec {

    private final @NotNull NodeTag tag;
    public @NotNull NodeTag getTag() { return tag; }

    // TODO? private final @Nullable Set<NodeName> alternativeNames;

    // private final @Nullable PdmlType<T> type;
    // public @Nullable PdmlType<T> getType() { return type; }
    private final @Nullable String typeName;
    public @Nullable String getTypeName() { return typeName; }

    /*
    private final @Nullable ParameterSpecs<?> attributeSpecs;
    public @Nullable ParameterSpecs<?> getAttributeSpecs () { return attributeSpecs; }
     */

    private final @Nullable Parameters attributeSpecs;
    public @Nullable Parameters getAttributeSpecs() { return attributeSpecs; }

    private final boolean hasOnlyAttributes;
    public boolean hasOnlyAttributes() { return hasOnlyAttributes; }

    // private final boolean hasAllAttributesOnTagLine;
    // public boolean hasAllAttributesOnTagLine () { return hasAllAttributesOnTagLine; }

    private final @Nullable TaggedNodeValidator validator;
    public @Nullable TaggedNodeValidator getValidator() { return validator; }

    private final @Nullable Supplier<SimpleDocumentation> documentation;
    public @Nullable Supplier<SimpleDocumentation> getDocumentationSupplier() { return documentation; }

    // private final @Nullable PXMLExtensionHandler extensionHandler;
    // private final @Nullable Function<ASTNode, NodeValidationError> validator;
    // private final boolean attributesAllowed = true;


    public PdmlNodeSpec (
        @NotNull NodeTag tag,
        // @Nullable PdmlType<T> type,
        @Nullable String typeName,
        @Nullable Parameters attributeSpecs,
        boolean hasOnlyAttributes,
        // boolean hasAllAttributesOnTagLine,
        @Nullable TaggedNodeValidator validator,
        @Nullable Supplier<SimpleDocumentation> documentation ) {

        this.tag = tag;
        // this.type = type;
        this.typeName = typeName;
        this.attributeSpecs = attributeSpecs;
        this.hasOnlyAttributes = hasOnlyAttributes;
        // this.hasAllAttributesOnTagLine = hasAllAttributesOnTagLine;
        this.validator = validator;
        this.documentation = documentation;
    }

    public PdmlNodeSpec (
        @NotNull NodeTag tag,
        // @Nullable PdmlType<T> type,
        @Nullable String typeName,
        @Nullable Parameters attributeSpecs,
        @Nullable Supplier<SimpleDocumentation> documentation ) {

        this ( tag, typeName, attributeSpecs, false, null, documentation );
    }


    public boolean validate (
        @NotNull TaggedNode taggedNode,
        @NotNull TaggedNodeValidatorContext context ) {

        if ( validator == null ) {
            return true;
        } else {
            return validator.validate ( this, taggedNode, context );
        }
    }

    /*
    public @Nullable ParameterSpec<?> getFirstPositionalAttributeOrNull () {
        return attributeSpecs != null ? attributeSpecs.firstPositionalParameterOrNull () : null;
    }

    public @Nullable String getFirstPositionalAttributeNameOrNull () {
        @Nullable ParameterSpec<?> firstPositional = getFirstPositionalAttributeOrNull();
        return firstPositional != null ? firstPositional.getName() : null;
    }
     */

    public @Nullable SimpleDocumentation getDocumentation() {
        Supplier<SimpleDocumentation> supplier = getDocumentationSupplier();
        return supplier != null ? supplier.get() : null;
    }

    public @Nullable String getDocumentationTitle() {
        SimpleDocumentation doc = getDocumentation();
        return doc != null ? doc.title () : null;
    }

    public @Nullable String getDocumentationDescription() {
        SimpleDocumentation doc = getDocumentation();
        return doc != null ? doc.description () : null;
    }

    public @Nullable String getDocumentationExamples() {
        SimpleDocumentation doc = getDocumentation();
        return doc != null ? doc.examples () : null;
    }

    public @NotNull String qualifiedName() { return tag.qualifiedTag (); }

    @Override public String toString() { return tag.toString(); }
}
