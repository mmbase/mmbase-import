/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * RelatedNodesTag, provides functionality for listing single related nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @author Jaco de Groot
 */
public class RelatedNodesTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListNodesTag.class.getName());
    protected String typeString = null;

    /**
     * @param type a nodeManager
     */
    public void setType(String type) throws JspTagException {
        typeString = getAttributeValue(type);
    }
    
    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException {
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        // obtain a reference to the node through a parent tag
        Node node = getNode();
        if (node == null) {
            throw new JspTagException("Could not find parent node!!");
        }
        
        NodeList nodes;
        if ( (whereString != null && !whereString.equals(""))
             || 
             (sortedString != null && !sortedString.equals(""))
             ) {

            if (typeString == null) {
                throw new JspTagException("Contraints attribute can only be given in combination with type attribute");
            } 
            NodeManager manager = getCloud().getNodeManager(typeString);
            NodeList    initialnodes = node.getRelatedNodes(typeString);
            
            String where = null;
            for (NodeIterator i = initialnodes.nodeIterator(); i.hasNext(); ) {
                Node n = i.nextNode();
                if (where == null) {
                    where = "" + n.getNumber();
                } else {
                    where += "," + n.getNumber();
                }
            }
            if (where == null) { // empty list, so use that one.
                nodes = initialnodes;
            } else {
                where = "number in (" + where + ")";
                if (whereString!=null) where= "(" + whereString + ") AND " + where;
                nodes = manager.getList(where, sortedString, directionString);
            }
        } else {
            if (typeString==null) {
                nodes = node.getRelatedNodes();
            } else {
                nodes = node.getRelatedNodes(typeString);
            }
        }
        return setReturnValues(nodes, true);
    }

}

