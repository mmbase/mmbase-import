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
    protected String searchDir = null;

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
     */
    public void setSearchdir(String search) throws JspTagException {
        searchDir = getAttributeValue(search);
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
        Node parentNode = getNode();
        if (parentNode == null) {
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

            if (role == null && searchDir == null) {
                initialnodes = parentNode.getRelatedNodes(type);
            } else {
                if (searchDir == null && directions != null) {
                    log.error("WRONG use of 'directions' attribute of relatednodes (should be searchdir). Fix this page before 1.7!");
                    initialnodes = parentNode.getRelatedNodes(type, role, directions);
                } else {
                    initialnodes = parentNode.getRelatedNodes(type, role, searchDir);
                }
            }

            StringBuffer where = null;
            for (NodeIterator i = initialnodes.nodeIterator(); i.hasNext(); ) {
                Node n = i.nextNode();
                if (where == null) {
                    where = new StringBuffer("" +  n.getNumber());
                } else {
                    where.append(",").append( n.getNumber());
                }
            }
            if (where == null) { // empty list, so use that one.
                nodes = initialnodes;
            } else {
                where.insert(0, "[number] in (").append(")");
                if (constraints != null) where.insert(0, "(" + constraints + ") AND ");
                nodes = manager.getList(where.toString(), orderby, directions);
            }
        } else {
            if (type == null) {
                if (role != null) {
                    throw new JspTagException("Must specify type attribute when using 'role'");
                }
                nodes = parentNode.getRelatedNodes();
            } else {
                if (role == null && searchDir == null) {
                    nodes = parentNode.getRelatedNodes(type);
                } else {
                    if (searchDir == null && directions != null) {
                        log.error("WRONG use of 'directions' attribute of relatednodes (should be searchdir). Fix this page before 1.7");
                        nodes = parentNode.getRelatedNodes(type, role, directions);
                    } else {
                        nodes = parentNode.getRelatedNodes(type, role, searchDir);
                    }
                }
            }
        }
        return setReturnValues(nodes, true);
    }

}

