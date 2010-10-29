/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.*;

/**
 * Container cognate for TreeTag
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7.1
 * @version $Id$
 */
public class TreeContainerTag extends RelatedNodesContainerTag implements NodeQueryContainer, ContainerReferrer { // extending from relatednodescontainer only for the attributes

    private static final Logger log = Logging.getLoggerInstance(TreeContainerTag.class);

    protected Attribute maxDepth    = Attribute.NULL;
    protected Attribute container   = Attribute.NULL;
    private String jspVar;

    public void setMaxdepth(String md) throws JspTagException {
        maxDepth = getAttribute(md);
    }
    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setJspvar(String jv) {
        jspVar = jv;
    }

    protected GrowingTreeList tree;

    public GrowingTreeList getTree() {
        return tree;
    }


    public Query getQuery() {
        return getNodeQuery();
    }
    public NodeQuery getNodeQuery() {
        return tree.getTemplate();
    }

    /**
     * Retrieves the starting query from environment.
     * Static because also used by TreeTag itself.
     */
    static NodeQuery  getStartQuery(NodeReferrerTag thisTag, Attribute containerAttribute, Attribute nodeAttribute) throws JspTagException {
        NodeQuery nq = null;
        String container = containerAttribute.getString(thisTag);
        String node      = nodeAttribute.getString(thisTag);
        if ("".equals(container) && "".equals(node)) {
            log.debug("no node attribute, no container attribute, trying container first");
            AbstractQueryWrapper q = (AbstractQueryWrapper) thisTag.getPageContext().getAttribute(QueryContainer.KEY, QueryContainer.SCOPE);
            if (q != null && (q instanceof NodeQueryWrapper)) {
                nq = (NodeQuery) q.getQuery();
            }

            if (nq == null) {
                NodeQueryContainer c = thisTag.findParentTag(NodeQueryContainer.class, null, false);
                if (c != null) {
                    nq = c.getNodeQuery();
                }
            }
        } else if (! "".equals(container)) {
            log.debug("container attribute, trying container");
            NodeQueryContainer c = thisTag.findParentTag(NodeQueryContainer.class, container, true);
            if (c != null) {
                nq = c.getNodeQuery();
            }
        }
        if (nq == null) { // try to work as node-referrer
            log.debug("working as node-referrer");
            Node n = thisTag.findNode();
            if (n == null) {
                throw new TaglibException("No NodeQueryContainer nor a NodeProvider found in tree-tag");
            } else {
                nq = Queries.createNodeQuery(n);
            }
        }
        return nq;

    }


    void addBranch(NodeManager nodeManager, String r, String sd) {
        tree.grow(nodeManager, r, sd);
    }
    @Override
    public int doStartTag() throws JspTagException {
        initTag();
        prevQuery= pageContext.getAttribute(QueryContainer.KEY, QueryContainer.SCOPE);
        // first of all, we need a 'start' query, take it from a surrounding 'nodequery container'

        query = new NodeQueryWrapper(getStartQuery(this, container, parentNodeId));


        if (nodeManager != Attribute.NULL) {
            tree = new GrowingTreeList(query,
                                       maxDepth.getInt(this, 5),
                                       query.getCloud().getNodeManager(nodeManager.getString(this)),
                                       role.getString(this),
                                       searchDirs.getString(this));

            if (path != Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on treecontainer");
        } else {
            tree = new GrowingTreeList(query, maxDepth.getInt(this, 5));
            if (path != Attribute.NULL) {
                Queries.addPath(tree.getTemplate(), (String) path.getValue(this), (String) searchDirs.getValue(this));

                // I'm not entirely sure why the following is necessary at all:
                Step step = tree.getTemplate().getSteps().get(2);
                if (query.getSteps().contains(step)) {
                    query.setNodeStep(step);
                }
            }
        }
        if (jspVar != null) {
            pageContext.setAttribute(jspVar, tree);
        }
        pageContext.setAttribute(QueryContainer.KEY, new NodeQueryWrapper(getNodeQuery()), QueryContainer.SCOPE);
        return EVAL_BODY;
    }
    @Override
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (java.io.IOException ioe){
                throw new JspTagException(ioe.toString());
            }
        }
        return SKIP_BODY;
    }
    @Override
    public int doEndTag() throws JspTagException {
        tree = null;
        return super.doEndTag();
    }

}
