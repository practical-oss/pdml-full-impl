package dev.ps.pdml.core.parser;

public class CorePdmlParserConfig {


    public static final boolean DEFAULT_IGNORE_TEXT_AFTER_ROOT_NODE_END = false;


    protected final boolean ignoreTextAfterRootNodeEnd;
    public boolean getIgnoreTextAfterRootNodeEnd () {
        return ignoreTextAfterRootNodeEnd;
    }


    public CorePdmlParserConfig (
        boolean ignoreTextAfterRootNodeEnd ) {

        this.ignoreTextAfterRootNodeEnd = ignoreTextAfterRootNodeEnd;
    }

    public CorePdmlParserConfig() {
        this ( DEFAULT_IGNORE_TEXT_AFTER_ROOT_NODE_END );
    }
}


