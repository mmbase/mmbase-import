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
 * A FormatSorter which can be configured with eht filters.xml.
 *
 * @todo this implementation can be merged with FormatSorter itself, i think.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: ServerFormatSorter.java,v 1.1 2003-02-05 16:31:39 michiel Exp $
 */
public class ServerFormatSorter extends  FormatSorter {
    private static Logger log = Logging.getLoggerInstance(ServerFormatSorter.class.getName());

    public static final String CONFIG_TAG = MainFilter.FILTERCONFIG_TAG + ".preferredSource";
    public static final String FORMAT_ATT = "format";

    public  ServerFormatSorter() {};

    public void configure(XMLBasicReader reader, Element el) {
        preferredFormats.clear();
        // reading preferredSource information    
        for( Enumeration e = reader.getChildElements(reader.getElementByPath(el, CONFIG_TAG));e.hasMoreElements();) {
            Element n3=(Element)e.nextElement();
            String format = reader.getElementAttributeValue(n3, FORMAT_ATT);
            preferredFormats.add(Format.get(format));
            log.service("Adding preferredSource format: '"+format +"'");
        }
  
    }
    
}

