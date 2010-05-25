/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;
import java.net.*;
import java.io.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class HasPageTag extends ContextReferrerTag implements Condition {

    private static  final Logger log = Logging.getLoggerInstance(HasPageTag.class);

    protected Attribute  page                 = Attribute.NULL;
    public void setPage(String p) throws JspTagException {
        page = getAttribute(p);
    }
    protected Attribute inverse = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    protected static String getResource(PageContext pageContext, String page) {
        String resource = page;
        if (! resource.startsWith("/")) {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            // Fetch the current servlet from request attribute.
            // This is needed when we are resolving relatively.
            String includingServlet = (String) request.getAttribute(IncludeTag.INCLUDE_PATH_KEY);
            if (includingServlet == null) {
                includingServlet = request.getServletPath();
            }
            try {
                log.debug("URL was relative");
                // Using url-objects only because they know how to resolve relativity
                URL u = new URL("http", "localhost", includingServlet);
                URL dir = new URL(u, "."); // directory
                File currentDir = new File(includingServlet + "includetagpostfix"); // to make sure that it is not a directory (tomcat 5 does not redirect then)
                resource = new URL(dir, resource).getFile();
            } catch (MalformedURLException mfue) {
                log.error(mfue);
            }

        }
        return resource;
    }


    public int doStartTag() throws JspTagException {
        if (page == Attribute.NULL) {
            throw new JspTagException("Attribute 'page' was not specified");
        }
        String resource = getResource(pageContext, page.getString(this));
        try {
            if (ResourceLoader.getWebRoot().getResource(resource).openConnection().getDoInput() != getInverse()) {
                return EVAL_BODY;
            } else {
                return SKIP_BODY;
            }
        } catch (IOException ioe) {
            log.error(ioe);
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            if (bodyContent != null) {
                try{
                    if(bodyContent != null) {
                        bodyContent.writeOut(bodyContent.getEnclosingWriter());
                    }
                } catch(java.io.IOException e){
                    throw new TaglibException(e);
                }
            }
        }
        return SKIP_BODY;
    }
}
