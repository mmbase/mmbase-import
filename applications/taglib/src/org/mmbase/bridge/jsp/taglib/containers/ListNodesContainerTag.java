/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.cache.CachePolicy;
import org.mmbase.storage.search.*;

/**
 * Container cognate for ListNodesTag.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: ListNodesContainerTag.java,v 1.21.2.2 2008-02-26 17:05:15 michiel Exp $
 */
public class ListNodesContainerTag extends NodeReferrerTag implements NodeQueryContainer {
    // nodereferrer because RelatedNodesContainer extension

    protected NodeQuery   query       = null;
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
        cachePolicy = getAttribute(t);
    }

    public void setType(String t) throws JspTagException {
        nodeManager = getAttribute(t);
    }

    public void setPath(String t) throws JspTagException {
        path = getAttribute(t);
    }
    public void setSearchdirs(String s) throws JspTagException {
        searchDirs = getAttribute(s);
    }

    public void setElement(String e) throws JspTagException {
        element = getAttribute(e);
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
     * @since MMBase-1.7.1
     */
    public void setNodes(String n) throws JspTagException {
        nodes = getAttribute(n);
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
        if (query.isUsed()) query = (NodeQuery) query.clone();
        return query;
    }

    // overridden from CloudReferrer.
    public Cloud getCloudVar() throws JspTagException {
        if (query == null) return super.getCloudVar(); // I think that this does not happen.
        return query.getCloud();
    }

    public int doStartTag() throws JspTagException {
        String cloneId = clone.getString(this);
        if (! "".equals(cloneId)) {
            query = (NodeQuery) getContextProvider().getContextContainer().getObject(cloneId);
            query = (NodeQuery) query.clone();
        } else if (getReferid() != null) {
            query = (NodeQuery) getContextProvider().getContextContainer().getObject(getReferid());
            if (nodeManager != Attribute.NULL || path != Attribute.NULL || element != Attribute.NULL) {
                throw new JspTagException("Cannot use 'nodemanager', 'path' or 'element' attributes together with 'referid'");
            }
        } else {
            if (nodeManager != Attribute.NULL) {
                query = super.getCloudVar().getNodeManager(nodeManager.getString(this)).createQuery();
                if (path != Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on listnodescontainer");
                if (element != Attribute.NULL) throw new JspTagException("'element' can only be used in combination with 'path' attribute");
            } else {
                if (path == Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on listnodescontainer");

                query = super.getCloudVar().createNodeQuery();
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
                    query.setNodeStep((Step) query.getSteps().get(0));
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
