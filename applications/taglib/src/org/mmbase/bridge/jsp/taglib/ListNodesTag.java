/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.containers.ListNodesContainerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import java.util.List;
import org.mmbase.util.logging.*;

/**
 * ListNodesTag, provides functionality for listing single nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id: ListNodesTag.java,v 1.17 2003-11-06 09:07:24 pierre Exp $
 */

public class ListNodesTag extends AbstractNodeListTag {
    private static final Logger log = Logging.getLoggerInstance(ListNodesTag.class);

    protected Attribute type      = Attribute.NULL;
    protected Attribute container = Attribute.NULL;


    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }


    protected NodeManager nodeManager;

    /**
     * @since MMBase-1.7
     */
    protected NodeManager getNodeManager() {
        return nodeManager;
    }

    /**
     * @since MMBase-1.7
     */
    protected int getNodes() throws JspTagException {
        ListNodesContainerTag c = (ListNodesContainerTag) findParentTag(ListNodesContainerTag.class, (String) container.getValue(this), false);


        if (c == null || type != Attribute.NULL) {
            if (type == Attribute.NULL) {
                throw new JspTagException("Attribute 'type' must be provided in listnodes tag (unless referid is given, or used in listnodescontainer)");
            }
            nodeManager = getCloud().getNodeManager(type.getString(this));
            NodeList nodes = nodeManager.getList(constraints.getString(this), (String) orderby.getValue(this), directions.getString(this));

            return setReturnValues(nodes, true);

        } else {
            NodeQuery query = (NodeQuery) c.getQuery();
            if (constraints != Attribute.NULL) {
                Queries.addConstraints(query, (String) constraints.getValue(this));
            }
            if (orderby != Attribute.NULL) {
                Queries.addSortOrders(query, (String) orderby.getValue(this), (String) directions.getValue(this));
            }
            NodeList nodes = getCloud().getList(query);

            return setReturnValues(nodes, true, query);
        }
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException{
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        return getNodes();
    }

}

