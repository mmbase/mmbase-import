/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationList;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ListRelationsTag, a tag around bridge.Node.getRelations.
 *
 * @author Michiel Meeuwissen
 */

public class ListRelationsTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListRelationsTag.class.getName());

    private String type = null;
    private String role = null;

    private Node   relatedtoNode = null;
    
    Node getRelatedtoNode() {
        return relatedtoNode;
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String t) throws JspTagException {
        type  = getAttributeValue(t);
    }
    /**
     * @param role a nodeManager
     */
    public void setRole(String r) throws JspTagException {
        role  = getAttributeValue(r);
    }

    public int doStartTag() throws JspTagException{
        int superresult =  super.doStartTag(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        // obtain a reference to the node through a parent tag
        relatedtoNode = getNode();
        if (relatedtoNode == null) {
            throw new JspTagException("Could not find parent node!!");
        }
        RelationList nodes;
        if (type == null && role == null) {
            nodes = relatedtoNode.getRelations();
        } else if (type == null) {
            nodes = relatedtoNode.getRelations(role);
        } else {
            nodes = relatedtoNode.getRelations(role, type);
        }
        return setReturnValues(nodes, true);
    }

}

