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
 * @version $Id: Generator.java,v 1.1 2002-03-28 15:49:22 michiel Exp $
 */
public  class Generator {

    private static Logger log = Logging.getLoggerInstance(Generator.class.getName());
    private Document tree;

    /**
     * To use the functionality of this class, instantiate it with
     * with a DOM Document in which the Elements must be put.
     */
    public Generator(Document tree) {
        if(tree == null) {
            throw new BridgeException("Tree was null");
        }        
        this.tree = tree;
    }

    /**
     * Returns the working DOM document.
     */
    public Document getDocument() {
        return tree;
    }

    /**
     * Adds one Node to a DOM Document.
     *
     * @param An MMBase bridge Node.
     */
    public Element addNode(Node node) {
        return addNode(node, node.getNodeManager().getFields(), false, true);
        
    }

    /**
     * Adds a field to the DOM Document. This means that there will
     * also be added a Node if this is necessary.
     *
     * @param An MMbase bridge Node.
     * @param An MMBase bridge Field.
     */
    public Element addField(Node node, Field field) {
        return addNode(node, field, false, false);
    }
    
    /**
     * This is a helper function that Executes an XPATH query on the give DOM Node.
     * Its only use is to do some error handling.
     */

    protected static Element getXMLElement(org.w3c.dom.Node node, String xpath) {
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

    /**
     * Executes a xpath query on the DOM document of this instance.
     *
     * @param A XPath query (as a string)
     * @return An DOM Element or null (if nothing found) 
     */
    
    protected Element getXMLElement(String xpath) {
        return getXMLElement(tree, xpath);
    }

    /**
     * Adds a whole MMBase bridge NodeList to the DOM Document.
     *
     * @param An MMBase bridge NodeList.
     */
    public void addNodeList(NodeList nodes) {
        log.debug("Adding a node list");
        if (nodes instanceof RelationList) {
            log.debug("This is a relation list");
            RelationIterator i = ((RelationList) nodes).relationIterator();
            while(i.hasNext()) {
                Relation n = i.nextRelation();
                addRelation(n);            
            }
        } else {
            log.debug("This is a node list");
            // for all nodes, output the xml...
            NodeIterator i = nodes.nodeIterator();
            while(i.hasNext()) {
                Node n = i.nextNode();
                addNode(n);
            }            
        }

        // return tree.getDocumentElement();
    }


    /**
     * Create the node if it is not in tree already. Add the given
     * fields, unless 'addFieldsIfExist' is false.
     *
     * allFields should be true if you are adding all fields.
     * 
     *
     */
    private Element addNode(Node node, Object fields, boolean addFieldsIfExist, boolean allFields) {
        
        // first look if we have the <objects start="/objects/object[%number%]" />
        // when not, this node is the start object....
        Element root = getXMLElement("/objects");
        
        if(root== null) {
            // No root element was available yet in the toXML function.
            // This means that the first node was inserted.....
            
            // Create the root element.
            root = tree.createElement("objects");
            org.w3c.dom.Attr attr = tree.createAttribute("root");
            attr.setValue("" + node.getNumber());
            root.setAttributeNode(attr);
            
            // get the complete node..
            org.w3c.dom.Element object = createNodeToXML(node, fields, allFields);
            root.appendChild(object);
            
            // put it in the document
            tree.appendChild(root);
            return object;
        } else { // root already  exists
            // look if this  node is already available in the tree.
            Element object = getXMLElement("/objects/object[@id='" + node.getNumber() + "']");

            if(object == null) {  // It doesn't exist, insert it into the tree...
                object = createNodeToXML(node, fields, allFields);
                root.appendChild(object);
            } else {
                if (addFieldsIfExist) { // it does exist, add the new fields, if this was explicity requested
                    nodeToXML(object, node, fields);
                }
            }
            return object;
        }
    }



    public Element addRelation(Relation relation) {
        // a relation exists from 3 things(for now..)

        // check if we have to do all this crap, this means, if the relation object already there.. no-need to do it???       
        Element relationObject = getXMLElement("/objects/object[@id='" + relation.getNumber() + "']");
        if(relationObject != null) {
            // TODO : WHEN A RELATION ON A RELATION, this can cause errors !!!
            // so check in dest // source if the relation tag is also inserted..
            return relationObject;
        }
        relationObject = addNode(relation);
        Element sourceObject      = addNode(relation.getSource());
        Element destinationObject = addNode(relation.getDestination());

        // <relation role="%role%" object="/objects/object[%relation%]" related="/objects/object[%destinationnumber%]"/>

        Cloud cloud = relation.getCloud();

        // add the relation header to the source and the destination node, if not already there...
        Node reldef = cloud.getNode(relation.getStringValue("rnumber"));

        // create the node's to be inserted..
        Element sourceRelation      = tree.createElement("relation");
        Element destinationRelation = tree.createElement("relation");

        // sourceRole
        String sourceRole = reldef.getStringValue("sname");
        org.w3c.dom.Attr attr = tree.createAttribute("role");
        attr.setValue(sourceRole);
        sourceRelation.setAttributeNode(attr);

        // destRole
        String destinationRole = reldef.getStringValue("dname");
        attr = tree.createAttribute("role");
        attr.setValue(destinationRole);
        destinationRelation.setAttributeNode(attr);

        // related
        String destinationPath = "" + relation.getDestination().getNumber();
        attr = tree.createAttribute("related");
        attr.setValue(destinationPath);
        sourceRelation.setAttributeNode(attr);

        // related
        String sourcePath = "" + relation.getSource().getNumber();
        attr = tree.createAttribute("related");
        attr.setValue(sourcePath);
        destinationRelation.setAttributeNode(attr);

        // me, me, me
        String objectPath = "" + relation.getNumber();
        attr = tree.createAttribute("object");
        attr.setValue(objectPath);
        sourceRelation.setAttributeNode(attr);

        attr = tree.createAttribute("object");
        attr.setValue(objectPath);
        destinationRelation.setAttributeNode(attr);

        sourceObject.appendChild(sourceRelation);
        destinationObject.appendChild(destinationRelation);
        return relationObject;
    }

    /**
     * Creates a node as a DOM Element which can be inserted into tree.
     *
     * @param   tree A DOM Document in which the object should be created.
     * @return       The node as a DOM Element.
     *
     **/

    protected Element createNodeToXML(Node node, Object fields, boolean allFields) {
        org.w3c.dom.Element object = tree.createElement("object");
        // the id...
        org.w3c.dom.Attr attr = tree.createAttribute("id");
        attr.setValue("" + node.getNumber());
        object.setAttributeNode(attr);

        // the type...
        attr = tree.createAttribute("type");
        attr.setValue(node.getNodeManager().getName());
        object.setAttributeNode(attr);

        // the type...
        attr = tree.createAttribute("complete");
        attr.setValue(allFields ? "true" : "false");
        object.setAttributeNode(attr);

        return nodeToXML(object, node, fields);

    }

    /**
     * Add new fields to the object, (unless the field already exist).
     *
     * You can feed it with a FieldList or with a Field.
     */
    
    protected Element nodeToXML(Element object, Node node, Object fields) {
        if (fields != null) {
            if (fields instanceof FieldList) {
                // we now insert all the fields with their info..
                FieldIterator i = ((FieldList) fields).fieldIterator();
                while(i.hasNext()) {
                    Field field = i.nextField();
                    if (log.isDebugEnabled()) log.debug("getting field " + field.getName());
                    if (getXMLElement(object, "field[@name='" + field.getName() + "']") == null) {
                        object.appendChild(node.getXMLValue(field.getName(), object.getOwnerDocument()));
                    }
                }
            } else {
                String fieldName = ((Field) fields).getName();
                log.debug("getting field " + fieldName);
                if (getXMLElement(object, "field[@name='" + fieldName + "']") == null) {
                    object.appendChild(node.getXMLValue(fieldName, object.getOwnerDocument()));
                }

            }
        }
        return object;
    }

    
    /**
     * Returns the document as a String.
     */

    public String toString() {
        try {
            org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(tree);
            java.io.StringWriter result = new java.io.StringWriter();
            org.apache.xml.serialize.XMLSerializer prettyXML = new org.apache.xml.serialize.XMLSerializer(result, format);
            prettyXML.serialize(tree);
            return result.toString();    
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * For debugging purposes. Return the constructed document as a String.
     */

    public String toStringFormatted() {
        try {
            org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(tree);
            format.setIndenting(true);
            format.setPreserveSpace(false);
            //  format.setOmitXMLDeclaration(true);
            //  format.setOmitDocumentType(true);
            java.io.StringWriter result = new java.io.StringWriter();
            org.apache.xml.serialize.XMLSerializer prettyXML = new org.apache.xml.serialize.XMLSerializer(result, format);
            prettyXML.serialize(tree);
            return result.toString();    
        } catch (Exception e) {
            return e.toString();
        }
    }


}
