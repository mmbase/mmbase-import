 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.applications.media.builders.MediaSources;
import org.mmbase.applications.media.Format;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;

/**
 * Client's format can be different for every request. So this does
 * not extend FormatComparator, but is does something similar.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: ClientFormatComparator.java,v 1.3 2003-02-05 14:43:05 michiel Exp $
 */
public class ClientFormatComparator extends  PreferenceComparator {
    private static Logger log = Logging.getLoggerInstance(ClientFormatComparator.class.getName());


    public  ClientFormatComparator() {
    }
    
    protected int getPreference(URLComposer ri) {
        log.info("ri: " + ri);
        log.info("inof: " + ri.getInfo());

        Object format = ri.getInfo().get("format");        
        if (log.isDebugEnabled()) { log.debug("Client's preference " + format); }
        if (format == null) {                  
            return 0; // no client preference given
        } else {
            FormatComparator comp;
            if (format instanceof Format) {
                if (format == ri.getFormat()) return 100;
            } else if (format instanceof String) {
                if (Format.get((String) format) == ri.getFormat()) return 100;
            } else if (format instanceof List) {
                List formatList = (List) format;
                int i = formatList.indexOf(ri.getFormat().toString());
                return i == -1 ? -10000 : -i; // the higher in this list, the better, 0 is highest.

            } else {
                log.error("Someting wrong in client's INFO, 'format' specified wrongly: " + format);
                return 0;
            }
        }
        return 0;
    }

}

