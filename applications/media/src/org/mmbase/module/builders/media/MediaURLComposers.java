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


/**
 * Provides the functionality to create URL's (or URI's) for a certain
 * fragment, source, provider combination.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MediaURLComposers.java,v 1.5 2003-01-14 20:36:20 michiel Exp $
 * @since MMBase-1.7
 */
public class MediaURLComposers extends MMObjectBuilder {    
    private static Logger log = Logging.getLoggerInstance(MediaURLComposers.class.getName());
    
    protected class MediaResponseInfo extends ResponseInfo {
        protected MMObjectNode    composer;
        protected MMObjectNode    provider;
        protected  MMObjectNode    fragment;
        MediaResponseInfo(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
            this.composer = composer; 
            this.provider = provider;
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

        public String       getURL() {
            StringBuffer args = new StringBuffer(composer.getStringValue("protocol") + "://" + provider.getStringValue("host") + composer.getStringValue("rootpath") + source.getStringValue("url"));
            return getArgs(args).toString();
        }
        public boolean      isAvailable() { 
            Boolean fragmentAvailable;
            if (fragment != null) {
                fragmentAvailable = (Boolean) fragment.getFunctionValue(MediaFragments.FUNCTION_AVAILABLE, null);
            } else {
                fragmentAvailable = Boolean.TRUE;
            }
            boolean providerAvailable = (provider.getIntValue("state") == MediaProviders.STATE_ON); // todo: use symbolic constant
            boolean sourceAvailable    = (source.getIntValue("state") == 3); // todo: use symbolic constant
            return fragmentAvailable.booleanValue() && providerAvailable && sourceAvailable;
        }

    }


    protected ResponseInfo createResponseInfo(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        return new MediaResponseInfo(composer, provider, source, fragment, info); 
    };

    /**
     * Returns just an abstract respresentation of an urlcomposer object. Only useful for editors.
     */

    public String getGUIIndicator(MMObjectNode n) {
        return n.getStringValue("protocol") + "://" + "&lt;host&gt;" +  n.getStringValue("rootpath");
    }

    /** 
     * A MediaURLComposer can construct one or more URL's for every
     * fragment/source/provider/composer/info combination
     *
     * For this all nodes of importance can be used
     * (form the composer to fragment and possibly some other
     * preferences stored in the Map argument)..
     * 
     * @throws IllegalArgumentException if provider or source is null
     * @returns A List of ResponseInfo's
     */

     public List getURLs(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
         if (provider == null) throw new IllegalArgumentException("Cannot create URL without a provider");
         if (source   == null) throw new IllegalArgumentException("Cannot create URL without a source");
         List result       = new ArrayList();
         result.add(createResponseInfo(composer, provider, source, fragment, info));
         return result;
     }

    
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.debug("Executing function " + function + " on node " + node.getNumber() + " with argument " + args);
        } 
        
        if (function.equals("info")) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, "info", empty);
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        }
        return super.executeFunction(node, function, args);
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
