/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.cache.CachePolicy;
import org.mmbase.storage.search.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Container cognate for ListNodesTag.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 */
public class ListNodesContainerTag extends NodeReferrerTag implements NodeQueryContainer {

    private static final Logger log = Logging.getLoggerInstance(ListNodesContainerTag.class);
    // nodereferrer because RelatedNodesContainer extension

    protected NodeQueryWrapper   query       = null;
    protected Object      prevQuery   = null;
    protected Attribute cachePolicy  = Attribute.NULL;
    protected Attribute   path        = Attribute.NULL;
    protected Attribute   searchDirs  = Attribute.NULL;
    protected Attribute   nodeManager = Attribute.NULL;
    protected  Attribute   element     = Attribute.NULL;
    protected  Attribute   nodes       = Attribute.NULL;
    protected  Attribute   clone       = Attribute.NULL;
    protected  Attribute   markused    = Attribute.NULL;
    protected String jspVar = null;

    /**
     * @since MMBase-1.8.0
     */
    public void setCachepolicy(String t) throws JspTagException {
        cachePolicy = getAttribute(t, true);
    }

    public void setType(String t) throws JspTagException {
        nodeManager = getAttribute(t, true);
    }

    public void setPath(String t) throws JspTagException {
        path = getAttribute(t, true);
    }
    public void setSearchdirs(String s) throws JspTagException {
        searchDirs = getAttribute(s, true);
    }

    public void setElement(String e) throws JspTagException {
        element = getAttribute(e, true);
    }

    /**
     * @since MMBase-1.7.1
     */
    public void setNodes(String n) throws JspTagException {
        nodes = getAttribute(n, true);
    }


    /**
     * @since MMBase-1.8.6
     */
    public void setClone(String c) throws JspTagException {
        clone = getAttribute(c);
    }
    /**
     * @since MMBase-1.8.6
     */
    public void setMarkused(String mu) throws JspTagException {
        markused = getAttribute(mu);
    }

    /**
     * @since MMBase-1.8.1
     */
    public void setJspvar(String jv) {
        jspVar = jv;
    }


    public Query getQuery() {
        return getNodeQuery();
    }

    public NodeQuery getNodeQuery() {
        if (query.isUsed()) {
            query.cloneQuery();
        }
        return query;
    }

    // overridden from CloudReferrer.
    public Cloud getCloudVar() throws JspTagException {
        if (query == null) {
            return super.getCloudVar(); // I think that this does not happen.
        }
        return query.getCloud();
    }

    /**
     * @TODO following code must be put in org.mmbase.bridge.util.Queries or
     * org.mmbase.util.Casting or so.
     * @since MMBase-1.9.1
     */
    private NodeQuery toNodeQuery(Object o) throws JspTagException {
        if (o == null) return null;
        if (o instanceof NodeQuery) {
            return (NodeQuery) o;
        } else if (o instanceof SearchQuery) {
            SearchQuery q = (SearchQuery) o;
            if (q.getSteps().size() != 1) {
                throw new IllegalStateException("The object " + q + " has not precisely one step and can therefore not be converted to a NodeQuery");
            }
            NodeQuery nq = getCloudVar().getNodeManager(q.getSteps().get(0).getTableName()).createQuery();
            nq.setConstraint(Queries.copyConstraint(nq.getConstraint(), nq.getSteps().get(0), nq, nq.getNodeStep()));
            nq.setOffset(q.getOffset());
            nq.setMaxNumber(q.getMaxNumber());
            nq.setDistinct(q.isDistinct());
            return nq;
        } else {
            // will give CCE.
            return (NodeQuery) o;
        }
    }

    public int doStartTag() throws JspException {
        initTag();
        prevQuery= pageContext.getAttribute(QueryContainer.KEY, QueryContainer.SCOPE);
        String cloneId = clone.getString(this);
        if (! "".equals(cloneId)) {
            query = new NodeQueryWrapper(toNodeQuery(((AbstractQueryWrapper) getContextProvider().getContextContainer().getObject(cloneId))));
            if (query == null) {
                throw new JspTagException("No query found with id '" + cloneId + "' in " + getContextProvider().getContextContainer());
            }
            query.cloneQuery();
        } else if (getReferid() != null) {
            Object o = getContextProvider().getContextContainer().getObject(getReferid());
            query = new NodeQueryWrapper(toNodeQuery(o));

            if (query == null) {
                throw new JspTagException("No query found in referred id " + getReferid());
            }
            if (nodeManager != Attribute.NULL || path != Attribute.NULL || element != Attribute.NULL) {
                throw new JspTagException("Cannot use 'nodemanager', 'path' or 'element' attributes together with 'referid'");
            }
        } else {
            if (nodeManager != Attribute.NULL) {
                query = new NodeQueryWrapper(super.getCloudVar().getNodeManager(nodeManager.getString(this)).createQuery());
                if (path != Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on listnodescontainer");
                if (element != Attribute.NULL) throw new JspTagException("'element' can only be used in combination with 'path' attribute");
            } else {
                if (path == Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on listnodescontainer");

                query = new NodeQueryWrapper(super.getCloudVar().createNodeQuery());
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
        }
        if (cachePolicy != Attribute.NULL) {
            query.setCachePolicy(CachePolicy.getPolicy(cachePolicy.getValue(this)));
        }

        if (nodes != Attribute.NULL) {
            Queries.addStartNodes(query, nodes.getString(this));
        }

        if (getId() != null) { // write to context.
            getContextProvider().getContextContainer().register(getId(), query);
        }
        if (jspVar != null) {
            pageContext.setAttribute(jspVar, query);
        }
        pageContext.setAttribute(QueryContainer.KEY, query, QueryContainer.SCOPE);

        return EVAL_BODY;
    }

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
    public int doEndTag() throws JspTagException {
        pageContext.setAttribute(KEY, prevQuery, SCOPE);
        prevQuery = null;
        if (markused.getBoolean(this, false)) {
            query.markUsed();
        }
        query = null;
        return super.doEndTag();
    }

    public Object getCurrent() {
        return null;
    }

}
