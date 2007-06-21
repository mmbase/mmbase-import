 /*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
  */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;

/**
 * filters the media sources with the format specified by the client.
 * e.g. urls(mp3), will result in only media sources with format = mp3.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class ClientFormatFilter implements Filter {
    private static Logger log = Logging.getLoggerInstance(ClientFormatFilter.class);
    
    public void configure(DocumentReader reader, Element e) {
        // nothing to be configured on default.
    }
    
    final public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        List<URLComposer> filteredUrlcomposers = new ArrayList<URLComposer>();
        
        for (URLComposer urlcomposer : urlcomposers) {
            Object format = urlcomposer.getInfo().get("format");
            if (log.isDebugEnabled()) {
                log.debug("Client specified format = " + format);
            }
          
            if(format==null) {
                if (log.isDebugEnabled()) {
                    log.debug("Client did not specify format.");
                }
                return urlcomposers;
            }

            if( format instanceof List) {
                if( ((List)format).size()==0 ) {
                    if (log.isDebugEnabled()) {
                        log.debug("Client did not specify format.");
                    }
                    return urlcomposers;
		}
            } 

            if (format instanceof Format) {
                if (format == urlcomposer.getFormat()) filteredUrlcomposers.add(urlcomposer);
            } else if (format instanceof String) {
                if (Format.get(""+format) == urlcomposer.getFormat()) filteredUrlcomposers.add(urlcomposer);
            } else if (format instanceof List) {
                // You could take the order in which the client specified the formats into account.
                List formatList = (List) format;
                if(formatList.contains(urlcomposer.getFormat().toString())) {
                    filteredUrlcomposers.add(urlcomposer);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("filteredUrlcomposers = "+filteredUrlcomposers);
        }
        return filteredUrlcomposers;
    }
}
