 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.applications.media.Format;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * Client's preferred format can be different for every request. This
 * Sorter uses the 'info' Map to sort the requested formats to the top
 * of the urlcomposer list.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: ClientFormatSorter.java,v 1.6 2007-07-10 09:51:35 michiel Exp $ 
 */
public class ClientFormatSorter extends  PreferenceSorter {
    private static final Logger log = Logging.getLoggerInstance(ClientFormatSorter.class);


    public  ClientFormatSorter() {
    }
    
    protected int getPreference(URLComposer ri) {
        if (log.isDebugEnabled()) {
            log.debug("ri: " + ri);
            log.debug("info: " + ri.getInfo());
        }

        Object format = ri.getInfo().get("format");        
        if (log.isDebugEnabled()) { 
            log.debug("Client's preference " + format); 
        }
        if (format == null) { 
            log.debug("no client preference given, so " + format + " is pretty normal -> 0");
            return 0;
        } else {
            if (format instanceof Format) {
                if (format == ri.getFormat()) {
                    log.debug("client preference matched, so " + format + " is pretty good -> 100");
                    return 100;
                } else {
                    log.debug("client preference didn't match, so " + format + " is pretty normal -> 0");
                    return 0;
                }
            } else if (format instanceof String) {
                if (Format.get((String) format) == ri.getFormat()) {
                    log.debug("client preference matched, so " + format + " is pretty good -> 100");
                    return 100;
                } else {
                    log.debug("client preference didn't match, so " + format + " is pretty normal -> 0");
                    return 0;
                }
            } else if (format instanceof List) {
                List formatList = (List) format;
                int i = formatList.indexOf(ri.getFormat().toString());
                int result = i == -1 ? -10000 : -i; 
                // the higher in this list, the better, 0 is highest.
                log.debug("format list " + formatList + " (" + ri.getFormat() + ") --> " + result);
                return result;

            } else {
                log.error("Someting wrong in client's INFO, 'format' specified wrongly: " + format + " ignorind and returning 0");
                return 0;
            }
        }
    }

}

