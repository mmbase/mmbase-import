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

    private Node   relatedfromNode = null;
    
    Node getRelatedfromNode() {
        return relatedfromNode;
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String t) throws JspTagException {
        type  = getAttributeValue(t);
    }
    /**
     * @param role a role
     */
    public void setRole(String r) throws JspTagException {
        role  = getAttributeValue(r);
    }

    public int doStartTag() throws JspTagException{
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            relatedfromNode =  (Node) getObject(getReferid() + ":relatedfromNode");
            // hmm, this might be a hack...
            return superresult;
        }
        // obtain a reference to the node through a parent tag
        relatedfromNode = getNode();
        if (relatedfromNode == null) {
            throw new JspTagException("Could not find parent node!!");
        }
        if (getId() != null) {
            getContextTag().register(getId() + ":relatedfromNode", relatedfromNode);
        }

        RelationList nodes;
        if (type == null && role == null) {
            nodes = relatedfromNode.getRelations();
        } else if (type == null) {
            nodes = relatedfromNode.getRelations(role);
        } else {
            nodes = relatedfromNode.getRelations(role, type);
        }
        return setReturnValues(nodes, true);
    }

}

