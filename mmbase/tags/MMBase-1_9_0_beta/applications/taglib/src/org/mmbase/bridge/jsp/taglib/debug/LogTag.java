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
 * @version $Id: LogTag.java,v 1.15 2006-08-30 18:02:16 michiel Exp $
 */

public class LogTag extends ContextReferrerTag {
    private Logger log;
    private boolean doLog;
    private int counter = 0; // A counter for every page. Because of this even <mm:log /> gets usefull.

    public final static String LOGTAG_CATEGORY = Logging.PAGE_CATEGORY + ".LOGTAG";  // pages themselfs log to subcategories of this.

    private String jspvar;
    /**
     * JspVar to Create, and write to
     */
    public void setJspvar(String j) {
        jspvar = j;
    }


    public void setPageContext(PageContext pc) {
        /* Determin only once per page if it can log */
        super.setPageContext(pc);
        log = (Logger) pageContext.getAttribute("__logtag_logger");
        if(log == null) {
            log = Logging.getLoggerInstance(LOGTAG_CATEGORY + ((HttpServletRequest)pageContext.getRequest()).getRequestURI().replace('/', '.'));
            counter = 0;
            pageContext.setAttribute("__logtag_logger", log);
        }
        doLog = log.isServiceEnabled();

    }

    public int doStartTag() throws JspTagException {
        if (jspvar != null) {
            pageContext.setAttribute(jspvar, log);
            return EVAL_BODY;
        } else if (doLog) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    public int doEndTag() throws JspTagException {
        if (doLog && jspvar == null) {
            log.service(counter++ + ": " + (bodyContent != null ? bodyContent.getString() : "-"));
        }
        if (jspvar != null && EVAL_BODY == EVAL_BODY_BUFFERED) {

            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (java.io.IOException e) {
                throw new JspTagException(e.toString());
            }
        }
        super.doEndTag();
        return EVAL_PAGE;
    }
}
