 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;
import org.mmbase.module.builders.media.MediaSources;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;

/**
 * This can sort a list with the requested formats on top.
 * @author  Michiel Meeuwissen
 * @version $Id: FormatComparator.java,v 1.1 2003-01-07 22:21:02 michiel Exp $
 */
public class FormatComparator extends  PreferenceComparator {
    private static Logger log = Logging.getLoggerInstance(FormatComparator.class.getName());
    private List preferredSources = new ArrayList();
    public  FormatComparator(String f) {
        preferredSources.add(f);
    }
    public  FormatComparator() {};

    // todo, make it work
    public void configure(XMLBasicReader reader, Element el) {
        // reading preferredSource information    
        for( Enumeration e = reader.getChildElements("mediasourcefilter.preferredSource","source");e.hasMoreElements();) {
            Element n3=(Element)e.nextElement();
            String format = reader.getElementAttributeValue(n3,"format");
            preferredSources.add(format.toLowerCase());
            log.debug("Adding preferredSource format: "+format);
        }
  
    }
    
    protected int getPreference(ResponseInfo ri) {        
        int index =  preferredSources.indexOf(ri.getSource().getFunctionValue(MediaSources.FUNCTION_FORMAT, null));
        if (index == -1) index = preferredSources.size() + 1;
        return -index;   // low index =  high preference
    }

}

