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
    private static Logger log = Logging.getLoggerInstance(RelatedNodesTag.class.getName());
    protected String type = null;
    protected String role = null;

    /**
     * @param type a nodeManager
     */
    public void setType(String type) throws JspTagException {
        this.type = getAttributeValue(type);
    }
    /**
     * @param role a role
     */
    public void setRole(String role) throws JspTagException {
        this.role = getAttributeValue(role);
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
        if ( (constraints != null && !constraints.equals(""))
             || 
             (orderby != null && !orderby.equals(""))
             ) { // given orderby or constraints, start hacking:

            if (type == null) {
                throw new JspTagException("Contraints attribute can only be given in combination with type attribute");
            } 
            NodeManager manager = getCloud().getNodeManager(type);
            NodeList initialnodes;

            if (role == null) {
                initialnodes = node.getRelatedNodes(type);
            } else {
                initialnodes = node.getRelatedNodes(type, role, directions);
            }
            
            StringBuffer where = null;
            for (NodeIterator i = initialnodes.nodeIterator(); i.hasNext(); ) {
                Node n = i.nextNode();
                if (where == null) {
                    where = new StringBuffer( n.getNumber());
                } else {
                    where.append(",").append( n.getNumber());
                }
            }
            if (where == null) { // empty list, so use that one.
                nodes = initialnodes;
            } else {
                where = new StringBuffer("number in (" + where + ")");
                if (constraints != null) where.insert(0, "(" + constraints + ") AND ");
                nodes = manager.getList(where.toString(), orderby, directions);
            }
        } else {
            if (type == null) {
                if (role != null) {
                    throw new JspTagException("Must specify type attribute when using 'role'");
                }
                nodes = node.getRelatedNodes();
            } else {
                if (role == null) {
                    nodes = node.getRelatedNodes(type);
                } else {
                    nodes = node.getRelatedNodes(type, role, directions);
                }
            }
        }
        return setReturnValues(nodes, true);
    }

}

