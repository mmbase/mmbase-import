/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.module.builders.media;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.net.*;
import java.text.*;


/**
 * An example. URL's from these kind of URLComposers can contain 'start' and 'end' arguments.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MyURLComposers.java,v 1.3 2003-01-08 22:23:07 michiel Exp $
 * @since MMBase-1.7
 */
public class MyURLComposers extends MediaURLComposers {
    
    private static Logger log = Logging.getLoggerInstance(MyURLComposers.class.getName());

    /** 
     * Adds 'start' and 'end' parameters using the fragment.
     * @throws MalformedURLException
     */

    protected List getURLs(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map preferences) {
        if (provider == null) throw new IllegalArgumentException("Cannot create URL without a provider");
        if (source   == null) throw new IllegalArgumentException("Cannot create URL without a source");

        StringBuffer args = new StringBuffer(composer.getStringValue("rootpath") +  source.getStringValue("url"));
        if (fragment != null) {
            int start = fragment.getIntValue("start");
            int end   = fragment.getIntValue("stop");
            char sep = '?';
            if (start > -1) {            
                appendTime(start, args.append(sep).append("start="));
                sep = '&';
            }
            if (end > -1) {            
                appendTime(end, args.append(sep).append("end="));
                sep = '&';
            }
            Format format = Format.get(source.getIntValue("format"));
            if (format.isReal()) { 
                // real...
                String title = fragment.getStringValue("title");
                args.append(sep).append("title=").append(title);
            }
        }
        List result = new ArrayList();
        try {
            result.add(
                       new URL(composer.getStringValue("protocol"), provider.getStringValue("host"), args.toString())
                       );
        } catch (MalformedURLException e) {
            log.error(e);
        }
        return result;
    }


    /**
     * Script accept times that look like dd:hh:mm:ss.th, where t is tenths of seconds.
     * @param time the time in milliseconds
     * @return the time in real format
     */
    protected static StringBuffer appendTime(int time, StringBuffer buf) {
        time /= 10; // in centis

        int centis = -1;
        int s     = 0;
        int min   = 0;
        int h     = 0;
        int d     = 0;

        if (time != 0) {
            centis = time % 100;
            time /= 100;  // in s
            if (time != 0) {
                s = time % 60;
                time /= 60;  // in min
                if (time != 0) {
                    min = time % 60;
                    time /= 60; // in hour
                    if (time != 0) {
                        h = time % 24;
                        d = time / 24;   // in day
                    }
                }
            }
        }
        boolean append = false;

        if (d > 0) append = true;
        if (append) buf.append(d).append(':');
        if (h > 0) append = true;
        if (append) buf.append(h).append(':');
        if (min > 0) append = true;
        if (append) buf.append(min).append(':');
        buf.append(s);
        if (centis > 0) {
            buf.append('.');
            if (centis < 10) buf.append('0');
            buf.append(centis);
        }
        
        return buf;
    }

}

