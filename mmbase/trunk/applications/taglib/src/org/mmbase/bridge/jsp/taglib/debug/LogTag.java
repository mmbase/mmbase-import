/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.debug;

import javax.servlet.jsp.*;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The implementation of the log tag.
 *
 * @author Michiel Meeuwissen 
 **/

public class LogTag extends ContextReferrerTag {
    private Logger log;           
    private boolean doLog;
    private int counter = 0; // A counter for every page. Because of this even <mm:log /> gets usefull.

    public final static String LOGTAG_CATEGORY = PAGE_CATEGORY + ".LOGTAG";  // pages themselfs log to subcategories of this.

    public void setPageContext(PageContext pc) {
        /* Determin only once per page if it can log */
        super.setPageContext(pc);        
        log = Logging.getLoggerInstance(LOGTAG_CATEGORY + ((HttpServletRequest)pageContext.getRequest()).getRequestURI().replace('/', '.'));
        doLog = log.isServiceEnabled();        
    }

    public int doStartTag() throws JspTagException {
        if (doLog) {            
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }        
    }

    public int doAfterBody() throws JspTagException {
        if (doLog) log.service(counter++ + ": " + bodyContent.getString());
        return SKIP_BODY;
    }    
}
