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
 * This is a taglib-implementation of the 'TREEFILE' command.
 * A full description of this command can be found in the mmbase-taglib.xml file.
 *
 * @author Johannes Verelst
 * @version $Id: TreeFileTag.java,v 1.8 2004-01-19 17:22:09 michiel Exp $
 */

public class TreeFileTag extends UrlTag {
    
    private static final Logger log = Logging.getLoggerInstance(TreeFileTag.class.getName());
    protected Attribute objectList = Attribute.NULL;
    protected TreeHelper th = new TreeHelper();
    
    public int doStartTag() throws JspTagException {
        if (page == Attribute.NULL) {
            throw new JspTagException("Attribute 'page' was not specified");
        }
        if (objectList == Attribute.NULL) {
            throw new JspTagException("Attribute 'objectlist' was not specified");
        }        
        return super.doStartTag();
    }

    protected String getPage() throws JspTagException {        
        String orgPage = super.getPage();
        String treePage = th.findTreeFile(orgPage, objectList.getString(this), pageContext.getSession());
        if (log.isDebugEnabled()) {
            log.debug("Retrieving page '" + page + "'");
        }
        if (treePage == null) throw new JspTagException("Could not find page " + orgPage);
        return treePage;
    }

    public int doEndTag() throws JspTagException {
        th.setCloud(getCloud());
        // Let UrlTag do the rest
        return super.doEndTag();
    }
    
    /**
     * @param includePage the page to include, can contain arguments and path (path/file.jsp?argument=value)
     * @return the entire URL that specifies the best match
     */
    
    public void setObjectlist(String includePage) throws JspTagException {
        objectList = getAttribute(includePage);
    }
}
