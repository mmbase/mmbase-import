 /*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
  */

package org.mmbase.module.builders.media;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

import java.util.Hashtable;


/**
 *
 *
 */
public class MediaProvider extends MMObjectBuilder {
    
    private static Logger log = Logging.getLoggerInstance(MediaProvider.class.getName());
    
    /**
     * The protocols that have to be used as default are specified in the
     * mediaprovider.xml
     *
     * @param format the format of the mediasource
     * @return the protocol belonging to the media format
     */
    private String getDefaultProtocol(String format) {
        log.debug("checking format "+format);
        Hashtable properties = getInitParameters();
        if(properties.containsKey(format)) {
            // Take protocol for media format from builder definition file.
            return (String)properties.get(format);
        } else if(properties.containsKey("default")) {
            // media format to protocol not specifies, so take default from properties.
            return (String)properties.get("default");
        } else {
            // Even no default protocol is specifies, give error.
            log.error("No protocol for media format "+format+" and default specified");
        }
        return null;
    }
    
    /**
     * evaluate the protocol for the media source
     */
    public String getProtocol(MMObjectNode mediasource) {
        // Later check extra protocol objects to evaluate the protocol.
        // At this point just use a list specified in mediaproviders.xml.
        return getDefaultProtocol((String)mediasource.getStringValue("str(format)"));
    }
}
