/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util.xml;

import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.*;

/**
 * Utilities related to the 'mmxf' rich field format of MMBase and bridge.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Mmxf.java,v 1.1 2005-06-06 22:45:09 michiel Exp $
 * @see    org.mmbase.util.transformers.XmlField
 * @since  MMBase-1.8
 */
public class Mmxf {

    private static final Logger log = Logging.getLoggerInstance(Mmxf.class);

    public final static String NAMESPACE = "http://www.mmbase.org/mmxf";
    public final static String DOCUMENTTYPE_PUBLIC =  "-//MMBase//DTD mmxf 1.1//EN";
    public final static String DOCUMENTTYPE_SYSTEM = "http://www.mmbase.org/dtd/mmxf_1_1.dtd";


    /**
     * Defaulting version of {@link #createTree(org.w3c.dom.Node, Relationmanager, int, String, String, StringBuffer}.
     */
    public static org.mmbase.bridge.Node createTree(org.w3c.dom.Node node, RelationManager relationManager, int depth, StringBuffer buf) {
        return createTree(node, relationManager, depth, "title", "body", buf);
    }
    /**
     * Creates a a tree of Nodes from an mmxf DOM-Node. The (mmxf) document can of course be stored
     * in one MMXF field, but if it is large, you want to split it up in a tree of nodes. This can
     * be necessary in an import, where you would e.g. receive a complete book (or chapter) in one
     * XML document. You could e.g. (XSL) transform it to one huge MMXF and then feed it into this function. See also 
     * {@link org.mmbase.util.transformers.XmlField} for creating MMXF from ASCII.
     *
     * If your MMXF is a Document, you must feed {@link org.w3c.dom.Document#getDocumentElement()}
     * 
     * @param node Source DOM-Node (it's node-name must be 'mmxf' or 'section').
     * @param relationManager Describes the object model in which it must be dispatched. Source and
     *        Destinaion must be of the same type, the relation must have a field 'pos'.
     * @param depth How far to dispatch. If -1 then it will be split up until no sections are
     *        remaining.
     * @param titleField The h-tag of the sections are written to this field.
     * @param xmlField   A new mmxf-document is created for the test and written to this field.
     * @param feedBack   A string buffer for feedback (can be e.g. used for logging or presenting on import-jsp).
     */
    public static org.mmbase.bridge.Node createTree(org.w3c.dom.Node node, RelationManager relationManager, int depth, String titleField, String xmlField, StringBuffer feedBack) {
        String nodeName = node.getNodeName();
        if (! (nodeName.equals("section") || nodeName.equals("mmxf"))) {
            throw new IllegalArgumentException("dom-Node must be a 'section' or 'mmxf' (but is a " + node.getNodeName() + ")");
        }
        NodeManager nm = relationManager.getDestinationManager();
        org.w3c.dom.NodeList childs = node.getChildNodes();
        int i = 0;

        String title;
        if (nodeName.equals("section")) {
            if (childs.getLength() < 1) throw new IllegalArgumentException("No child nodes! (should at least be a h-child)");
            org.w3c.dom.Node h = childs.item(i);
            if (! h.getNodeName().equals("h")) {
                throw new IllegalArgumentException("No h-tag");
            }
            title = h.getNodeValue();
            i++;
        } else {
            title = "Imported MMXF";
        }
        Document mmxf = createMmxfDocument();
        while (i < childs.getLength()) {
            org.w3c.dom.Node next = childs.item(i);
            String name = next.getNodeName();
            if (name.equals("p") || name.equals("table") ||
                (depth != 0 && name.equals("section"))) {
                org.w3c.dom.Node n = mmxf.importNode(next, true);
                mmxf.getDocumentElement().appendChild(n);
            } else {
                break;
            }
            i++;
        }
        // create the node.
        org.mmbase.bridge.Node newNode = nm.createNode();
        newNode.setStringValue(titleField, title);
        newNode.setXMLValue(xmlField, mmxf);
        newNode.commit();

        int pos = 0;
        while (i < childs.getLength()) {
            org.w3c.dom.Node next = childs.item(i);
            String name = next.getNodeName();
            if (name.equals("section")) {
                org.mmbase.bridge.Node  destination = createTree(node, relationManager, depth > 0 ? depth -1 : depth, titleField, xmlField, feedBack); 
                Relation relation = relationManager.createRelation(newNode, destination);
                relation.setIntValue("pos", pos++);                
                relation.commit();
            } else {
                throw new IllegalArgumentException("Not a section, but a " + name);
            }
            i++;

        }
        return newNode;
            
    }

    protected static Document createMmxfDocument() {
        DocumentBuilder documentBuilder = org.mmbase.util.xml.DocumentReader.getDocumentBuilder();
        DOMImplementation impl = documentBuilder.getDOMImplementation();
        Document document = impl.createDocument(NAMESPACE, 
                                       "mmxf", 
                                       impl.createDocumentType("mmxf", DOCUMENTTYPE_PUBLIC, DOCUMENTTYPE_SYSTEM)
                                       );
        document.getDocumentElement().setAttribute("version", "1.1");
        return document;
    }
}
