/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: ListNodesContainerTag.java,v 1.6 2003-08-29 12:12:24 keesj Exp $
 */
public class ListNodesContainerTag extends CloudReferrerTag implements NodeListContainer {


    private static final Logger log = Logging.getLoggerInstance(ListNodesContainerTag.class);

    private NodeQuery   query     = null;
    private Attribute nodeManager = Attribute.NULL;


    public void setType(String t) throws JspTagException {
        nodeManager = getAttribute(t);
    }

    public Query getQuery() {
        if (query.isUsed()) query = (NodeQuery) query.clone();
        return query;
    }



    public int doStartTag() throws JspTagException {        
        query = getCloud().getNodeManager(nodeManager.getString(this)).createQuery();
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
