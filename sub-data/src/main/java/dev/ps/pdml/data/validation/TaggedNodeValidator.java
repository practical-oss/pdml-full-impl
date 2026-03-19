package dev.ps.pdml.data.validation;

import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.nodespec.PdmlNodeSpec;
import dev.ps.shared.basics.annotations.NotNull;

public interface TaggedNodeValidator {

    boolean validate (
        @NotNull PdmlNodeSpec nodeSpec,
        @NotNull TaggedNode taggedNode,
        @NotNull TaggedNodeValidatorContext context );
}
