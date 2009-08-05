
/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;


import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletResponse;
import org.mmbase.bridge.jsp.taglib.TaglibException;



import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Does a redirect, using the features of UrlTag.
 *
 * @author Michiel Meeuwissen
 * @version $Id: RedirectTag.java,v 1.10 2009-01-12 14:14:12 michiel Exp $
 * @since MMBase-1.7
 */

public class RedirectTag extends UrlTag  {

    private static final Logger log = Logging.getLoggerInstance(RedirectTag.class);


    @Override protected final String getAbsolute() throws JspTagException {
        return "server";
    }
    @Override protected final boolean escapeAmps() throws JspTagException {
        return false;
    }
    @Override protected final boolean encode() throws JspTagException {
        return true;
    }
    /**
     * Method called at end of Tag used to send redirect,
     * always skips the remainder of the JSP page.
     *
     * @return SKIP_PAGE
     */
    @Override public final int doEndTag() throws JspTagException {
        super.doEndTag();
        try {
            // dont set value, but redirect.
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            response.sendRedirect(url.get());
        } catch (java.io.IOException io) {
            throw new TaglibException(io);
        }
	return SKIP_PAGE;
    }




}
