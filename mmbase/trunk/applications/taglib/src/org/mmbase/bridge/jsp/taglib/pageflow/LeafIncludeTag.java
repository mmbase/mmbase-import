/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.jsp.taglib.pageflow;
import java.util.ArrayList;
import java.util.Map;

import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.bridge.jsp.taglib.pageflow.UrlTag.UrlParameters;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Notfound;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like IncludeTag, but an entire tree of files is being probed to find the one
 * that has the most specified value.
 *
 * This is a taglib-implementation of the 'LEAFPART' command
 * A full description of this command can be found in the mmbase-taglib.xml file.
 *
 * @author Johannes Verelst
 * @version $Id: LeafIncludeTag.java,v 1.23 2007-06-18 17:26:51 michiel Exp $
 */

public class LeafIncludeTag extends IncludeTag {

    private static final Logger log = Logging.getLoggerInstance(LeafIncludeTag.class);
   
    protected Attribute objectList = Attribute.NULL;
    private TreeHelper th = new TreeHelper();


    public void setObjectlist(String p) throws JspTagException {
        objectList = getAttribute(p);
    }


    protected String getPage(String p) throws JspTagException {
        try {
            return th.findLeafFile(p, objectList.getValue(this).toString(),
                                   pageContext.getSession());
        } catch (java.io.IOException ioe) {
            throw new TaglibException(ioe);
        }
    }
    
    protected void initTag(boolean internal) throws JspTagException {
        super.initTag(internal);
        th.setCloud(getCloudVar());
        th.setBackwardsCompatible(! "false".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.smartpath_backwards_compatible")));
        url.setLegacy();
    }


    public void doFinally() {
        th.doFinally();
        super.doFinally();
    }



    // override to cancel
    protected boolean doMakeRelative() {
    	log.debug("doMakeRelative() overridden!");
        return false;
    }
}
