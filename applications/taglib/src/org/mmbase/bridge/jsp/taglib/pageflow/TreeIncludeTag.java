/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Stack;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.*;

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
 */
public class TreeIncludeTag extends IncludeTag {
    
    private static Logger log = Logging.getLoggerInstance(TreeIncludeTag.class.getName());
    protected String objectlist;
    private TreeHelper th = new TreeHelper();
    
    public int doAfterBody() throws JspTagException {
        // sigh, we would of course prefer to extend, but no multiple inheritance possible in Java..
        TreeFileTag treefilehelper = new TreeFileTag();
        
        if (page == null) {
            throw new JspTagException("Attribute 'page' was not specified");
        }
        if (objectlist == null) {
            throw new JspTagException("Attribute 'objectlist' was not specified");
        }
        
        th.setCloud(getCloud());
        
        String orgpage = page;
        page = th.findTreeFile(orgpage, objectlist, pageContext.getSession());
        
        log.debug("Retrieving page '" + page + "'");
        if (page == null) throw new JspTagException("Could not find page " + orgpage);
        // Let IncludeTag do the rest of the work
        return includePage();
    }
    
    public void setObjectlist(String p) throws JspTagException {
        objectlist = getAttributeValue(p);
    }

        
}
