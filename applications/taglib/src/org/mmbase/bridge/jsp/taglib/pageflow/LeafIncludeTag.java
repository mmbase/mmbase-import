/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.bridge.jsp.taglib.pageflow;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

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
 * @version $Id: LeafIncludeTag.java,v 1.9 2004-01-20 09:46:11 johannes Exp $
 */

public class LeafIncludeTag extends IncludeTag {
    
    private static final Logger log = Logging.getLoggerInstance(LeafIncludeTag.class.getName());
    protected Attribute objectList = Attribute.NULL;
    private TreeHelper th = new TreeHelper();

    public int doStartTag() throws JspTagException {        
        if (objectList == Attribute.NULL) {
            throw new JspTagException("Attribute 'objectlist' was not specified");
        }
        return super.doStartTag();
    }

    protected String getPage() throws JspTagException {        
        String orgPage = super.getPage();
        String leafPage = th.findLeafFile(orgPage, objectList.getString(this), pageContext.getSession());
        if (log.isDebugEnabled()) {
            log.debug("Retrieving page '" + leafPage + "'");
        }
        if (leafPage == null) throw new JspTagException("Could not find page " + orgPage);
        return leafPage;
    }
    
    public void doAfterBodySetValue() throws JspTagException {
        th.setCloud(getCloud());
        // Let IncludeTag do the rest of the work
        includePage();
    }
    
    public void setObjectlist(String p) throws JspTagException {
        objectList = getAttribute(p);
    }

    // override to cancel 
    protected StringBuffer makeRelative(StringBuffer show) {
        log.debug("makeRelative() overridden!");
        return show;
    }
}
