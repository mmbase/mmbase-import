/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import java.util.*;

import org.mmbase.framework.FrameworkException;
import org.mmbase.util.functions.*;

/**
 * Responsible for the proper handling of urls within the basic framework {@link BasicFramework}.
 * You should implement UrlConverter if you want to create and resolve your own
 * userfriendly links within {@link BasicFramework}
 *.
 * You can configure several UrlConverters in 'framework.xml'.
 *
 * They will be chained one after another.

 * @author Michiel Meeuwissen
 * @version $Id: UrlConverter.java,v 1.5 2008-02-22 14:05:57 michiel Exp $
 * @since MMBase-1.9
 */
public interface UrlConverter {


    Parameter[] getParameterDefinition();

    /**
     * See {@link Framework#getUrl(String, Map, Parameters, boolean)}.
     * But it can also return <code>null</code> which mean, 'I don't know.'
     */
    String getUrl(String path,
                  Map<String, Object> parameters,
                  Parameters frameworkParameters,
                  boolean escapeAmps) throws FrameworkException;

    String getProcessUrl(String path,
                         Map<String, Object> parameters,
                         Parameters frameworkParameters,
                         boolean escapeAmps) throws FrameworkException;



    /**
     * See {@link Framework#geInternaltUrl(String, Map, Parameters)}.
     * But it can also return <code>null</code> which mean, 'I don't know'.
     */
    String getInternalUrl(String path,
                          Map<String, Object> params,
                          Parameters frameworkParameters) throws FrameworkException;


}
