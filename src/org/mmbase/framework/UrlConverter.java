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
 * @version $Id: UrlConverter.java,v 1.8 2007-11-16 17:30:33 michiel Exp $
 * @since MMBase-1.9
 * @todo Parameters are passed as Collections of Map.Entry. Not sure that is handy/correct. The main
 * reason is that you can create such objects easily from both Parameters as from Maps, and that you
 * don't loose order, if there is one (it is also easiy to create a map from Parameters, but it has
 * no garanteed order any more then).
 */
public interface UrlConverter {


    Parameter[] getParameterDefinition();

    /**
     * See {@link Framework#getUrl(String, Map, Parameters,boolean)}.
     * But it can also return <code>null</code> which mean, 'I don't know.'
     */
    StringBuilder getUrl(String path,
                         Map<String, Object> parameters,
                         Parameters frameworkParameters,
                         boolean escapeAmps);


    /**
     * See {@link Framework#geInternaltUrl(String, Map, Parameters)}.
     * But it can also return <code>null</code> which mean, 'I don't know'.
     */
    StringBuilder getInternalUrl(String path,
                                 Map<String, Object> params,
                                 Parameters frameworkParameters);


}
