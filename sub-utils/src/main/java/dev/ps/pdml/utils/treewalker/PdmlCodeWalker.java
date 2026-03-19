package dev.ps.pdml.utils.treewalker;

import dev.ps.shared.text.range.TextRange;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.pdml.data.CorePdmlConstants;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.nodespec.PdmlNodeSpec;
import dev.ps.pdml.parser.PdmlParser;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.data.nodespec.PdmlNodeSpecs;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.pdml.utils.treewalker.handler.*;

import java.io.IOException;
import java.io.Reader;


public class PdmlCodeWalker<N, R> {

    private final @NotNull PdmlParser parser;
    public @NotNull PdmlParser getPdmlParser() { return parser; }

    private final @NotNull PdmlTokenReader reader;
    private final @NotNull PdmlTreeWalkerEventHandler<N, R> eventHandler;
    private final @Nullable PdmlNodeSpecs nodeSpecs;


    public PdmlCodeWalker (
        @NotNull PdmlParser parser,
        @NotNull PdmlTreeWalkerEventHandler<N, R> eventHandler ) {

        this.parser = parser;
        this.reader = parser.pdmlReader ();
        this.eventHandler = eventHandler;
        this.nodeSpecs = parser.config ().getNodeSpecs();

        this.parser.setOptimizeTypedNodes ( false );
    }

    public PdmlCodeWalker (
        @NotNull Reader reader,
        @NotNull ReaderResource readerResource,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlTreeWalkerEventHandler<N, R> eventHandler ) throws IOException {

        this ( PdmlParser.create ( reader, readerResource, parserConfig ), eventHandler );
    }

    /*
    @Deprecated
    public PdmlCodeWalker (
        @NotNull CharReader charReader,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlTreeWalkerEventHandler<N, R> eventHandler ) throws IOException {

        this ( PdmlParser.create ( charReader, parserConfig ), eventHandler );
    }
     */

    /*
    @Deprecated
    public PdmlCodeWalker (
        @NotNull Reader reader,
        @Nullable ReaderResource readerResource,
        @Nullable Integer currentLineNumber,
        @Nullable Integer currentColumnLineNumber,
        @NotNull PdmlParserConfig parserConfig,
        @NotNull PdmlTreeWalkerEventHandler<N, R> eventHandler ) throws IOException {

        this ( PdmlParser.create ( reader, readerResource, currentLineNumber, currentColumnLineNumber, parserConfig ), eventHandler );
    }
     */


    // TODO public boolean walk()
    public void walk() throws IOException, PdmlException {

        eventHandler.onStart();
        requireRootNode();
        eventHandler.onEnd();
    }

    private void requireRootNode() throws IOException, PdmlException {

        parser.skipWhitespaceBeforeRootNode();
        requireTaggedNode ( null, true );
        if ( ! parser.config ().getIgnoreTextAfterRootNodeEnd() ) {
            parser.requireDocumentEnd();
        }
    }

    private void requireTaggedNode ( N handlerParentNode, boolean isRootNode )
        throws IOException, PdmlException {

        @NotNull TaggedNode taggedNode = parser.requireFromNodeStartToAttributes ();
        // PdmlNodeSpec<?> nodeSpec = taggedNode.getSpec();
        PdmlNodeSpec nodeSpec = nodeSpecs == null ? null : nodeSpecs.getOrNull ( taggedNode.getTag () );
        boolean isEmptyNode = taggedNode.isEmpty() &&
            reader.isAtChar ( CorePdmlConstants.NODE_END_CHAR );

        TextRange location = taggedNode.getTextLocation ();
        TaggedNodeStartEvent startEvent = new TaggedNodeStartEvent (
            location != null ? location.startPosition () : null,
            taggedNode.getTag (),
            taggedNode.getNamespaceDefinitions(),
            taggedNode.getStringAttributes(), isEmptyNode, nodeSpec );
        N handlerNode;
        if ( isRootNode ) {
            handlerNode = eventHandler.onRootNodeStart ( startEvent );
        } else {
            handlerNode = eventHandler.onTaggedNodeStart ( startEvent, handlerParentNode );
        }

        parseChildNodes ( startEvent, handlerNode );

        requireNodeEnd ( taggedNode, startEvent, isRootNode, handlerNode );
    }

    private void requireNodeEnd (
        @NotNull TaggedNode taggedNode,
        @NotNull TaggedNodeStartEvent startEvent,
        boolean isRootNode,
        N handlerParentNode ) throws IOException, PdmlException {

        parser.requireTaggedNodeEnd ( taggedNode );

        TextRange por = taggedNode.getTextLocation ();
        TaggedNodeEndEvent event = new TaggedNodeEndEvent (
            por != null ? por.startPosition () : null,
            startEvent );
        if ( isRootNode ) {
            eventHandler.onRootNodeEnd ( event, handlerParentNode );
        } else {
            eventHandler.onTaggedNodeEnd ( event, handlerParentNode );
        }
    }

    private void parseChildNodes ( @NotNull TaggedNodeStartEvent startEvent, N handlerParentNode )
        throws IOException, PdmlException {

        while ( reader.isNotAtEnd() ) {

            if ( reader.isAtNodeEnd() ) {
                return;

            } else if ( reader.isAtNodeStart() ) {
                requireTaggedNode ( handlerParentNode, false );

            } else {
                try {
                    parser.consumeTextLeafFragmentsAndComments (
                        ( text, location ) -> {
                            TextEvent event = new TextEvent ( text, location, startEvent );
                            try {
                                eventHandler.onText ( event, handlerParentNode );
                            } catch ( IOException | PdmlException e ) {
                                throw new RuntimeException ( e );
                            }
                        },
                        ( comment, location ) -> {
                            CommentEvent event = new CommentEvent ( comment, location, startEvent );
                            try {
                                eventHandler.onComment ( event, handlerParentNode );
                            } catch ( IOException | PdmlException e ) {
                                throw new RuntimeException ( e );
                            }
                        } );

                } catch ( RuntimeException re ) {
                    if ( re.getCause() instanceof IOException ioe ) {
                        throw ioe;
                    } else if ( re.getCause() instanceof PdmlException pe ) {
                        throw pe;
                    } else {
                        throw re;
                    }
                }
            }
        }
    }
}
