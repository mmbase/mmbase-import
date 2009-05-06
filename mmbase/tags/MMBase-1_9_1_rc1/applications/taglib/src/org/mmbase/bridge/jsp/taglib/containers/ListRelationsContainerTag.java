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
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import org.mmbase.cache.CachePolicy;


/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: ListRelationsContainerTag.java,v 1.25 2009-04-28 08:46:23 michiel Exp $
 */
public class ListRelationsContainerTag extends NodeReferrerTag implements NodeQueryContainer {

    private NodeQuery   query        = null;
    private Object      prevQuery    = null;
    private Attribute cachePolicy  = Attribute.NULL;
    private Attribute type       = Attribute.NULL;
    private Attribute role       = Attribute.NULL;
    private Attribute searchDir  = Attribute.NULL;
    protected String jspVar = null;

    public void setCachepolicy(String t) throws JspTagException {
        cachePolicy = getAttribute(t);
    }

    /**
     * @param t a nodeManager
     */
    public void setType(String t) throws JspTagException {
        type  = getAttribute(t);
    }
    /**
     * @param r a role
     */
    public void setRole(String r) throws JspTagException {
        role  = getAttribute(r, true);
    }
    public void setSearchdir(String s) throws JspTagException {
        searchDir = getAttribute(s, true);
    }


    public Query getQuery() {
        return getNodeQuery();
    }
    public NodeQuery getNodeQuery() {
        if (query.isUsed()) query = (NodeQuery) query.clone();
        return query;
    }

    public Node getRelatedFromNode() throws JspTagException {
        return getNode();
    }

    @Override
    public int doStartTag() throws JspException {
        initTag();
        prevQuery= pageContext.getAttribute(QueryContainer.KEY, QueryContainer.SCOPE);
        if (getReferid() != null) {
            query = (NodeQuery) getContextProvider().getContextContainer().getObject(getReferid());
        } else {
            Node relatedFromNode = getNode();
            Cloud cloud = relatedFromNode.getCloud();
            NodeManager nm = null;
            if (type != Attribute.NULL) {
                nm = cloud.getNodeManager(type.getString(this));
            }
            query        = Queries.createRelationNodesQuery(relatedFromNode, nm, (String) role.getValue(this), (String) searchDir.getValue(this));
        }

        if (cachePolicy != Attribute.NULL) {
            query.setCachePolicy(CachePolicy.getPolicy(cachePolicy.getValue(this)));
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
    @Override
    public int doAfterBody() throws JspTagException {
        if(EVAL_BODY == EVAL_BODY_BUFFERED) {
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
        pageContext.setAttribute(KEY, prevQuery, SCOPE);
        prevQuery = null;
        query = null;
        return super.doEndTag();
    }

    public Object getCurrent() {
        return null;
    }

}
