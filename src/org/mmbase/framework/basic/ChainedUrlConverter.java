/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;

import java.util.*;

import org.mmbase.framework.FrameworkException;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.functions.Parameter;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Keeps track of several UrlConverters and chains them one after another.
 * If the outcome of an UrlConverter is not <code>null</code> its result is returned. The
 * question remains whether we want UrlConverters to be realy chained so that the
 * outcome of a converter can be added to the outcome of its preceder.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: ChainedUrlConverter.java,v 1.5 2008-02-22 13:03:29 michiel Exp $
 * @since MMBase-1.9
 */
public class ChainedUrlConverter implements UrlConverter {

    private static final Logger log = Logging.getLoggerInstance(ChainedUrlConverter.class);

    /**
     * List containing the UrlConverters found in the framework configuration.
     */
    private final List<UrlConverter> uclist = new ArrayList<UrlConverter>();
    private final List<Parameter>   parameterDefinition = new ArrayList<Parameter>();

    /**
     * Adds the UrlConverters to the list.
     */
    public void add(UrlConverter u) {
        uclist.add(u);
        for (Parameter p : u.getParameterDefinition()) {
            if (! parameterDefinition.contains(p)) {
                parameterDefinition.add(p);
            }
        }
    }
    public boolean contains(UrlConverter u) {
        return uclist.contains(u);
    }

    public Parameter[] getParameterDefinition() {
        return parameterDefinition.toArray(Parameter.EMPTY);
    }

    /**
     * The URL to be printed in a page
     */
    public String getUrl(String path,
                         Map<String, Object> params,
                         Parameters frameworkParameters, boolean escapeAmps) throws FrameworkException {

        String p = path;
        for (UrlConverter uc : uclist) {
            String b = uc.getUrl(p.toString(), params, frameworkParameters, escapeAmps);
            if (b != null) {
                return b;
            }
            //p = b;
        }
        //log.debug("ChainedUrlConverter has: " + b);

        return p;   // this seems incorrect (what if nothing is resolved by one of the uc's? then params etc. are lost)
    }

    public String getActionUrl(String path,
                                Map<String, Object> params,
                                Parameters frameworkParameters, boolean escapeAmps) throws FrameworkException {

        String p = new String(path);
        for (UrlConverter uc : uclist) {
            String b = uc.getActionUrl(p.toString(), params, frameworkParameters, escapeAmps);
            if (b != null) {
                return b;
            }
            //p = b;
        }
        //log.debug("ChainedUrlConverter has: " + b);

        return p;   // this seems incorrect (what if nothing is resolved by one of the uc's? then params etc. are lost)
    }


    /**
     * The 'technical' url
     */
    public String getInternalUrl(String path,
                                        Map<String, Object> params,
                                        Parameters frameworkParameters) throws FrameworkException {
        String p = new String(path);
        for (UrlConverter uc : uclist) {
            String b = uc.getInternalUrl(p.toString(), params, frameworkParameters);
            log.debug("ChainedUrlConverter has: " + b);
            if (b != null) return b;
            //p = b;
        }
        return p;   // this seems incorrect (what if nothing is resolved by one of the uc's? then params etc. are lost)
    }

    public String toString() {
        return "ChainedUrlConverter" + uclist;
    }

}
