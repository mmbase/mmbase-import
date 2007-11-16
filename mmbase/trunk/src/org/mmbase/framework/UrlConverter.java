/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.mmbase.util.functions.*;

/**
 * Responsible for the proper handling of urls within the framework.
 * Called by FrameworkFilter to resolve userfriendly links into technical urls.
 * You should implement UrlConverter if you want to create and resolve your own
 * userfriendly links within your framework.
 * You can configure several UrlConverters in your framework's 'framework.xml'.
 * They will be chained one after another.

 * @author Michiel Meeuwissen
 * @version $Id: UrlConverter.java,v 1.7 2007-11-16 11:40:08 michiel Exp $
 * @since MMBase-1.9
 * @todo Parameters are passed as Collections of Map.Entry. Not sure that is handy/correct. The main
 * reason is that you can create such objects easily from both Parameters as from Maps, and that you
 * don't loose order, if there is one (it is also easiy to create a map from Parameters, but it has
 * no garanteed order any more then).
 */
public interface UrlConverter {


    Parameter[] getParameterDefinition();

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
     * @param process    If following the URL must lead to a process rendering. IOW, this URL is
     *                   for form actions.
     * @return An URL relative to the root of this web application (i.e. withouth a context path),
     * or <code>null</code> if this UrlConvert does not know how produce an url for given parameteters.
     */
    StringBuilder getUrl(String path,
                         Map<String, Object> parameters,
                         Parameters frameworkParameters,
                         boolean escapeAmps);


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
     * or <code>null</code> if this UrlConvert does not know how produce an url for given parameteters.
     */
    StringBuilder getInternalUrl(String path,
                                 Map<String, Object> params,
                                 Parameters frameworkParameters);


}
