package dev.ps.pdml.parser;

import dev.ps.pdml.data.nodespec.PdmlNodeSpecs;
import dev.ps.pdml.ext.types.PdmlTypes;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public class PdmlParserConfigBuilder {


    public static @NotNull PdmlParserConfig createDefault() {
        return new PdmlParserConfigBuilder().build();
    }


    private boolean ignoreTextAfterEndOfRootNode =
        PdmlParserConfig.DEFAULT_IGNORE_TEXT_AFTER_ROOT_NODE_END;
    private @Nullable PdmlNodeSpecs nodeSpecs =
        PdmlParserConfig.DEFAULT_NODE_SPECS;
    private @Nullable PdmlTypes types = PdmlTypes.STANDARD_TYPES;
    private boolean ignoreComments =
        PdmlParserConfig.DEFAULT_IGNORE_COMMENTS;
    private boolean allowAttributesWithoutCaret =
        PdmlParserConfig.DEFAULT_ALLOW_ATTRIBUTES_WITHOUT_CARET;


    public PdmlParserConfigBuilder() {}


    public PdmlParserConfigBuilder ignoreTextAfterEndOfRootNode (
        boolean ignoreTextAfterEndOfRootNode ) {

        this.ignoreTextAfterEndOfRootNode = ignoreTextAfterEndOfRootNode;
        return this;
    }

    public PdmlParserConfigBuilder nodeSpecs (
        @Nullable PdmlNodeSpecs nodeSpecs ) {

        this.nodeSpecs = nodeSpecs;
        return this;
    }

    public PdmlParserConfigBuilder types (
        @Nullable PdmlTypes types ) {

        this.types = types;
        return this;
    }

    public PdmlParserConfigBuilder ignoreComments (
        boolean ignoreComments ) {

        this.ignoreComments = ignoreComments;
        return this;
    }

    public PdmlParserConfigBuilder allowAttributesWithoutCaret (
        boolean allowAttributesWithoutCaret ) {

        this.allowAttributesWithoutCaret = allowAttributesWithoutCaret;
        return this;
    }

    public PdmlParserConfig build() {

        return new PdmlParserConfig (
            ignoreTextAfterEndOfRootNode,
            nodeSpecs,
            types,
            ignoreComments,
            allowAttributesWithoutCaret );
    }
}
