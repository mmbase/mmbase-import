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
 * @version $Id: Generator.java,v 1.9 2002-05-30 15:37:14 eduard Exp $
 */
public  class Generator {
   
    private static Logger log = Logging.getLoggerInstance(Generator.class.getName());
    private Document tree;

    /**
     * To use the functionality of this class, instantiate it with
     * with a DOM Document in which the Elements must be put.
     */
    public Generator(javax.xml.parsers.DocumentBuilder documentBuilder) {
        this.tree = documentBuilder.newDocument();
    }

    /**
     * Adds a field to the DOM Document. This means that there will
     * also be added a Node if this is necessary.
     * @param An MMbase bridge Node.
     * @param An MMBase bridge Field.
     * @return The field which added.
     */
    public Element addField(org.mmbase.bridge.Node node, Field field) {
        Element rootElement = getRootElement();
        if(rootElement == null) {
            rootElement = addRootElement(node);
        }
        Element nodeElement = getNodeElement(rootElement, node);
        if(nodeElement == null) {
            nodeElement = addNodeElement(rootElement, node);
        }
        Element fieldElement = getFieldElement(nodeElement, field);
        if(fieldElement == null) {
            fieldElement = addFieldElement(nodeElement, node, field);
        }
        return fieldElement;
    }

    /**
     * Adds one Node to a DOM Document.
     * @param An MMBase bridge Node.
     * @return The node which added.
     */
    public Element addNode(org.mmbase.bridge.Node node) {
        Element rootElement = getRootElement();
        if(rootElement == null) {
            rootElement = addRootElement(node);
        }
        Element nodeElement = getNodeElement(rootElement, node);

        // when not there, create and add all the fields...
        if(nodeElement == null) {
            // when we are a relation, add relation stuff....
            if(node instanceof Relation) {
                Relation relation = (Relation) node;
                nodeElement = addNodeElement(rootElement, node);
                Element sourceElement = addNode(relation.getSource());
                Element destinationElement = addNode(relation.getDestination());

                sourceElement.appendChild(createRelationEntry(relation, true));
                destinationElement.appendChild(createRelationEntry(relation, false));
            }
            // adding a normal node
            else {
                nodeElement = addNodeElement(rootElement, node);
            }

            // process the fields
            FieldIterator i = node.getNodeManager().getFields().fieldIterator();
            while(i.hasNext()) {
                Field field = i.nextField();
                addFieldElement(nodeElement, node, field);
            }
        }
        return nodeElement;
    }

    /**
     * Adds one Relation to a DOM Document.
     * @param An MMBase bridge Node.
     * @return The node which was added
     */
    public Element addRelation(Relation relation) {
        return addNode(relation);
    }

    /**
     * Adds a whole MMBase bridge NodeList to the DOM Document.
     * @param An MMBase bridge NodeList.
     */
    public void addNodeList(org.mmbase.bridge.NodeList nodes) {
        NodeIterator i = nodes.nodeIterator();
        while(i.hasNext()) {
            addNode(i.nextNode());
        }
    }

    /**
     * Adds one Relation to a DOM Document.
     * @param An MMBase bridge Node.
     */
    public void addRelationList(RelationList relations) {
        RelationIterator i = relations.relationIterator();
        while(i.hasNext()) {
            addNode(i.nextRelation());
        }
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
     * This is a helper function that Executes an XPATH query on the give DOM Node.
     * Its only use is to do some error handling.
     * @param A XPath query (as a string)
     * @return An DOM Element or null (if nothing found)
     */
    protected Element getXMLElement(org.w3c.dom.Node node, String xpath) {
        if (log.isDebugEnabled()) log.debug("gonna execute the query:" + xpath);
        Element found = null;
        try {
            found = (Element) org.apache.xpath.XPathAPI.selectSingleNode(node, xpath);
        }
        catch(javax.xml.transform.TransformerException te) {
            String msg = "error executing query: '" + xpath + "'";
            log.error(msg);
            log.error(Logging.stackTrace(te));
            throw new BridgeException(msg);
        }
        return found;
    }

    private Element getRootElement() {
        return getXMLElement(tree, "/objects");
    }

    private Element getNodeElement(Element rootElement, org.mmbase.bridge.Node node) {
        return getXMLElement(rootElement, "object[@id='" + node.getNumber() + "']");
    }

    private Element getFieldElement(Element nodeElement, Field field) {
        return getXMLElement(nodeElement, "field[@name='" + field.getName() + "']");
    }

    private Element addRootElement(org.mmbase.bridge.Node node) {        
        Element rootElement = tree.createElement("objects");
        org.w3c.dom.Attr attr = tree.createAttribute("root");
        attr.setValue("" + node.getNumber());
        rootElement.setAttributeNode(attr);
        tree.appendChild(rootElement);
        return rootElement;
    }

    private Element addNodeElement(Element rootElement, org.mmbase.bridge.Node node) {
        Element nodeElement = tree.createElement("object");

        // the id...
        org.w3c.dom.Attr attr = tree.createAttribute("id");
        attr.setValue("" + node.getNumber());
        nodeElement.setAttributeNode(attr);

        // the type...
        attr = tree.createAttribute("type");
        attr.setValue(node.getNodeManager().getName());
        nodeElement.setAttributeNode(attr);

        rootElement.appendChild(nodeElement);
        return nodeElement;
    }

    private Element addFieldElement(Element nodeElement, org.mmbase.bridge.Node node, Field field) {
        Element fieldElement = tree.createElement("field");

        org.w3c.dom.Attr attr;
        // the name...
        attr = tree.createAttribute("name");
        attr.setValue(field.getName());
        fieldElement.setAttributeNode(attr);
/*
        // position create
        attr = tree.createAttribute("position-create");
        attr.setValue("" + getFieldPosition(field, node.getNodeManager().getFields(NodeManager.ORDER_CREATE)));
        fieldElement.setAttributeNode(attr);

        // position search
        attr = tree.createAttribute("position-search");
        attr.setValue("" + getFieldPosition(field, node.getNodeManager().getFields(NodeManager.ORDER_SEARCH)));
        fieldElement.setAttributeNode(attr);

        // position edit
        attr = tree.createAttribute("position-edit");
        attr.setValue("" + getFieldPosition(field, node.getNodeManager().getFields(NodeManager.ORDER_EDIT)));
        fieldElement.setAttributeNode(attr);

        // position list
        attr = tree.createAttribute("position-list");
        attr.setValue("" + getFieldPosition(field, node.getNodeManager().getFields(NodeManager.ORDER_LIST)));
        fieldElement.setAttributeNode(attr);
*/
        // format
        attr = tree.createAttribute("format");
        attr.setValue(getFieldFormat(node, field));
        fieldElement.setAttributeNode(attr);

        // insert the actual value inside the field thing!
        switch(field.getType()) {
            case Field.TYPE_XML:
                Document doc = node.getXMLValue(field.getName());                
                // only fill the field, if field has a value..
                if(doc!= null) {                    
                    // put the xml inside the field...
                    fieldElement.appendChild(importDocument(fieldElement, doc));
                }
            break;
            case Field.TYPE_BYTE:
                org.mmbase.util.transformers.Base64 transformer = new org.mmbase.util.transformers.Base64();
                fieldElement.appendChild(tree.createTextNode(transformer.transform(node.getByteValue(field.getName()))));
            default:
                fieldElement.appendChild(tree.createTextNode(node.getStringValue(field.getName())));
        }
        nodeElement.appendChild(fieldElement);
        return fieldElement;
    }
    
    private org.w3c.dom.Element importDocument(org.w3c.dom.Element fieldElement, Document toImport) {
        // Element body = document.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "SOAP-ENV:Body");
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
                || fieldName.equals("snumber")
                || fieldName.equals("dnumber")
                || fieldName.equals("rnumber")
                || fieldName.equals("role")
                || guiType.equals("reldefs")) {
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

    private int getFieldPosition(Field field, FieldList list) {
        for(int i=0; i < list.size(); i++) {
            if(list.getField(i).equals(field)) {
                return i;
            }
        }
        return -1;
    }

    private Element createRelationEntry(Relation relation, boolean createSourceEntry) {
        Element fieldElement = tree.createElement("relation");


        // we have to know what the relation type was...
        Cloud cloud = relation.getCloud();
        org.mmbase.bridge.Node reldef = cloud.getNode(relation.getStringValue("rnumber"));


        org.w3c.dom.Attr attr;
        // the role
        attr = tree.createAttribute("role");
        if(createSourceEntry) {
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
        if(createSourceEntry) {
            attr.setValue("" + relation.getDestination().getNumber());
        }
        else {
            attr.setValue("" + relation.getSource().getNumber());
        }
        fieldElement.setAttributeNode(attr);

        // type
        attr = tree.createAttribute("type");
        if(createSourceEntry) {
            attr.setValue("source");
        }
        else {
            attr.setValue("destination");
        }
        fieldElement.setAttributeNode(attr);
        return fieldElement;
    }
}
