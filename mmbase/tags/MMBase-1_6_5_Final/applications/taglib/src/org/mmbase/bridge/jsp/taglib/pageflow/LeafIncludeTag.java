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
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.*;

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
 */
public class LeafIncludeTag extends IncludeTag {
    
    private static Logger log = Logging.getLoggerInstance(LeafIncludeTag.class.getName());
    protected String objectlist;
    private TreeHelper th = new TreeHelper();

    public int doStartTag() throws JspTagException {        
        if (objectlist == null) {
            throw new JspTagException("Attribute 'objectlist' was not specified");
        }
        return super.doStartTag();
    }
    
    public void doAfterBodySetValue() throws JspTagException {
        LeafFileTag leaffilehelper = new LeafFileTag();
                    
        th.setCloud(getCloud());
        
        String orgpage = page;
        page = th.findLeafFile(orgpage, objectlist, pageContext.getSession());
        log.debug("Retrieving page '" + page + "'");
        if (page == null) throw new JspTagException("Could not find page " + orgpage);
        
        // Let IncludeTag do the rest of the work
        includePage();
    }
    
    public void setObjectlist(String p) throws JspTagException {
        objectlist = getAttributeValue(p);
    }
}
