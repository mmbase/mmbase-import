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
import javax.servlet.jsp.JspException;

import org.mmbase.util.Casting;

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
 * @version $Id: TreeFileTag.java,v 1.18.2.2 2007-06-07 13:52:24 michiel Exp $
 */

public class TreeFileTag extends UrlTag {

    private static final Logger log = Logging.getLoggerInstance(TreeFileTag.class);
    protected Attribute objectList = Attribute.NULL;
    protected TreeHelper th = new TreeHelper();

    protected Attribute notFound        = Attribute.NULL;

    public void setNotfound(String n) throws JspTagException {
        notFound = getAttribute(n);
    }


    public int doStartTag() throws JspTagException {
        if (page == Attribute.NULL) {
            throw new JspTagException("Attribute 'page' was not specified");
        }
        if (objectList == Attribute.NULL) {
            throw new JspTagException("Attribute 'objectlist' was not specified");
        }
        th.setCloud(getCloudVar());
        th.setBackwardsCompatible(! "false".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.smartpath_backwards_compatible")));
        super.doStartTag();
        helper.setValue(new Comparable() {
                            final TreeFileTag t = TreeFileTag.this;
                            public String toString() {
                                try {
                                    String string = t.getUrl();
                                    // this means that it is written to page by ${_} and that consequently there _must_ be a body.
                                    // this is needed when body is not buffered.
                                    haveBody();
                                    return string;
                                } catch (Throwable e){
                                    return e.toString();
                                }
                            }
                            public int compareTo(Object o) {
                                return toString().compareTo(Casting.toString(o));
                            }
                        });
        return EVAL_BODY; // lets try _not_ buffering the body.
        // this may give unexpected results if ${_} is not used (or another tag calling 'haveBody')
    }

    protected String getPage() throws JspTagException {
        String orgPage = super.getPage();
        try {
            String treePage = th.findTreeFile(orgPage, objectList.getString(this), pageContext.getSession());
            if (log.isDebugEnabled()) {
                log.debug("Retrieving page '" + treePage + "'");
            }
            
            if (treePage == null || "".equals(treePage)) {
                throw new JspTagException("Could not find page " + orgPage);
            }
            return treePage;
        } catch (java.io.IOException ioe) {
            throw new TaglibException(ioe);
        }

    }


    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
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
            url = super.getUrl(writeamp, encode);
        } catch (JspTagException e) {
            if (!notFound.getString(this).equals("skip")) {
                throw(e);
            }
        }
        return url;
    }

}
