/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ListRelationsTag, a tag around bridge.Node.getRelations.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ListRelationsTag.java,v 1.9 2004-01-14 22:06:15 michiel Exp $ 
 */

public class ListRelationsTag extends AbstractNodeListTag {
    private static final Logger log = Logging.getLoggerInstance(ListRelationsTag.class);

    private Attribute type = Attribute.NULL;
    private Attribute role = Attribute.NULL;
    private Attribute searchDir = Attribute.NULL;

    
    Node getRelatedfromNode() {
        NodeList returnList = getReturnList();
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

    public void setSearchdir(String s) throws JspTagException {
        searchDir = getAttribute(s);
    }

    public int doStartTag() throws JspTagException{
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        // obtain a reference to the node through a parent tag
        Node relatedFromNode = getNode();
        if (relatedFromNode == null) {
            throw new JspTagException("Could not find parent node!!");
        }

        NodeManager nm = null;
        if (type != Attribute.NULL) {
            nm = getCloud().getNodeManager(type.getString(this));
        }
        RelationList nodes = relatedFromNode.getRelations((String) role.getValue(this), nm, (String) searchDir.getValue(this));
        nodes.setProperty("relatedFromNode", relatedFromNode);
        return setReturnValues(nodes, true);
    }

}

