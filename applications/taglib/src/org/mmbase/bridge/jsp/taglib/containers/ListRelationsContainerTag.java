/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: ListRelationsContainerTag.java,v 1.1 2004-02-26 22:11:50 michiel Exp $
 */
public class ListRelationsContainerTag extends NodeReferrerTag implements QueryContainer {


    private static final Logger log = Logging.getLoggerInstance(ListRelationsContainerTag.class);

    private Query   query        = null;
    private NodeQuery   relatedQuery        = null;
    private Attribute type       = Attribute.NULL;
    private Attribute role       = Attribute.NULL;
    private Attribute searchDir  = Attribute.NULL;



    /**
     * @param type a nodeManager
     */
    public void setType(String t) throws JspTagException {
        type  = getAttribute(t);
    }
    /**
     * @param role a role
     */
    public void setRole(String r) throws JspTagException {
        role  = getAttribute(r);
    }

    public void setSearchdir(String s) throws JspTagException {
        searchDir = getAttribute(s);
    }


    public Query getQuery() {
        if (query.isUsed()) query = (Query) query.clone();
        return query;
    }

    public Node getRelatedFromNode() throws JspTagException {
        return getNode();
    }

    public NodeQuery getRelatedQuery() {
        // if (relatedQuery.isUsed()) relatedQuery = (NodeQuery) relatedQuery.clone();
        return relatedQuery;
    }


    public int doStartTag() throws JspTagException {        
        Cloud cloud = getCloud();
        query = cloud.createQuery();

        NodeManager nm = null;
        if (type != Attribute.NULL) {
            nm = getCloud().getNodeManager(type.getString(this));
        }
        Node relatedFromNode = getNode();
        query = Queries.createRelationNodesQuery(relatedFromNode, nm, (String) role.getValue(this), (String) searchDir.getValue(this)); 
        relatedQuery = Queries.createRelatedNodesQuery(relatedFromNode, nm, (String) role.getValue(this), (String) searchDir.getValue(this)); 

        return EVAL_BODY_BUFFERED;
    }
    // if EVAL_BODY == EVAL_BODY_BUFFERED
    public int doAfterBody() throws JspTagException {
        try {
            if (bodyContent != null) {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }
        } catch (java.io.IOException ioe){
            throw new JspTagException(ioe.toString());
        } 
        return SKIP_BODY;        
    }

}
