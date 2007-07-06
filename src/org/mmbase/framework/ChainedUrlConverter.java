/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.io.*;
import java.util.*;

import org.mmbase.util.functions.Parameters;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Keeps track of several UrlConverters and chains them one after another. 
 * If the outcome of an UrlConverter is not null its result is returned. The 
 * question is whether we want UrlConverters to be realy chained so that the 
 * outcome of a converter can be added to the outcome of its preceder.
 * 
 * @author Andr&eacute; van Toly
 * @version $Id: ChainedUrlConverter.java,v 1.1 2007-07-06 14:22:47 andre Exp $
 * @since MMBase-1.9
 */
public class ChainedUrlConverter implements UrlConverter {

    private static final Logger log = Logging.getLoggerInstance(ChainedUrlConverter.class);
    
    /** 
     * List containing the UrlConverters found in the framework configuration.
     */
    private final List<UrlConverter> uclist = new ArrayList<UrlConverter>();
    
    /** 
     * Constructor.
     */
    public ChainedUrlConverter() {
    }
    
    /** 
     * Adds the UrlConverters to the list.
     */
    public void add(UrlConverter u) {
        uclist.add(u);
    }
    
    /** 
     * The URL to be printed in a page
     */
    public StringBuilder getUrl(String path, 
                                Collection<Map.Entry<String, Object>> params,
                                Parameters frameworkParameters, boolean escapeAmps) {

        StringBuilder p = new StringBuilder(path);
        for (UrlConverter uc : uclist) {
            StringBuilder b = uc.getUrl(p.toString(), params, frameworkParameters, escapeAmps);
            log.debug("ChainedUrlConverter has: " + b.toString());
            if (b != null) return b;
            p = b;
        }
        return p;   // this seems incorrect (what if nothing is resolved by one of the uc's? then params etc. are lost)
    }


    /** 
     * The 'technical' url
     */
    public StringBuilder getInternalUrl(String path, 
                                        Collection<Map.Entry<String, Object>> params, 
                                        Parameters frameworkParameters) {
        StringBuilder p = new StringBuilder(path);
        for (UrlConverter uc : uclist) {
            StringBuilder b = uc.getInternalUrl(p.toString(), params, frameworkParameters);
            log.debug("ChainedUrlConverter has: " + b.toString());
            if (b != null) return b;
            p = b;
        }
        return p;   // this seems incorrect (what if nothing is resolved by one of the uc's? then params etc. are lost)
    }

}
