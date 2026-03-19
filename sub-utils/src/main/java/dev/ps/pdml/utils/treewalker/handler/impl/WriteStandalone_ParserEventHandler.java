package dev.ps.pdml.utils.treewalker.handler.impl;

import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.writer.PdmlWriter;
import dev.ps.pdml.writer.node.PdmlNodeWriter;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.pdml.utils.treewalker.handler.*;

import java.io.IOException;
import java.io.Writer;

public class WriteStandalone_ParserEventHandler implements PdmlTreeWalkerEventHandler<NodeTag, Void> {

    private final PdmlNodeWriter writer;
    private final PdmlWriter basicWriter;

    private NodeTag rootNodeName;


    public WriteStandalone_ParserEventHandler ( PdmlNodeWriter writer ) {

        this.writer = writer;
        this.basicWriter = writer.getPdmlWriter ();
        this.rootNodeName = null;
    }

    public WriteStandalone_ParserEventHandler ( Writer writer ) {

        this ( new PdmlNodeWriter ( writer ) );
    }

    @Override
    public @NotNull NodeTag onRootNodeStart ( @NotNull TaggedNodeStartEvent event ) throws IOException {

        rootNodeName = event.tag ();
        return onNodeStart_ ( event );
    }

    @Override
    public void onRootNodeEnd ( @NotNull TaggedNodeEndEvent event, @NotNull NodeTag rootNode ) throws IOException {
        onTaggedNodeEnd ( event, rootNode );
    }

    @Override
    public @NotNull NodeTag onTaggedNodeStart (
        @NotNull TaggedNodeStartEvent event, @NotNull NodeTag parentName ) throws IOException {

        return onNodeStart_ ( event );
    }

    private @NotNull NodeTag onNodeStart_ ( @NotNull TaggedNodeStartEvent event ) throws IOException {

        NodeTag name = event.tag ();
        // writer.writeNodeStart ( event.tag(), event.declaredNamespaces(), event.attributes(), event.isEmptyNode() );
        writer.writeNodeStart ( event.tag (), " ", event.declaredNamespaces(), event.attributes(), true, true );
        return name;
    }

    @Override
    public void onTaggedNodeEnd ( @NotNull TaggedNodeEndEvent event, @NotNull NodeTag name ) throws IOException {
        // if ( ! event.isEmptyNode() ) writer.getWriter().writeNodeEndSymbol();
        basicWriter.writeNodeEndChar();
    }

    @Override
    public void onText ( @NotNull TextEvent event, @NotNull NodeTag parentNode ) throws IOException {
        basicWriter.writeText ( event.text(), true );
    }

    @Override
    public void onComment ( @NotNull CommentEvent event, @NotNull NodeTag parentNode ) throws IOException {
        basicWriter.writeMultilineComment ( event.comment() );
    }

    // public @NotNull NodeName getResult() { return rootNodeName; }
    public Void getResult() { return null; }
}
