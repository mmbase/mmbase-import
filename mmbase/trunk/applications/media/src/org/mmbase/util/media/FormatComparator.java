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
 * @version $Id: FormatComparator.java,v 1.2 2003-01-08 08:50:18 michiel Exp $
 */
public class FormatComparator extends  PreferenceComparator {
    private static Logger log = Logging.getLoggerInstance(FormatComparator.class.getName());

    public static String CONFIG_TAG = "preferredSource";
    public static String SOURCE_TAG = "source";
    public static String FORMAT_ATT = "format";

    private List preferredSources = new ArrayList();
    public  FormatComparator(String f) {
        preferredSources.add(f);
    }
    public  FormatComparator() {};

    public void configure(XMLBasicReader reader, Element el) {
        preferredSources.clear();
        // reading preferredSource information    
        for( Enumeration e = reader.getChildElements(reader.getElementByPath(el, MediaSourceFilter.FILTERCONFIGS_TAG + "." + CONFIG_TAG), SOURCE_TAG);e.hasMoreElements();) {
            Element n3=(Element)e.nextElement();
            String format = reader.getElementAttributeValue(n3, FORMAT_ATT);
            preferredSources.add(format.toLowerCase());
            log.service("Adding preferredSource format: '"+format +"'");
        }
  
    }
    
    protected int getPreference(ResponseInfo ri) {
        String format = (String) ri.getSource().getFunctionValue(MediaSources.FUNCTION_FORMAT, null);
        int index =  preferredSources.indexOf(format);
        if (index == -1) { 
            log.debug("Not found format: '" + format + "' in" + preferredSources);
            index = preferredSources.size() + 1;
        }
        index = -index;   // low index =  high preference
        log.debug("preference of format '" + format + "': " + index);
        return index; 
    }

}

