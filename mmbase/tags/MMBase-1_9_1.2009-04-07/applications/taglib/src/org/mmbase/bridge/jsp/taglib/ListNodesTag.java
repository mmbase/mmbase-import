/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.jsp.taglib.containers.ListNodesContainerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;

/**
 * ListNodesTag, provides functionality for listing single nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id: ListNodesTag.java,v 1.32 2008-07-24 08:08:07 michiel Exp $
 */

public class ListNodesTag extends AbstractNodeListTag {

    protected Attribute type = Attribute.NULL;
    protected Attribute container = Attribute.NULL;

    protected Attribute path       = Attribute.NULL;
    protected Attribute element    = Attribute.NULL;
    protected Attribute searchDirs = Attribute.NULL;
    protected Attribute nodes      = Attribute.NULL;

    protected Attribute distinct   = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    /**
     * @param t The name of a node manager
     */
    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }


    /**
     * @since MMBase-1.7.1
     */
    public void setPath(String p) throws JspTagException {
        path = getAttribute(p, true);
    }
    /**
     * @since MMBase-1.7.1
     */
    public void setElement(String e) throws JspTagException {
        element = getAttribute(e, true);
    }
    /**
     * @since MMBase-1.7.1
     */
    public void setSearchdirs(String s) throws JspTagException {
        searchDirs = getAttribute(s, true);
    }
    /**
     * @since MMBase-1.7.1
     */
    public void setNodes(String n) throws JspTagException {
       nodes = getAttribute(n);
    }

    /**
     * @since MMBase-1.8
     */
    public void setDistinct(String d) throws JspTagException {
        distinct = getAttribute(d);
    }



    /**
     * @since MMBase-1.7
     */
    protected NodeQuery getQuery() throws JspTagException {
        ListNodesContainerTag c = findParentTag(ListNodesContainerTag.class, (String) container.getValue(this), false);

        NodeQuery query;
        if (c == null || type != Attribute.NULL || path != Attribute.NULL) {
            if (type == Attribute.NULL && path == Attribute.NULL) {
                throw new JspTagException("Attribute 'type' or 'path' must be provided in listnodes tag (unless referid is given, or used in listnodescontainer)");
            }
            if (type != Attribute.NULL) {
                if (path != Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on listnodes");
                NodeManager nodeManager = getCloudVar().getNodeManager(type.getString(this));
                query = nodeManager.createQuery();
            } else {
                query = getCloudVar().createNodeQuery();
                Queries.addPath(query, (String) path.getValue(this), (String) searchDirs.getValue(this));

                if (element != Attribute.NULL) {
                    String alias = element.getString(this);
                    Step nodeStep = query.getStep(alias);
                    if (nodeStep == null) {
                        throw new JspTagException("Could not set element to '" + alias + "' (no such step)");
                    }
                    query.setNodeStep(nodeStep);
                } else {
                    // default to first step
                    query.setNodeStep(query.getSteps().get(0));
                }
            }
        } else {
            query = (NodeQuery) c.getQuery();
        }
        if (constraints != Attribute.NULL) {
            Queries.addConstraints(query, (String) constraints.getValue(this));
        }
        if (orderby != Attribute.NULL) {
            Queries.addSortOrders(query, (String) orderby.getValue(this), (String) directions.getValue(this));
        }
        if (nodes != Attribute.NULL) {
            Queries.addStartNodes(query, nodes.getString(this));
        }
        if (distinct != Attribute.NULL) {
            query.setDistinct(distinct.getBoolean(this, false));
        }
        return query;
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException {
        int superresult = doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        NodesAndTrim result = getNodesAndTrim(getQuery());
        return setReturnValues(result.nodeList, result.needsTrim);

    }

}
