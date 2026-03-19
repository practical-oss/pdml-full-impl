package dev.ps.pdml.xml.eventhandlers;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.namespace.NodeNamespaces;
import dev.ps.pdml.data.namespace.NodeNamespace;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.string.StringConstants;
import dev.ps.pdml.utils.treewalker.handler.*;
import dev.ps.prt.argument.StringArgument;
import dev.ps.prt.argument.StringArguments;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class WriteXMLParserEventHandler implements PdmlTreeWalkerEventHandler<NodeTag, String> {

    private final XMLStreamWriter XMLWriter;
    private final NodeNamespaces allNamespaces = new NodeNamespaces ( null );


    public WriteXMLParserEventHandler ( Writer writer ) throws XMLStreamException {

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        // factory.setProperty ( XMLOutputFactory.IS_REPAIRING_NAMESPACES, true );
        this.XMLWriter = factory.createXMLStreamWriter ( writer );
    }

    public WriteXMLParserEventHandler ( XMLStreamWriter XMLWriter ) {
        this.XMLWriter = XMLWriter;
    }


    @Override
    public void onStart() throws PdmlException {

        try {
            XMLWriter.writeStartDocument ( StandardCharsets.UTF_8.name(), "1.0" );
            XMLWriter.writeCharacters ( StringConstants.OS_LINE_BREAK );
        } catch ( XMLStreamException e ) {
            throw new PdmlException ( e );
        }
    }

    @Override
    public void onEnd() throws PdmlException {

        try {
            XMLWriter.writeEndDocument();
            XMLWriter.writeCharacters ( StringConstants.OS_LINE_BREAK );
        } catch ( XMLStreamException e ) {
            throw new PdmlException ( e );
        }
    }

    @Override
    public @NotNull NodeTag onRootNodeStart ( @NotNull TaggedNodeStartEvent event )
        throws PdmlException {

        return onNodeStart ( event );
    }

    @Override
    public void onRootNodeEnd ( @NotNull TaggedNodeEndEvent event, @NotNull NodeTag node )
        throws PdmlException {

        onTaggedNodeEnd ( event, node );
    }

    @Override
    public @NotNull NodeTag onTaggedNodeStart ( @NotNull TaggedNodeStartEvent event, @NotNull NodeTag parentNode )
        throws PdmlException {

        return onNodeStart ( event );
    }

    private @NotNull NodeTag onNodeStart ( @NotNull TaggedNodeStartEvent event )
        throws PdmlException {

        addNamespaces ( event );

        NodeTag name = event.tag ();
        String localName = name.tag ();

        try {
            String namespacePrefix = name.namespacePrefix();
            if ( namespacePrefix == null ) {
                if ( event.isEmptyNode() ) {
                    XMLWriter.writeEmptyElement ( localName );
                } else {
                    XMLWriter.writeStartElement ( localName );
                }

            } else {
                String URI = allNamespaces.getByPrefix ( namespacePrefix ).URI();
                if ( event.isEmptyNode() ) {
                    XMLWriter.writeEmptyElement ( namespacePrefix, localName, URI );
                } else {
                    XMLWriter.writeStartElement ( namespacePrefix, localName, URI );
                }
            }

            writeDeclaredNamespaces ( event.declaredNamespaces() );
            writeAttributes ( event.attributes() );
        } catch ( XMLStreamException e ) {
            throw new PdmlException ( e );
        }

        return name;
    }

    public void onTaggedNodeEnd ( @NotNull TaggedNodeEndEvent event, @NotNull NodeTag name )
        throws PdmlException {

        if ( ! event.startEvent().isEmptyNode() ) {
            try {
                XMLWriter.writeEndElement();
            } catch ( XMLStreamException e ) {
                throw new PdmlException ( e );
            }
        }
    }

    @Override
    public void onText ( @NotNull TextEvent text, @NotNull NodeTag parentNode )
        throws PdmlException {

        try {
            XMLWriter.writeCharacters ( text.text() );
        } catch ( XMLStreamException e ) {
            throw new PdmlException ( e );
        }
    }

    @Override
    public void onComment ( @NotNull CommentEvent comment, @NotNull NodeTag parentNode )
        throws PdmlException {

        try {
            XMLWriter.writeComment ( comment.commentWithoutDelimiters () );
        } catch ( XMLStreamException e ) {
            throw new PdmlException ( e );
        }
    }

    public @NotNull String getResult() { return "foo"; }


    private void addNamespaces ( @NotNull TaggedNodeStartEvent event ) {

        NodeNamespaces namespaces = event.declaredNamespaces();
        if ( namespaces == null ) return;
        Collection<NodeNamespace> list = namespaces.list();
        if ( list == null ) return;

        for ( NodeNamespace namespace : list ) {
            allNamespaces.add ( namespace );
        }
    }

    private void writeDeclaredNamespaces ( NodeNamespaces namespaces )
        throws XMLStreamException {

        if ( namespaces == null ) return;
        Collection<NodeNamespace> list = namespaces.list();
        if ( list == null ) return;

        for ( NodeNamespace namespace : list ) {
            allNamespaces.add ( namespace );
            String value = namespace.URI();
            // TODO?
            // if ( namespace.isDefaultNamespace() ) {
            if ( false ) {
                XMLWriter.writeAttribute (
                    XMLConstants.XMLNS_ATTRIBUTE,
                    value );
            } else {
                XMLWriter.writeAttribute (
                    XMLConstants.XMLNS_ATTRIBUTE,
                    XMLConstants.XML_NS_URI,
                    namespace.namePrefix(),
                    value );
            }
        }
    }

    public void writeAttributes ( @Nullable StringArguments attributes )
        throws XMLStreamException {

        if ( attributes == null ) return;

        for ( StringArgument attribute : attributes.list() ) {
            /*
            if ( ! attribute.hasNamespace() ) {
                XMLWriter.writeAttribute ( attribute.getLocalNameText(), attribute.getValueText() );
            } else {
                XMLWriter.writeAttribute (
                    attribute.getNamespacePrefixText(),
                    attribute.getNamespaceURIText(),
                    attribute.getLocalNameText(),
                    attribute.getValueText() );
            }
             */
            XMLWriter.writeAttribute ( attribute.name(), attribute.value() );
        };
    }
}
