/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import javax.servlet.jsp.PageContext;
import java.util.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.Entry;
import org.mmbase.util.functions.Parameters;

/**
 * A Framework is the place where components are displayed in. 
 *
 * @author Johannes Verelst
 * @version $Id: Framework.java,v 1.2 2006-10-13 23:00:03 johannes Exp $
 * @since MMBase-1.9
 */
public interface Framework {

    /** Return the name of the framework */
    public String getName();

    /** 
     * Return a modified URL for a given page. This method is called from within the mm:url
     * tag.
     * @param page The page to create an URL for
     * @param component The component to use to search the file for
     * @param cloud The cloud to use to find objects if required
     * @param pageContext The current page context, can be used to get the request, response, etc.
     * @param params The content of the mm:param tags that were passed. Note that the implementing class may add entries to this list
     */
    public String getUrl(String page, Component component, Parameters parameters);

    //Cloud cloud, PageContext pageContext, List<Entry> params) throws javax.servlet.jsp.JspTagException;


    public Parameters createUrlParameters(); 


    /** 
     * Return whether or not the 'getUrl' method will return a relative URL (relative to the
     * current request) or not. The default behavior of mm:url is to return a 'true' here,
     * but if you want to implement behavior like the mm:leaffile/mm:treefile tags, you have
     * to return false here.
     */
    public boolean makeRelativeUrl();
}
