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
 * @version $Id: Framework.java,v 1.4 2006-10-14 09:46:48 michiel Exp $
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
     * @param blockParameters The parameters that were set on the block using referids and sub-&lt;mm:param&gt; tags
     * @param frameworkParameters The parameters that are required by the framework, for instance containing the 'request' and 'cloud'.
     */

    public String getUrl(String page, Component component, Parameters blockParameters, Parameters frameworkParameters);

    /**
     * Return a Parameters object that needs to be passed on to the getUrl() call. The following parameters will be auto-filled
     * if they are returned here:
     * <ul>
     *  <li>Parameter.CLOUD</li>
     *  <li>Parameter.REQUEST</li>
     *  <li>Parameter.RESPONSE</li>
     * </ul>
     */
    public Parameters createFrameworkParameters(); 

    /** 
     * Return whether or not the 'getUrl' method will return a relative URL (relative to the
     * current request) or not. The default behavior of mm:url is to return a 'true' here,
     * but if you want to implement behavior like the mm:leaffile/mm:treefile tags, you have
     * to return false here.
     */
    public boolean makeRelativeUrl();
}
