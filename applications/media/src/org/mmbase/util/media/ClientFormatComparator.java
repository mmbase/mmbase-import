 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;
import org.mmbase.module.builders.media.MediaSources;
import org.mmbase.module.builders.media.Format;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;

/**
 * This can sort a list with the requested formats on top for a client, so the 'ResponseInfo' is used.
 * @author  Michiel Meeuwissen
 * @version $Id: ClientFormatComparator.java,v 1.1 2003-01-08 22:20:25 michiel Exp $
 */
public class ClientFormatComparator extends  PreferenceComparator {
    private static Logger log = Logging.getLoggerInstance(FormatComparator.class.getName());


    public  ClientFormatComparator() {
    }
    
    protected int getPreference(ResponseInfo ri) {
        Object format = ri.getInfo().get("format");        
        if (format == null) {            
            return 0; // no client preference given
        } else {
            FormatComparator comp;
            if (format instanceof Format) {
                comp = new FormatComparator((Format) format);
            } else if (format instanceof String) {
                comp = new FormatComparator((String) format);
            } else {
                log.error("Someting wrong in client's INFO, 'format' specified wrongly: " + format);
                return 0;
            }
            return comp.getPreference(ri);
        }
    }

}

