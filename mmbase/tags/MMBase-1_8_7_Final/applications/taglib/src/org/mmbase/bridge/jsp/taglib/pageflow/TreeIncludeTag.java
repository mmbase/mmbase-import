/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like IncludeTag, but an entire tree of files is being probed to find the one
 * that has the most specified value.
 *
 * This is a taglib-implementation of the 'TREEPART' command, but renamed to
 * 'Treeinclude' for esthetic reasons :)
 * A full description of this command can be found in the mmbase-taglib.xml file.
 *
 * @author Johannes Verelst
 * @version $Id: TreeIncludeTag.java,v 1.15.2.2 2008-03-13 15:52:41 michiel Exp $
 */

public class TreeIncludeTag extends IncludeTag {

    private static final Logger log = Logging.getLoggerInstance(TreeIncludeTag.class) ;
    protected Attribute objectList = Attribute.NULL;
    private TreeHelper th = new TreeHelper();

    public int doStartTag() throws JspTagException {
        if (objectList == Attribute.NULL) {
            throw new JspTagException("Attribute 'objectlist' was not specified");
        }
        th.setCloud(getCloudVar());
        th.setBackwardsCompatible(! "false".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.smartpath_backwards_compatible")));
        return super.doStartTag();
    }

    protected String getPage() throws JspTagException {
        String orgPage = super.getPage();
        try {
            String treePage = th.findTreeFile(orgPage, objectList.getString(this), pageContext.getSession());
            if (log.isDebugEnabled()) {
                log.debug("Retrieving page '" + treePage + "'");
            }

            if (treePage == null || "".equals(treePage)) {
                //throw new JspTagException("Could not find page " + orgPage);
                return orgPage;
            }

            return treePage;
        } catch (java.io.IOException ioe) {
            throw new TaglibException(ioe);
        }
    }

    public void doAfterBodySetValue() throws JspTagException {
        // Let IncludeTag do the rest of the work
        includePage();
    }

    public void doFinally() {
        th.doFinally();
        super.doFinally();
    }

    public void setObjectlist(String p) throws JspTagException {
        objectList = getAttribute(p);
    }

    protected String getUrl(boolean writeamp, boolean encode) throws JspTagException {
        String url = "";
        try {
            url = super.getUrl(writeamp, encode);
        } catch (JspTagException e) {
            if (!notFound.getString(this).equals("skip")) {
                throw(e);
            }
        }
        return url;
    }

    // override to cancel
    protected boolean doMakeRelative() {
    	log.debug("doMakeRelative() overridden!");
        return false;
    }
}
