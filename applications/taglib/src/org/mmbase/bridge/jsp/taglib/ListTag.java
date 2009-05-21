/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;

/**
 * ListTag, provides functionality for listing cluster nodes
 * ('multilevel' search) in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id$
 */

public class ListTag extends AbstractNodeListTag implements ClusterNodeProvider {
    private static final Logger log = Logging.getLoggerInstance(ListTag.class);

    protected Attribute nodes = Attribute.NULL;
    protected Attribute path = Attribute.NULL;
    protected Attribute distinct = Attribute.NULL;
    protected Attribute search = Attribute.NULL;
    protected Attribute fields = Attribute.NULL;
    protected Attribute container = Attribute.NULL;

    /**
     * @param fields a comma separated list of fields of the nodes.
     **/
    public void setFields(String fields) throws JspTagException {
        this.fields = getAttribute(fields);
    }

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    /**
     * Sets the nodes to start the search with.
     * The value '-1' interpreted as <code>null</code>, whcih indicates no
     * predefined startnodes are sued (a more general search is conducted
     * instead).
     * @param nodes a node or a comma separated list of nodes.
     */
    public void setNodes(String nodes) throws JspTagException {
        // parse/map the nodes they can be params, sessions or aliases
        // instead of just numbers
        this.nodes = getAttribute(nodes);
    }

    /**
     * @param path a comma separated list of nodeManagers
     */
    public void setPath(String path) throws JspTagException {
        this.path = getAttribute(path);
    }

    /**
     * The search parameter, determines how directionality affects the search.
     * Possible values are <code>both</code>, <code>destination</code>,
     * <code>source</code>, <code>either</code> and <code>all</code>
     * @param search the search value
     */
    public void setSearchdir(String search) throws JspTagException {
        this.search = getAttribute(search);
        if (log.isDebugEnabled())
            log.debug("Setting search dir to " + this.search);
    }

    /**
     * @param distinct the selection query for the object we are looking for
     */
    public void setDistinct(String distinct) throws JspTagException {
        this.distinct = getAttribute(distinct);
    }

    /**
     * To be overrided by related-tag
     */
    protected String getSearchNodes() throws JspTagException {
        if (nodes != Attribute.NULL) {
            return nodes.getString(this);
        } else {
            return "";
        }
    }

    /**
     * To be overrided by related-tag
     */
    protected String getPath() throws JspTagException {
        return path.getString(this);
    }

    protected QueryContainer getListContainer() throws JspTagException {
        return findParentTag(ListContainerTag.class, (String) container.getValue(this), false);
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException {
        int superresult = doStartTagHelper();
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        QueryContainer c = getListContainer();


        Query query;
        if (c == null || path != Attribute.NULL) {
            // old-style, container-less working
            if (path == Attribute.NULL) {
                throw new JspTagException("Path attribute is mandatory if referid not speficied");
            }

            String distinctString = distinct.getString(this).toLowerCase();
            boolean searchDistinct = false;
            if ("true".equals(distinctString) || "yes".equals(distinctString)) {
                searchDistinct = true;
            }


            String searchString = search.getString(this).toUpperCase();
            if (searchString.length() == 0) {
                searchString = "BOTH";
            } else if (
                !searchString.equals("BOTH")
                    && !searchString.equals("EITHER")
                    && !searchString.equals("SOURCE")
                    && !searchString.equals("DESTINATION")
                    && !searchString.equals("ALL")) {
                throw new JspTagException("Search should be one of BOTH, SOURCE, " + "DESTINATION, EITHER, or ALL (value found was " + searchString + ")");
            }

            if (log.isDebugEnabled()) {
                log.debug("pathstring " + path.getString(this));
                log.debug("directions " + directions);
                log.debug("searchString " + searchString);
            }

            query = Queries.createQuery(getCloudVar(),
                                        getSearchNodes(),
                                        getPath(),
                                        fields.getString(this),
                                        (String) constraints.getValue(this),
                                        (String) orderby.getValue(this),
                                        (String) directions.getValue(this),
                                        searchString,
                                        searchDistinct);
        } else {   // container found!
            if (path != Attribute.NULL || search != Attribute.NULL) {
                throw new JspTagException("search and path attributes not supported within a container.");
            }

            query = c.getQuery();
            if (constraints != Attribute.NULL) {
                Queries.addConstraints(query, (String) constraints.getValue(this));
            }
            if (orderby != Attribute.NULL) {
                Queries.addSortOrders(query, (String) orderby.getValue(this), (String) directions.getValue(this));
            }

            if (fields != Attribute.NULL) {
                query.removeFields();
                Queries.addFields(query, fields.getString(this));
            }
            if (distinct != Attribute.NULL) {
                query.setDistinct(distinct.getBoolean(this, false));
            }
            if (nodes != Attribute.NULL) {
                Queries.addStartNodes(query, nodes.getString(this));
            }

        }

        NodesAndTrim result = getNodesAndTrim(query);
        return setReturnValues(result.nodeList, result.needsTrim);

    }

}
