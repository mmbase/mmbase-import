/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.containers.ListNodesContainerTag;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ListNodesTag, provides functionality for listing single nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id: ListNodesTag.java,v 1.8 2003-07-25 18:16:33 michiel Exp $ 
 */

public class ListNodesTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListNodesTag.class);

    protected Attribute type      = Attribute.NULL;
    protected Attribute container = Attribute.NULL;

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
        ListNodesContainerTag c = (ListNodesContainerTag) findParentTag(ListNodesContainerTag.class, (String) container.getValue(this), false);

        if (c == null) {
            if (type == Attribute.NULL) {
                throw new JspTagException("Attribute 'type' must be provided in listnodes tag (unless referid is given)");
            }
            
            NodeManager manager = getCloud().getNodeManager(type.getString(this));
            NodeList nodes = manager.getList(constraints.getString(this), orderby.getString(this), directions.getString(this));
            return setReturnValues(nodes, true);
        } else {
            NodeQuery query = (NodeQuery) c.getQuery();
            NodeList nodes = query.getNodeManager().getList(query);
            return setReturnValues(nodes, true);
        }
    }

}

