/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util.xml;

import org.w3c.dom.*;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.*;

/**
 * Uses the XML functions from the bridge the construct DOM elements
 * representing MMBase data structures.
 *
 * @author Michiel Meeuwissen
 * @author Eduard Witteveen
 * @version $Id: Generator.java,v 1.10 2002-06-11 20:51:34 eduard Exp $
 */
public class Generator {   
    private static Logger log = Logging.getLoggerInstance(Generator.class.getName());
    private Document tree = null;

    /**
     * To use the functionality of this class, instantiate it with
     * with a DOM Document in which the Elements must be put.
     */
    public Generator(javax.xml.parsers.DocumentBuilder documentBuilder) {
        this.tree = documentBuilder.newDocument();
        Element rootElement = tree.createElement("objects");
        tree.appendChild(rootElement);
    }
    
    /**
     * Returns the working DOM document.
     * @return The document, build with the operations done on the generator class
     */
    public Document getDocument() {
        return tree;
    }

    /**
     * Returns the document as a String.
     * @return the xml generated as an string
     */
    public String toString() {
        return toString(false);
    }

    /**
     * Returns the document as a String.
     * @param ident if the string has to be idented
     * @ return the xml generated as an string
     */
    public String toString(boolean ident) {
        try {
            org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(tree);
            if(ident) {
                format.setIndenting(true);
                format.setPreserveSpace(false);
                //  format.setOmitXMLDeclaration(true);
                //  format.setOmitDocumentType(true);
            }
            java.io.StringWriter result = new java.io.StringWriter();
            org.apache.xml.serialize.XMLSerializer prettyXML = new org.apache.xml.serialize.XMLSerializer(result, format);
            prettyXML.serialize(tree);
            return result.toString();
        }
        catch (Exception e) {
            return e.toString();
        }
    }
    
    /**
     * Adds a field to the DOM Document. This means that there will
     * also be added a Node if this is necessary.
     * @param An MMbase bridge Node.
     * @param An MMBase bridge Field.
     */
    public void add(org.mmbase.bridge.Node node, Field fieldDefinition) {
        Element object = getNode(node);

        // get the field...
        Element field = (Element) object.getFirstChild();
        while(field != null && !fieldDefinition.getName().equals(field.getAttribute("name"))) {
            field = (Element) field.getNextSibling();
        }
        // when not found, we are in a strange situation..
        if(field==null) throw new BridgeException("field with name: " + fieldDefinition.getName() + " of node " + node.getNumber() + " with  nodetype: " + fieldDefinition.getNodeManager().getName() + " not found, while it should be in the node skeleton.. xml:\n" + toString(true));        
        // when it is filled, we can return
        Attr notfilled = field.getAttributeNode("notfilled");
        if(notfilled == null) return;
        // was not filled, so fill it... first clear the not filled attribute
        field.removeAttributeNode(notfilled);
        // now fill it with the new info...
        // format
        field.setAttribute("format", getFieldFormat(node, fieldDefinition));
        // the value
        switch(fieldDefinition.getType()) {
            case Field.TYPE_XML:
                Document doc = node.getXMLValue(fieldDefinition.getName());                
                // only fill the field, if field has a value..
                if(doc!= null) {                    
                    // put the xml inside the field...
                    field.appendChild(importDocument(field, doc));
                }
            break;
            case Field.TYPE_BYTE:
                org.mmbase.util.transformers.Base64 transformer = new org.mmbase.util.transformers.Base64();
                field.appendChild(tree.createTextNode(transformer.transform(node.getByteValue(fieldDefinition.getName()))));
            break;
            default:
                field.appendChild(tree.createTextNode(node.getStringValue(fieldDefinition.getName())));
        }
        // or do we need more?
    }

    /**
     * Adds one Node to a DOM Document.
     * @param An MMBase bridge Node.
     */
    public void add(org.mmbase.bridge.Node node) {
        // process all the fields..
        FieldIterator i = node.getNodeManager().getFields().fieldIterator();
        while(i.hasNext()) {
            add(node, i.nextField());
        }                
    }

    /**
     * Adds one Relation to a DOM Document.
     * @param An MMBase bridge Node.
     */
    public void add(Relation relation) {        
        add((org.mmbase.bridge.Node)relation);   
    }

    /**
     * Adds a whole MMBase bridge NodeList to the DOM Document.
     * @param An MMBase bridge NodeList.
     */
    public void add(org.mmbase.bridge.NodeList nodes) {    
        NodeIterator i = nodes.nodeIterator();
        while(i.hasNext()) {
            add(i.nextNode());
        }    
    }

    /**
     * Adds one Relation to a DOM Document.
     * @param An MMBase bridge Node.
     */
    public void add(RelationList relations) {
        RelationIterator i = relations.relationIterator();
        while(i.hasNext()) {
            add(i.nextRelation());
        }
    }

    private Element getNode(org.mmbase.bridge.Node node) { 
        // MMBASE BUG...
        // we dont know if we have the correct typee...
        node = node.getCloud().getNode(node.getNumber());
        
        // if we are a relation,.. behave like one!
        // why do we find it out now, and not before?       
             
        // TODO: reseach!!
        boolean getElementByIdWorks = false;
        Element object = null;
        if(getElementByIdWorks) {
            object = tree.getElementById("" + node.getNumber());        
        }
        else {
            // TODO: this code should be removed!! but other code doesnt work :(
            // this cant be fast in performance...
            String xpath = "//*[@id='"+node.getNumber()+"']";
            try {
                object = (Element) org.apache.xpath.XPathAPI.selectSingleNode(tree.getDocumentElement(), xpath);
            }
            catch(javax.xml.transform.TransformerException te) {
                String msg = "error executing query: '" + xpath + "'";
                log.error(msg);
                log.error(Logging.stackTrace(te));
                throw new BridgeException(msg);
            }
        }        
        if(object != null) return object;

        // if it is a realtion... first add source and destination info thingies..
        // can only happen after the node = node.getCloud().getNode(node.getNumber()); thing!
        if(node instanceof Relation) {
            Relation relation = (Relation) node;
            getNode(relation.getSource()).appendChild(createRelationEntry(relation, relation.getSource()));
            getNode(relation.getDestination()).appendChild(createRelationEntry(relation, relation.getDestination()));
        }
        
        // node didnt exist, so we need to create it...
        object = tree.createElement("object");        
        // the id...
        object.setAttribute("id", "" + node.getNumber());
        // the type...
        object.setAttribute("type", node.getNodeManager().getName());
        
        // add the fields (empty)
        FieldIterator i = node.getNodeManager().getFields().fieldIterator();
        while(i.hasNext()) {
            Field fieldDefinition = i.nextField();
            Element field = tree.createElement("field");            
            // the name
            field.setAttribute("name", fieldDefinition.getName());
            if(fieldDefinition.getName().equals("otype") || fieldDefinition.getName().equals("number")) {
                field.setAttribute("format", getFieldFormat(node, fieldDefinition));
                field.appendChild(tree.createTextNode(node.getStringValue(fieldDefinition.getName())));
            }
            else {
                // that it is not filled yet...
                field.setAttribute("notfilled", "");
            }
            // add it to the object
            object.appendChild(field);
        }        
        tree.getDocumentElement().appendChild(object);
        return object;        
    }    
    
    private org.w3c.dom.Element importDocument(org.w3c.dom.Element fieldElement, Document toImport) {
        String namespace = toImport.getDoctype().getSystemId();
        String prefix = toImport.getDocumentElement().getTagName() + ":";        
        log.debug("using namepace: " + namespace + " with prefix: " + prefix);        
        return importElement(fieldElement, toImport.getDocumentElement(), namespace, prefix);
    }

    private org.w3c.dom.Element importElement(org.w3c.dom.Element parent, org.w3c.dom.Element toImport, String namespace, String prefix) {
        // first create the Element
        Element current = parent.getOwnerDocument().createElementNS(namespace, prefix + toImport.getTagName());
        
        // add all the attributs..
        org.w3c.dom.NamedNodeMap namednodes = toImport.getAttributes();        
        for(int i=0; i < namednodes.getLength(); i++) {
            org.w3c.dom.Node namesnode = namednodes.item(i);
            switch(namesnode.getNodeType()) {
                case org.w3c.dom.Node.ATTRIBUTE_NODE:
                    Attr attr = (Attr)namesnode;
                    if(attr.getNamespaceURI() == null) {
                        String name = attr.getName();
                        // when there is a : inside the name, assume that it _should_ be a namespace :p
                        if(name.indexOf(':') != -1) {
                            // we have somekinda namespace thingie...
                            current.setAttribute(attr.getName(), attr.getValue());
                        }
                        else {
                            current.setAttributeNS(namespace, prefix + attr.getName(), attr.getValue());
                        }                        
                    }
                    else {
                        current.setAttribute(attr.getName(), attr.getValue());
                    }                    
                break;
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                case org.w3c.dom.Node.COMMENT_NODE:
                case org.w3c.dom.Node.ELEMENT_NODE:
                case org.w3c.dom.Node.TEXT_NODE:
                case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE :
                case org.w3c.dom.Node.DOCUMENT_NODE:
                case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
                case org.w3c.dom.Node.ENTITY_NODE:
                case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:
                case org.w3c.dom.Node.NOTATION_NODE:
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    throw new RuntimeException("type #" + namesnode.getNodeType() +"not implemented is not implemented");
                default:
                    throw new RuntimeException("type #" + namesnode.getNodeType() + "was unknown!");
            }
        }
        // add to the parent
        parent.appendChild(current);        
        
        // add all the childnodes...
        org.w3c.dom.Node childnode = toImport.getFirstChild();
        while(childnode != null) {
             switch(childnode.getNodeType()) {              
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // throw new RuntimeException("not implemented");
                    CDATASection cdata = current.getOwnerDocument().createCDATASection(((CDATASection)childnode).getData());
                    current.appendChild(cdata);
                break;
                case org.w3c.dom.Node.COMMENT_NODE:
                    Comment comment = current.getOwnerDocument().createComment(((Comment)childnode).getData());
                    current.appendChild(comment);
                break;                    
                case org.w3c.dom.Node.ELEMENT_NODE:
                    importElement(current, (Element)childnode, namespace, prefix);
                break;
                case org.w3c.dom.Node.TEXT_NODE:
                    Text text = current.getOwnerDocument().createTextNode(((Text)childnode).getData()); 
                    current.appendChild(text);
                break;
                
                case org.w3c.dom.Node.ATTRIBUTE_NODE:
                case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE :
                case org.w3c.dom.Node.DOCUMENT_NODE:
                case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
                case org.w3c.dom.Node.ENTITY_NODE:
                case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:
                case org.w3c.dom.Node.NOTATION_NODE:
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    throw new RuntimeException("type #" + childnode.getNodeType() +"not implemented is not implemented");
                default:
                    throw new RuntimeException("type #" + childnode.getNodeType() + "was unknown!");
            }
            // go to nextone..
            childnode = childnode.getNextSibling();
        }
        // return the Element...
        return current;
    }
    
    private String getFieldFormat(org.mmbase.bridge.Node node, Field field) {
        switch (field.getType()) {
            case Field.TYPE_XML:
                return "xml";
            case Field.TYPE_STRING:
                return "string";
            case Field.TYPE_NODE:
                return "object";  // better would be "node" ?
            case Field.TYPE_INTEGER:
            case Field.TYPE_LONG:
                // was it a builder?
                String fieldName = field.getName();
                String guiType = field.getGUIType();

                // I want a object database type!
                if(fieldName.equals("otype")
                || fieldName.equals("number")                
                || fieldName.equals("snumber")
                || fieldName.equals("dnumber")
                || fieldName.equals("rnumber")
                || fieldName.equals("role")
                || guiType.equals("reldefs")) {
                    return "object";  // better would be "node" ?
                }
                if(guiType.equals("eventtime")) {
                    return "date";
                }
            case Field.TYPE_FLOAT:
            case Field.TYPE_DOUBLE:
                return "numeric";
            case Field.TYPE_BYTE:
                return "bytes";
            default:
                throw new RuntimeException("could not find field-type for:" + field.getType() + " for field: " + field);
        }
    }

    private Element createRelationEntry(Relation relation, org.mmbase.bridge.Node relatedNode) {
        Element fieldElement = tree.createElement("relation");


        // we have to know what the relation type was...
        Cloud cloud = relation.getCloud();
        org.mmbase.bridge.Node reldef = cloud.getNode(relation.getStringValue("rnumber"));


        org.w3c.dom.Attr attr;
        // the role
        attr = tree.createAttribute("role");
        if(relation.getSource().getNumber() ==  relatedNode.getNumber()) {
            attr.setValue(reldef.getStringValue("sname"));
        }
        else {
            attr.setValue(reldef.getStringValue("dname"));
        }
        fieldElement.setAttributeNode(attr);

        // object
        attr = tree.createAttribute("object");
        attr.setValue("" + relation.getNumber());
        fieldElement.setAttributeNode(attr);

        // related
        attr = tree.createAttribute("related");
        if(relation.getSource().getNumber() ==  relatedNode.getNumber()) {
            attr.setValue("" + relation.getDestination().getNumber());
        }
        else {
            attr.setValue("" + relation.getSource().getNumber());
        }
        fieldElement.setAttributeNode(attr);

        // type
        attr = tree.createAttribute("type");
        if(relation.getSource().getNumber() ==  relatedNode.getNumber()) {
            attr.setValue("source");
        }
        else {
            attr.setValue("destination");
        }
        fieldElement.setAttributeNode(attr);
        return fieldElement;
    }    
}
