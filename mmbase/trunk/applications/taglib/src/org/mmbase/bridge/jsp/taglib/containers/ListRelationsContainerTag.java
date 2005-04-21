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
import java.util.*;
import org.mmbase.storage.search.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: ListRelationsContainerTag.java,v 1.7 2005-04-21 11:13:09 michiel Exp $
 */
public class ListRelationsContainerTag extends NodeReferrerTag implements QueryContainer {

    private static final Logger log = Logging.getLoggerInstance(ListRelationsContainerTag.class);
    private NodeQuery   query        = null;
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
        if (query.isUsed()) query = (NodeQuery) query.clone();
        return query;
    }

    public Node getRelatedFromNode() throws JspTagException {
        return getNode();
    }

    public NodeQuery getRelatedQuery() {
        NodeQuery r = (NodeQuery) relatedQuery.clone();
        // copy constraint and sort-orders of the query.
        List querySteps = query.getSteps();
        List rSteps     = r.getSteps();
        for (int i = 0 ; i < querySteps.size(); i++) {
            Step queryStep = (Step) querySteps.get(i);
            Step rStep = (Step) rSteps.get(i);            
            log.info("Copyuing" + queryStep);
            Queries.copyConstraint(query.getConstraint(), queryStep, r, rStep);  
            Queries.copySortOrders(query.getSortOrders(), queryStep, r, rStep);  
        }

        return r;
    }


    public int doStartTag() throws JspTagException {        
        Cloud cloud = getCloudVar();
        NodeManager nm = null;
        if (type != Attribute.NULL) {
            nm = getCloudVar().getNodeManager(type.getString(this));
        }
        Node relatedFromNode = getNode();
        query        = Queries.createRelationNodesQuery(relatedFromNode, nm, (String) role.getValue(this), (String) searchDir.getValue(this)); 
        relatedQuery = Queries.createRelatedNodesQuery(relatedFromNode, nm, (String) role.getValue(this), (String) searchDir.getValue(this)); 

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
        query = null;
        return super.doEndTag();
    }

}
