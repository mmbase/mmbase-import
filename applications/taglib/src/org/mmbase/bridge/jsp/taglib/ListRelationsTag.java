/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationList;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ListRelationsTag, a tag around bridge.Node.getRelations.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ListRelationsTag.java,v 1.6 2003-06-06 10:03:08 pierre Exp $ 
 */

public class ListRelationsTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListRelationsTag.class.getName());

    private Attribute type = Attribute.NULL;
    private Attribute role = Attribute.NULL;

    
    Node getRelatedfromNode() {
        return returnList == null ? null : (Node) returnList.getProperty("relatedFromNode");
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String t) throws JspTagException {
        type  = getAttribute(t);
    }
    /**
     * @param role a role
     */
    public void setRole(String r) throws JspTagException {
        role  = getAttribute(r);
    }

    public int doStartTag() throws JspTagException{
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        // obtain a reference to the node through a parent tag
        Node relatedfromNode = getNode();
        if (relatedfromNode == null) {
            throw new JspTagException("Could not find parent node!!");
        }

        RelationList nodes;
        if (type == Attribute.NULL && role == Attribute.NULL) {
            nodes = relatedfromNode.getRelations();
        } else if (type == Attribute.NULL) {
            nodes = relatedfromNode.getRelations(role.getString(this));
        } else {
            nodes = relatedfromNode.getRelations(role.getString(this), type.getString(this));
        }
        nodes.setProperty("relatedFromNode", relatedfromNode);
        return setReturnValues(nodes, true);
    }

}

