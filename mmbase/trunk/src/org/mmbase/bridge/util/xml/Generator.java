/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * Uses the XML functions from the bridge the construct DOM elements
 * representing MMBase data structures.
 *
 * @author Michiel Meeuwissen
 * @author Eduard Witteveen
 * @version $Id: Generator.java,v 1.5 2002-04-08 12:10:13 eduard Exp $
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
     *
     * @param An MMbase bridge Node.
     * @param An MMBase bridge Field.
     */
    public Element addField(Node node, Field field) {
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
     */
    public Element addNode(Node node) {
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
     */    
    public Element addRelation(Relation relation) {
        return addNode(relation);
    }

    /**
     * Adds a whole MMBase bridge NodeList to the DOM Document.
     * @param An MMBase bridge NodeList.
     */
    public void addNodeList(NodeList nodes) {
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
     */
    public Document getDocument() {
        return tree;
    }

    /**
     * Returns the document as a String.
     */
    public String toString() {
        return toString(false);
    }

    /**
     * Returns the document as a String.
     *@param ident if the string has to be idented
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
    
    private Element getNodeElement(Element rootElement, Node node) {
        return getXMLElement(rootElement, "object[@id='" + node.getNumber() + "']");
    }

    private Element getFieldElement(Element nodeElement, Field field) {
        return getXMLElement(nodeElement, "field[@name='" + field.getName() + "']");
    }
    
    private Element addRootElement(Node node) {
        Element rootElement = tree.createElement("objects");
        org.w3c.dom.Attr attr = tree.createAttribute("root");
        attr.setValue("" + node.getNumber());
        rootElement.setAttributeNode(attr);
        tree.appendChild(rootElement);
        return rootElement;
    }
    
    private Element addNodeElement(Element rootElement, Node node) {
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

    private Element addFieldElement(Element nodeElement, Node node, Field field) {    
        Element fieldElement = tree.createElement("field");

        org.w3c.dom.Attr attr;
        // the name...
        attr = tree.createAttribute("name");
        attr.setValue(field.getName());
        fieldElement.setAttributeNode(attr);

        // gui-type
        attr = tree.createAttribute("gui-type");
        attr.setValue(field.getGUIType());
        fieldElement.setAttributeNode(attr);

        // gui-name
        attr = tree.createAttribute("gui-name");
        attr.setValue(field.getGUIName());
        fieldElement.setAttributeNode(attr);
     

        // type
        attr = tree.createAttribute("type");
        attr.setValue("" + field.getType());
        fieldElement.setAttributeNode(attr);

        // state
        attr = tree.createAttribute("state");
        attr.setValue("" + field.getState());
        fieldElement.setAttributeNode(attr);

        if(field.getType() == Field.TYPE_XML) {
            Document doc = node.getXMLValue(field.getName());
            if(doc!= null) {
                fieldElement.appendChild(tree.importNode(doc.getDocumentElement(), true));
            }
            
        }
        else {
            fieldElement.appendChild(tree.createTextNode(node.getStringValue(field.getName())));
        }    
        nodeElement.appendChild(fieldElement);
        return fieldElement;
    }
    
    private Element createRelationEntry(Relation relation, boolean createSourceEntry) {
        Element fieldElement = tree.createElement("relation");
        

        // we have to know what the relation type was...        
        Cloud cloud = relation.getCloud();
        Node reldef = cloud.getNode(relation.getStringValue("rnumber"));
        
        
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
