/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.debug;

import javax.servlet.jsp.JspTagException;
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

    public int doAfterBody() throws JspTagException {
        String thisPage = ((HttpServletRequest)pageContext.getRequest()).getRequestURI().replace('/', '.');        
        Logger log = Logging.getLoggerInstance("MMBASE-PAGE" + thisPage);
        log.service(bodyContent.getString());
        return SKIP_BODY;
    }    
}
