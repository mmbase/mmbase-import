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
import org.mmbase.util.StringSplitter;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: ListContainerTag.java,v 1.1 2003-07-29 17:08:09 michiel Exp $
 */
public class ListContainerTag extends CloudReferrerTag implements NodeListContainer {


    private static Logger log = Logging.getLoggerInstance(ListContainerTag.class);

    private Query   query     = null;
    private Attribute path = Attribute.NULL;


    public void setPath(String t) throws JspTagException {
        path = getAttribute(t);
    }

    public Query getQuery() {
        if (query.isUsed()) query = (Query) query.clone();
        return query;
    }


    public int doStartTag() throws JspTagException {        
        if (path == Attribute.NULL) {
            throw new JspTagException("Path attribute is mandatory");
        }
        Cloud cloud = getCloud();
        query = cloud.createQuery();
        Iterator i = StringSplitter.split(path.getString(this)).iterator();
        NodeManager previous = null;
        if (i.hasNext()) {
            previous = cloud.getNodeManager((String) i.next());
            query.addStep(previous);
        }       
        
        while (i.hasNext()) {
            String t = (String) i.next();
            if (cloud.hasNodeManager(t)) {
                NodeManager current  = cloud.getNodeManager(t);
                query.addRelationStep(current);
            } else {
                NodeManager current = cloud.getNodeManager((String) i.next());
                RelationManager rm = cloud.getRelationManager(previous, current, t);
                query.addRelationStep(rm);                
            }
        }
         
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
