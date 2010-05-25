/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.containers.RelatedNodesContainerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;

/**
 * RelatedNodesTag, provides functionality for listing single related nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id$
 */
public class RelatedNodesTag extends AbstractNodeListTag {

    protected Attribute type = Attribute.NULL;
    protected Attribute path = Attribute.NULL;
    protected Attribute element = Attribute.NULL;
    protected Attribute role = Attribute.NULL;
    protected Attribute searchDir = Attribute.NULL;  // for use with 'role' and 'type'
    protected Attribute searchDirs = Attribute.NULL; // for use with 'path' and 'element'

    protected Attribute container = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String type) throws JspTagException {
        this.type = getAttribute(type, true);
    }
    /**
     * @param role a role
     */
    public void setRole(String role) throws JspTagException {
        this.role = getAttribute(role, true);
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
     * Performs the search
     */
    public int doStartTag() throws JspTagException {
        int superresult = doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        RelatedNodesContainerTag c = findParentTag(RelatedNodesContainerTag.class, (String) container.getValue(this), false);

        NodeQuery query;
        if (type != Attribute.NULL || path != Attribute.NULL || c == null || parentNodeId != Attribute.NULL) {

            // obtain a reference to the node through a parent tag
            Node parentNode = getNode();
            if (parentNode == null) {
                throw new TaglibException("Could not find parent node!!");
            }
            Cloud cloud;
            {
                // prefer cloud of current page, otherwise of node
                CloudProvider cloudProvider = findCloudProvider(false);
                cloud = cloudProvider != null ? cloudProvider.getCloudVar() : parentNode.getCloud();
            }
            query = cloud.createNodeQuery();
            Step step1 = query.addStep(parentNode.getNodeManager());
            query.setAlias(step1, parentNode.getNodeManager().getName() + "0");
            query.addNode(step1, parentNode);


            String searchDirections;
            if (searchDir == Attribute.NULL) { // searchdir is a bit deprecated.
                searchDirections = (String) searchDirs.getValue(this);
            } else {
                if (searchDirs != Attribute.NULL) {
                    throw new TaglibException("Cannot specify both 'searchdir' and 'searchdirs' attributes. ");
                }
                searchDirections = (String) searchDir.getValue(this);
            }

            NodeManager otherManager;
            if (path == Attribute.NULL) {
                if (type == Attribute.NULL) {
                    otherManager = cloud.getNodeManager("object");
                } else {
                    if (element != Attribute.NULL) {
                        throw new TaglibException("Cannot specify both 'element' and 'type' attributes");
                    }
                    otherManager = cloud.getNodeManager(type.getString(this));
                }
                RelationStep step2 = query.addRelationStep(otherManager, (String) role.getValue(this), searchDirections);
                Step step3 = step2.getNext();
                query.setNodeStep(step3); // makes it ready for use as NodeQuery
            } else {
                if (role != Attribute.NULL) {
                    throw new TaglibException("Cannot specify both 'path' and 'role' attributes");
                }
                Queries.addPath(query, (String) path.getValue(this), searchDirections);
                if (element != Attribute.NULL) {
                    String alias = element.getString(this);
                    Step nodeStep = query.getStep(alias);
                    if (nodeStep == null) {
                        throw new JspTagException("Could not set element to '" + alias + "' (no such step)");
                    }
                    query.setNodeStep(nodeStep);
                } else {
                    // default to third step (first step is the related node, second is the relation)
                    query.setNodeStep(query.getSteps().get(2));
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

        NodesAndTrim result = getNodesAndTrim(query);
        return setReturnValues(result.nodeList, result.needsTrim);
    }
}
