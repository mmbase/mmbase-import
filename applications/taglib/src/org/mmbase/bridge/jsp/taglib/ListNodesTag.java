/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ListNodesTag, provides functionality for listing single nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id: ListNodesTag.java,v 1.7 2003-06-06 10:03:08 pierre Exp $ 
 */

public class ListNodesTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListNodesTag.class.getName());

    protected Attribute type = Attribute.NULL;

    /**
     * @param type a nodeManager
     */
    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException{
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        if (type == Attribute.NULL) {
            throw new JspTagException("Attribute 'type' must be provided in listnodes tag (unless referid is given)");
        }

        NodeManager manager=getCloud().getNodeManager(type.getString(this));
        NodeList nodes = manager.getList(constraints.getString(this), orderby.getString(this), directions.getString(this));
        return setReturnValues(nodes, true);
    }

}

