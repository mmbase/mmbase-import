/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

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
 * @version $Id: TreeFileTag.java,v 1.30 2008-06-20 15:59:59 michiel Exp $
 */

public class TreeFileTag extends UrlTag {

    private static final Logger log = Logging.getLoggerInstance(TreeFileTag.class);
    protected Attribute objectList = Attribute.NULL;
    protected final TreeHelper th = new TreeHelper();

    protected Attribute notFound        = Attribute.NULL;

    public void setObjectlist(String p) throws JspTagException {
        objectList = getAttribute(p);
    }
    public void setNotfound(String n) throws JspTagException {
        notFound = getAttribute(n);
    }

    @Override
    protected String getPage(String p) throws JspTagException {
        try {
            return th.findTreeFile(p, objectList.getValue(this).toString(),
                                   pageContext.getSession());
        } catch (java.io.IOException ioe) {
            throw new TaglibException(ioe);
        }
    }

    @Override
    protected void initTag(boolean internal) throws JspTagException {
        th.setCloud(getCloudVar());
        th.setBackwardsCompatible(! "false".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.smartpath_backwards_compatible")));
        super.initTag(internal);
        url.setLegacy();
        if (log.isDebugEnabled()) {
            log.debug("TreeFile end of starttag: " + url.toString());
        }
    }

    @Override
    public int doStartTag() throws JspTagException {
        super.doStartTag();
        helper.setValue(url);
        return EVAL_BODY; // lets try _not_ buffering the body.
        // this may give unexpected results if ${_} is not used (or another tag calling 'haveBody')
    }

    @Override
    public void doFinally() {
        th.doFinally();
        super.doFinally();
    }


    @Override
    // override to cancel
    protected boolean doMakeRelative() {
        log.debug("doMakeRelative() overridden!");
        return false;
    }


    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

}
