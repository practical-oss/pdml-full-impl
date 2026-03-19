package dev.ps.pdml.xml.eventhandlers;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.namespace.NodeNamespaces;
import dev.ps.pdml.data.namespace.NodeNamespace;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.utils.treewalker.handler.TaggedNodeStartEvent;
import dev.ps.pdml.utils.treewalker.handler.CommentEvent;
import dev.ps.pdml.utils.treewalker.handler.PdmlTreeWalkerEventHandler;
import dev.ps.pdml.utils.treewalker.handler.TextEvent;
import dev.ps.pdml.xml.XMLUtilities;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.prt.argument.StringArguments;
import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collection;

public class CreateDOM_ParserEventHandler implements PdmlTreeWalkerEventHandler<Node, Document> {

    private Document document;
    private final NodeNamespaces allNamespaces = new NodeNamespaces ( null );


    public CreateDOM_ParserEventHandler() {}


    @Override
    public void onStart() throws PdmlException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = factory.newDocumentBuilder ();
            this.document = docBuilder.newDocument();
            // this.document.setXmlStandalone ( true );
        } catch ( ParserConfigurationException e ) {
            throw new PdmlException ( e );
        }
    }

    @Override
    public @NotNull Node onRootNodeStart ( @NotNull TaggedNodeStartEvent event ) {

        Element rootElement = createElement ( event.tag () );
        addDeclaredNamespaces ( rootElement, event.declaredNamespaces() );
        addAttributes ( event.attributes(), rootElement );
        document.appendChild ( rootElement );
        return rootElement;
    }

    @Override
    public @NotNull Node onTaggedNodeStart ( @NotNull TaggedNodeStartEvent event, @NotNull Node parentElement ) {

/*
        if ( ! rootNode.isAttribute() ) {
            Element element = createElement ( rootNode );
            parentElement.appendChild ( element );
            return element;

        } else {
            Attr attribute;
            Element element = (Element) parentElement;
            if ( ! rootNode.hasNamespace() ) {
                attribute = document.createAttribute ( rootNode.getLocalName() );
                element.setAttributeNode ( attribute );
            } else {
                attribute = document.createAttributeNS (
                    rootNode.getNamespaceURI().toString(), rootNode.getName().toString() );
                element.setAttributeNodeNS(attribute);
            }
            return attribute;
        }
*/
        Element element = createElement ( event.tag () );
        addDeclaredNamespaces ( element, event.declaredNamespaces() );
        addAttributes ( event.attributes(), element );
        parentElement.appendChild ( element );
        return element;
    }

    private void addDeclaredNamespaces ( Element element, NodeNamespaces namespaces ) {

        if ( namespaces == null ) return;
        Collection<NodeNamespace> list = namespaces.list();
        if ( list == null ) return;

        for ( NodeNamespace namespace : list ) {
            allNamespaces.add ( namespace );
            String value = namespace.URI();
            /* TODO?
            if ( namespace.isDefaultNamespace() ) {
                addAttributeWithoutNamespace ( element, XMLConstants.XMLNS_ATTRIBUTE, value );
            } else {
             */
                String qualifiedName = XMLUtilities.getQualifiedNameForNamespaceAttribute ( namespace.namePrefix() );
                addAttributeWithNamespace (
                    element, XMLConstants.XMLNS_ATTRIBUTE_NS_URI, qualifiedName, value );
            // }
        }
    }

    public void addAttributes ( @Nullable StringArguments attributes, @NotNull Element element ) {

        if ( attributes == null ) return;
        // Collection<Parameter<String>> list = attributes.list();
        // if ( list == null ) return;

        // for ( StringArgument attribute : list ) {
        attributes.forEach ( attribute -> {

            String value = attribute.value();
/*
            if ( ! attribute.hasNamespace() ) {
                addAttributeWithoutNamespace ( element, attribute.getLocalNameText(), value );
            } else {
                addAttributeWithNamespace (
                    element, attribute.getNamespaceURI().toString(), attribute.getName().qualifiedName (), value );
            }
 */
            addAttributeWithoutNamespace ( element, attribute.name(), value );
        } );
    }


/*
    public void onAttributes ( @NotNull StringArguments attributes, @NotNull Node parentNode ) throws Exception {

        Element element = (Element) parentNode;

        for ( NodeAttribute attribute : attributes.getList() ) {

            if ( ! attribute.hasNamespace() ) {
                Attr DOMAttribute = document.createAttribute ( attribute.getLocalName() );
                element.setAttributeNode ( DOMAttribute );
            } else {
                Attr DOMAttribute = document.createAttributeNS (
                    attribute.getNamespaceURI().toString(), attribute.getName().fullName() );
                element.setAttributeNodeNS ( DOMAttribute );
            }
        }
    }

    public void onAttributes ( @Nullable ASTNodeAttributes attributes, @NotNull Node parentNode ) throws Exception {

        if ( attributes == null ) return;

        Element element = (Element) parentNode;

        for ( ASTNodeAttribute attribute : attributes.getList() ) {

            String value = attribute.getValueText();
            if ( ! attribute.hasNamespace() ) {
                addAttributeWithoutNamespace ( element, attribute.getLocalNameText(), value );
            } else {
                addAttributeWithNamespace (
                    element, attribute.getNamespaceURI().toString(), attribute.getName().qualifiedName (), value );
            }
        }
    }
 */

    private void addAttributeWithoutNamespace (
        @NotNull Element element, @NotNull String localName, @Nullable String value ) {

        element.setAttribute ( localName, value );
    }

    private void addAttributeWithNamespace (
        @NotNull Element element, @NotNull String URI, @NotNull String fullName, @NotNull String value ) {

        element.setAttributeNS ( URI, fullName, value );
    }

    @Override
    public void onText ( @NotNull TextEvent text, @NotNull Node parentElement ) {

/*
        if ( parentElement instanceof Element ) {
            Text textElement = document.createTextNode ( text.getText() );
            parentElement.appendChild ( textElement );

        } else if ( parentElement instanceof Attr ) {
            Attr attribute = (Attr) parentElement;
            attribute.setValue ( text.getText() );

        } else {
            throw new RuntimeException ( "Unexpected parentElement " + parentElement );
        }
*/
        if ( parentElement instanceof Element ) {
            Text textElement = document.createTextNode ( text.text() );
            parentElement.appendChild ( textElement );
        } else {
            throw new RuntimeException ( "Unexpected parentElement " + parentElement );
        }
    }

    @Override
    public void onComment ( @NotNull CommentEvent comment, @NotNull Node parentElement ) {

        Comment comment_ = document.createComment ( comment.commentWithoutDelimiters () );
        parentElement.appendChild ( comment_ );
    }

    @Override
    public @NotNull Document getResult() { return document; }


    private Element createElement ( NodeTag name ) {

        @Nullable String prefix = name.namespacePrefix();
        if ( prefix == null ) {
            return document.createElement ( name.tag () );
        } else {
            String URI = allNamespaces.getByPrefix ( prefix ).URI();
            return document.createElementNS(
                URI, name.qualifiedTag () );
        }
    }
}
