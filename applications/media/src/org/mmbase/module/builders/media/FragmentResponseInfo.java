/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.module.builders.media;

import org.mmbase.module.core.MMObjectNode;
import java.util.Map;
import java.util.Hashtable;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: FragmentResponseInfo.java,v 1.1 2003-01-21 17:46:23 michiel Exp $
 * @todo    Move to org.mmbase.util.media, I think
 */

abstract public class FragmentResponseInfo extends ResponseInfo  {
    protected MMObjectNode fragment;

    public FragmentResponseInfo(MMObjectNode source, MMObjectNode fragment, Map info) {
        this.source   = source;
        this.fragment = fragment;
        this.info     = info;
        if (this.info == null) this.info = new Hashtable();        
    }

    protected StringBuffer getArgs(StringBuffer args) {                                   
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
            
            if (getFormat().isReal()) { 
                // real...
                String title = fragment.getStringValue("title");
                args.append(sep).append("title=").append(title);
            }
        }
        return args;
    }
    
    public boolean      isAvailable() { 
        Boolean fragmentAvailable;
        if (fragment != null) {
            fragmentAvailable = (Boolean) fragment.getFunctionValue(MediaFragments.FUNCTION_AVAILABLE, null);
        } else {
            fragmentAvailable = Boolean.TRUE;
        }
        boolean sourceAvailable    = (source.getIntValue("state") == 3); // todo: use symbolic constant
        return fragmentAvailable.booleanValue() && sourceAvailable;
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
