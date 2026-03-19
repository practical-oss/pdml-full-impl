package dev.ps.pdml.xml;

import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.data.node.tagged.TaggedNode;
import dev.ps.pdml.data.node.leaf.CommentLeaf;
import dev.ps.pdml.data.node.leaf.TextLeaf;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PdmlTreeToXMLTreeConverter {

    public PdmlTreeToXMLTreeConverter (){}


    public @NotNull Document convert ( @NotNull TaggedNode pdmlRootNode )
        throws ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document document = docBuilder.newDocument();
        Element rootElement = convertPdmlTaggedNode ( pdmlRootNode, document );
        document.appendChild ( rootElement );
        return document;
    }


    private @NotNull Element convertPdmlTaggedNode (
        @NotNull TaggedNode pdmlTaggedNode,
        @NotNull Document xmlDocument ) {

        NodeTag name = pdmlTaggedNode.getTag ();
        Element xmlElement = createXMLElement ( name, xmlDocument );

        addAttributes ( pdmlTaggedNode, xmlElement );
        appendChildNodes ( pdmlTaggedNode, xmlElement, xmlDocument );

        return xmlElement;
    }

    private void addAttributes (
        @NotNull TaggedNode pdmlTaggedNode,
        @NotNull Element xmlElement ) {

        if ( pdmlTaggedNode.hasNoAttributes() ) {
            return;
        }

        pdmlTaggedNode.getStringAttributes().forEach ( attribute -> {
            // TODO check if valid XML attribute tag
            xmlElement.setAttribute (
                attribute.name(),
                attribute.valueAsNonNullString() );
        } );
    }

    private void appendChildNodes (
        @NotNull TaggedNode pdmlTaggedNode,
        @NotNull Element xmlElement,
        @NotNull Document xmlDocument ) {

        if ( pdmlTaggedNode.hasNoChildNodes() ) {
            return;
        }

        pdmlTaggedNode.getChildNodes().forEach ( childNode -> {

            if ( childNode instanceof TaggedNode taggedNode ) {
                Element xmlChildElement = convertPdmlTaggedNode (
                    taggedNode, xmlDocument );
                xmlElement.appendChild ( xmlChildElement );

            } else if ( childNode instanceof TextLeaf textChild ) {
                Text textElement = xmlDocument.createTextNode ( textChild.getText() );
                xmlElement.appendChild ( textElement );

            } else if ( childNode instanceof CommentLeaf commentChild ) {
                Comment commentElement = xmlDocument.createComment ( commentChild.textWithoutDelimiters() );
                xmlElement.appendChild ( commentElement );

            } else {
                throw new IllegalStateException (
                    "Unexpected childNode type " + childNode.getClass () );
            }
        } );
    }

    private Element createXMLElement (
        @NotNull NodeTag name,
        @NotNull Document xmlDocument ) {

        @Nullable String prefix = name.namespacePrefix();
        @NotNull String localName = name.tag ();
        // TODO check if tag is valid in XML
        if ( prefix == null ) {
            return xmlDocument.createElement ( localName );
        } else {
            // TODO
            // String URI = allNamespaces.getByPrefix ( prefix ).URI();
            String URI = null;
            // TODO : in prefix or local tag
            return xmlDocument.createElementNS(
                URI, prefix + ":" + localName );
        }
    }
}
