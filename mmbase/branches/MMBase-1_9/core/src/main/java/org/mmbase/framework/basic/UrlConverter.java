/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import java.util.*;
import java.io.Serializable;

import org.mmbase.framework.*;
import org.mmbase.util.functions.*;

/**
 * Responsible for the proper handling of urls within the basic framework {@link BasicFramework}.
 * You should implement UrlConverter if you want to create and resolve your own
 * user-friendly links within {@link BasicFramework}.
 *
 * You can configure several UrlConverters in 'config/framework.xml'. They will be
 * chained one after another.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 * @todo EXPERIMENTAL
 */
public interface UrlConverter extends Serializable {


    /**
     * An URLConverter can add parameters to it's parent Framework. If the parameter is already
     * defined in the Framework, the framework will of course ignore the one requested to be defined
     * by this UrlConverter.
     */
    Parameter<?>[] getParameterDefinition();


    /**
     * The state of rendering will be determined (request.getRequestUri) and we will return if the
     * current URL is managed by <em>this</em> UrlConverter
     */
    boolean isFilteredMode(Parameters frameworkParameters) throws FrameworkException;

    int getDefaultWeight();

    /**
     * See {@link org.mmbase.framework.Framework#getUrl(String, Map, Parameters, boolean)}.
     * But it can also return <code>null</code> which mean, 'I don't know.'
     * @param path The path (generally a relative URL) to create an URL for.
     * @param parameters Parameters The parameters to be passed to the page, as specified e.g. with  mm:param -tags
     * @param frameworkParameters The parameters that are required by the framework
     * @param escapeAmps <code>true</code> if parameters should be added with an escaped &amp; (&amp;amp;).
     *                   You should escape &amp; when a URL is exposed (i.e. in HTML), but not if the url is
     *                   for some reason called directly.
     * @return An URL relative to the root of this web application (i.e. without a context
     * path). {@link Url#NOT} if not determinable.
     * @throws FrameworkException thrown when something goes wrong in the Framework
     */
    Url getUrl(String path,
                  Map<String, ?> parameters,
                  Parameters frameworkParameters,
                  boolean escapeAmps) throws FrameworkException;

    /**
     * @return An URL relative to the root of this web application (i.e. without a context  path). Never <code>null</code>
     */
    Url getProcessUrl(String path,
                         Map<String, ?> parameters,
                         Parameters frameworkParameters,
                         boolean escapeAmps) throws FrameworkException;



    /**
     * See {@link org.mmbase.framework.Framework#getInternalUrl(String, Map, Parameters)}.
     * But it can also return <code>null</code> which mean, 'I don't know'.
     * @param path The page (e.g. image/css) provided by the component to create an URL for
     * @param params Extra parameters for that path
     * @param frameworkParameters The parameters that are required by the framework, such as the
     *                            'request' and 'cloud' objects
     * @return A valid internal URL, or {@link Url#NOT} if nothing framework specific could be
     *         determined (this would make it possible to 'chain' frameworks).
     * @throws FrameworkException thrown when something goes wrong in the Framework
     */
    Url getInternalUrl(String path,
                       Map<String, ?> params,
                       Parameters frameworkParameters) throws FrameworkException;


}
