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
import org.mmbase.storage.search.Step;
//import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: RelatedContainerTag.java,v 1.21 2008-08-14 13:58:37 michiel Exp $
 */
public class RelatedContainerTag extends NodeReferrerTag implements QueryContainer {

    //  private static final Logger log = Logging.getLoggerInstance(RelatedContainerTag.class);

    private Query     query      = null;
    private Object prevQuery     = null;
    private Attribute cachePolicy  = Attribute.NULL;
    private Attribute path       = Attribute.NULL;
    private Attribute searchDirs = Attribute.NULL;
    private Attribute fields     = Attribute.NULL;
    protected String jspVar = null;

    public void setCachepolicy(String t) throws JspTagException {
        cachePolicy = getAttribute(t);
    }

    public void setPath(String t) throws JspTagException {
        path = getAttribute(t);
    }

    public void setSearchdirs(String s) throws JspTagException {
        searchDirs = getAttribute(s);
    }


    public void setFields(String f) throws JspTagException {
        fields = getAttribute(f);
    }

    /**
     * @since MMBase-1.8.1
     */
    public void setJspvar(String jv) {
        jspVar = jv;
    }

    public Query getQuery() {
        if (query.isUsed()) query = (Query) query.clone();
        return query;
    }



    public int doStartTag() throws JspException {
        initTag();
        prevQuery= pageContext.getAttribute(QueryContainer.KEY, QueryContainer.SCOPE);
        if (getReferid() != null) {
            query = (Query) getContextProvider().getContextContainer().getObject(getReferid());
        } else {
            if (path == Attribute.NULL) {
                throw new JspTagException("Path attribute is mandatory");
            }
            Node node = getNode();
            Cloud cloud = node.getCloud();
            query = cloud.createQuery();

            Step step = query.addStep(node.getNodeManager());
            query.setAlias(step, node.getNodeManager().getName() + "0");
            query.addNode(step, node);

        }

        if (getId() != null) { // write to context.
            getContextProvider().getContextContainer().register(getId(), query);
        }
        if (jspVar != null) {
            pageContext.setAttribute(jspVar, query);
        }


        if (cachePolicy != Attribute.NULL) {
            query.setCachePolicy(CachePolicy.getPolicy(cachePolicy.getValue(this)));
        }

        Queries.addPath(query, (String) path.getValue(this), (String) searchDirs.getValue(this));

        Queries.addFields(query, (String) fields.getValue(this));

        pageContext.setAttribute(QueryContainer.KEY, query, QueryContainer.SCOPE);

        return EVAL_BODY;
    }

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
