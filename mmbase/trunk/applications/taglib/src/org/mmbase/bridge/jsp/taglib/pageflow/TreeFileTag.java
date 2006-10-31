/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Notfound;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like IncludeTag, but an entire tree of files is being probed to find the one
 * that has the most specified value.
 *
 * This is a taglib-implementation of the 'TREEFILE' command.
 * A full description of this command can be found in the mmbase-taglib.xml file.
 *
 * @author Johannes Verelst
 * @version $Id: TreeFileTag.java,v 1.21 2006-10-31 16:06:10 michiel Exp $
 */

public class TreeFileTag extends UrlTag {

    private static final Logger log = Logging.getLoggerInstance(TreeFileTag.class);
    protected Attribute objectList = Attribute.NULL;
    protected TreeHelper th = new TreeHelper();

    protected Attribute notFound        = Attribute.NULL;

    public void setNotfound(String n) throws JspTagException {
        notFound = getAttribute(n);
    }

    protected String getPage() throws JspTagException {
        String orgPage = super.getPage();
        String treePage = th.findTreeFile(orgPage, objectList.getString(this), pageContext.getSession());
        if (log.isDebugEnabled()) {
            log.debug("Retrieving page '" + treePage + "'");
        }

        if (treePage == null || "".equals(treePage)) {
            throw new JspTagException("Could not find page " + orgPage);
        }
        StringBuilder show = new StringBuilder();
        if (! useAbsoluteAttribute(show, treePage)) {
            if (treePage.charAt(0) == '/') {
                HttpServletRequest req =  (HttpServletRequest) getPageContext().getRequest();
                show.insert(0, req.getContextPath());
            }
            show.append(treePage);
        }

        return show.toString();
    }

    public int doEndTag() throws JspTagException {
        th.setCloud(getCloudVar());
        // Let UrlTag do the rest
        int retval = super.doEndTag();
        return retval;
    }

    public void doFinally() {
        th.doFinally();
        super.doFinally();
    }

    /**
     * @param includePage the page to include, can contain arguments and path (path/file.jsp?argument=value)
      */

    public void setObjectlist(String includePage) throws JspTagException {
        objectList = getAttribute(includePage);
    }

    // override to cancel
    protected boolean doMakeRelative() {
    	log.debug("doMakeRelative() overridden!");
        return false;
    }

    protected String getUrl(boolean writeamp, boolean encode) throws JspTagException {
        String url = "";
        try {
            url = super.getLegacyUrl(writeamp, encode);
        } catch (JspTagException e) {
            // TODO Test this.
            if (Notfound.get(notFound, this) == Notfound.SKIP) {
                throw e;
            }
        }
        return url;
    }

}
