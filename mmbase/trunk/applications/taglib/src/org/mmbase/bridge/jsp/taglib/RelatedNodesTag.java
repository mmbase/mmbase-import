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
import org.mmbase.bridge.jsp.taglib.containers.RelatedNodesContainerTag;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * RelatedNodesTag, provides functionality for listing single related nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id: RelatedNodesTag.java,v 1.28 2003-11-06 09:07:25 pierre Exp $
 */

public class RelatedNodesTag extends AbstractNodeListTag {
    private static final Logger log = Logging.getLoggerInstance(RelatedNodesTag.class);
    protected Attribute type      = Attribute.NULL;
    protected Attribute path      = Attribute.NULL;
    protected Attribute role      = Attribute.NULL;
    protected Attribute searchDir = Attribute.NULL;

    protected Attribute container = Attribute.NULL;


    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String type) throws JspTagException {
        this.type = getAttribute(type);
    }
    /**
     * @param role a role
     */
    public void setRole(String role) throws JspTagException {
        this.role = getAttribute(role);
    }

    /**
     * The search parameter, determines how directionality affects the search.
     * Possible values are <code>both</code>, <code>destination</code>,
     * <code>source</code>, and <code>all</code>
     * @param search the swerach value
     */
    public void setSearchdir(String search) throws JspTagException {
        searchDir = getAttribute(search);
    }


    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException {
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        RelatedNodesContainerTag c = (RelatedNodesContainerTag) findParentTag(RelatedNodesContainerTag.class, (String) container.getValue(this), false);

        NodeQuery query;
        Cloud cloud = getCloud();
        if (type != Attribute.NULL || c == null || parentNodeId != Attribute.NULL) {

            // obtain a reference to the node through a parent tag
            Node parentNode = getNode();
            if (parentNode == null) {
                throw new JspTagException("Could not find parent node!!");
            }

            query = cloud.createNodeQuery();
            Step step1 = query.addStep(parentNode.getNodeManager());
            query.addNode(step1, parentNode);

            NodeManager otherManager;
            if (type == Attribute.NULL) {
                otherManager = cloud.getNodeManager("object");
            } else {
                otherManager = cloud.getNodeManager(type.getString(this));
            }

            RelationStep step2 = query.addRelationStep(otherManager, (String) role.getValue(this), (String) searchDir.getValue(this));
            Step step3 = step2.getNext();

            query.setNodeStep(step3);  // makes it ready for use as NodeQuery

            if (constraints != Attribute.NULL) {
                Queries.addConstraints(query, (String) constraints.getValue(this));
            }
            if (orderby != Attribute.NULL) {
                Queries.addSortOrders(query, (String) orderby.getValue(this), (String) directions.getValue(this));
            }
        } else {
            query = (NodeQuery) c.getQuery();
            if (constraints != Attribute.NULL) {
                Queries.addConstraints(query, (String) constraints.getValue(this));
            }
            if (orderby != Attribute.NULL) {
                Queries.addSortOrders(query, (String) orderby.getValue(this), (String) directions.getValue(this));
            }
        }

        NodeList nodes = cloud.getList(query);
        return setReturnValues(nodes, true, query);
    }
}

