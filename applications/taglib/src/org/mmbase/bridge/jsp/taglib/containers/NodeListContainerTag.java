/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.*;

import org.mmbase.bridge.*;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListContainerTag.java,v 1.1 2003-07-23 17:46:58 michiel Exp $
 */
public class NodeListContainerTag extends CloudReferrerTag implements NodeListContainer {


    private static Logger log = Logging.getLoggerInstance(NodeListContainerTag.class);

    private NodeList   result    = null;
    private Query      query     = null;


    public Query getQuery() {
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
        query = getCloud().createQuery();
        return EVAL_BODY_BUFFERED;
    }

}
