/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.bridge.*;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: ListNodesContainerTag.java,v 1.3 2003-07-26 14:50:48 pierre Exp $
 */
public class ListNodesContainerTag extends CloudReferrerTag implements NodeListContainer {


    private static Logger log = Logging.getLoggerInstance(ListNodesContainerTag.class);

    private NodeList   result    = null;
    private NodeQuery   query     = null;

    private Attribute nodeManager = Attribute.NULL;


    public void setType(String t) throws JspTagException {
        nodeManager = getAttribute(t);
    }

    public Query getQuery() {
        if (query.isUsed()) query = (NodeQuery) query.clone();
        return query;
    }


    // javadoc inherited (from NodeListContainer)
    public void   setResult(NodeList result) throws JspTagException {
        if (result != null) {
            throw new JspTagException("Result was set already");
        }
        this.result = result;
    }


    public NodeList  getResult() throws JspTagException {
        if (result == null) {
            throw new JspTagException("No result available yet");
        }
        return result;
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
