/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.io.*;
import java.util.*;
import org.mmbase.bridge.Node;
import org.mmbase.util.functions.Parameters;

/**
 * Responsible for the proper handling of urls within the framework. 
 * Called by FrameworkFilter to resolve userfriendly links into technical urls.
 * You should implement UrlConverter if you want to create and resolve your own
 * userfriendly links within your framework.
 * 
 * @author Michiel Meeuwissen
 * @version $Id: UrlConverter.java,v 1.2 2007-06-28 11:18:19 andre Exp $
 * @since MMBase-1.9
 */
public interface UrlConverter {


    /** 
     * Return a (possibly modified) URL for a given path. 
     * This method is called (for example) from within the mm:url tag, and can be exposed to the outside world.
     * I.e. when within a components's head you use<br />
     * &lt;mm:url page="/css/style.css" /&gt;, <br />
     * this method is called to determine the proper url (i.e., relative to the framework or component base).
     * If you need treefile/leaffile type of functionality in your framework, you can implement that
     * here in your code.
     *
     * @param path The path (generally a relative URL) to create an URL for.
     * @param parameters Parameters The parameters to be passed to the page
     * @param frameworkParameters The parameters that are required by the framework
     * @param escapeAmps <code>true</code> if parameters should be added with an escaped &amp; (&amp;amp;). 
     *                   You should escape &amp; when a URL is exposed (i.e. in HTML), but not if the url is 
     *                   for some reason called directly. 
     * @return An URL relative to the root of this web application (i.e. withouth a context path)
     */
    public StringBuilder getUrl(String path, 
                                Collection<Map.Entry<String, Object>> parameters,
                                Parameters frameworkParameters, boolean escapeAmps);


    /**
     * Generates an URL to a resource to be called and included by a renderer.
     * Typically, this generates a URL to a jsp, called by a renderer such as the {@link JspRenderer}, 
     * who calls the resource using the RequestDispatcher.
     * This method allows for frameworks to do some filtering on URLs (such as pretty URLs).
     * You should generally not call this method unless you write a Renderer that depends on code or
     * data from external resources.
     * @param path The page (e.g. image/css) provided by the component to create an URL for
     * @param params Extra parameters for that path
     * @param frameworkParameters The parameters that are required by the framework, such as the
     *                            'request' and 'cloud' objects
     * @return A valid interal URL, or <code>null</code> if nothing framework specific could be
     *         determined (this would make it possible to 'chain' frameworks).
     */
    public StringBuilder getInternalUrl(String path, 
                                        Collection<Map.Entry<String, Object>> params, 
                                        Parameters frameworkParameters);


}
