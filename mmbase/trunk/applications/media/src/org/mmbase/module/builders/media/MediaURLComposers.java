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
 * @version $Id: MediaURLComposers.java,v 1.1 2003-01-03 21:35:44 michiel Exp $
 * @since MMBase-1.7
 */
public class MediaURLComposers extends MMObjectBuilder {    
    private static Logger log = Logging.getLoggerInstance(MediaURLComposers.class.getName());

    public static final String FUNCTION_URL = "url";
    
    /**
     * ResponseInfo is a wrapper around an URL.  It contains besides
     * the URL some extra meta information about it, like the MimeType
     * of the resource it represents and if it is currently available
     * or not.  An URL can be unavailable because of two reasons:
     * Because the provider is offline, or because the fragment where
     * it belongs to is not valid (e.g. because of publishtimes)
     *
     */

    public class ResponseInfo  {
        private URL    url;
        private String mimeType;
        private boolean available;
        ResponseInfo(URL u, String m, boolean a) {
            url = u; mimeType = m; available = a;
        }
        ResponseInfo(URL u, String m) {
            this(u, m, true);
        }
        public URL    getURL()       { return url;      }
        public String getMimeType()  { return mimeType; }
        public boolean isAvailable() { return available; }
        
        public String toString() {
            return url.toString();
        }
    }

    /**
     * Returns just an abstract respresentation of an urlcomposer object. Only useful for editors.
     */

    public String getGUIIndicator(MMObjectNode n) {
        try {
            return new URL(n.getStringValue("protocol"), "&lt;host&gt;", n.getStringValue("rootpath")).toString();
        } catch (MalformedURLException e) { return null; }
    }

    /** 
     * Construct an URL. For this all nodes of importance can be used
     * (form the composer to fragment and possibly some other
     * preferences stored in the Map argument)..
     * 
     * This is the end-point of a chain of 'getFunction' calls to
     * 'getURLs' which would normally start in a fragment (or possibly
     * in a source).
     * 
     * @throws MalformedURLException
     * @throws IllegalArgumentException if provider or source is null
     */

    protected URL getURL(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map preferences) throws MalformedURLException {        
        if (provider == null) throw new IllegalArgumentException("Cannot create URL without a provider");
        if (source   == null) throw new IllegalArgumentException("Cannot create URL without a source");
        return 
            new URL(composer.getStringValue("protocol"),
                    provider.getStringValue("host"),
                    composer.getStringValue("rootpath") + source.getStringValue("url"));
    }
    /**
     * This function is between executeFunction and getURL and has two
     * purposes. It translates the executeFunction argument List to
     * the arguments of getURL and wraps the result in a ResponseInfo object.
     *
     * Final, because I think that you should override getURL.
     */
    final protected ResponseInfo getResponseInfo(MMObjectNode n, List arguments) throws MalformedURLException {
        MMObjectNode provider   = null;
        MMObjectNode source     = null;
        MMObjectNode fragment   = null;
        Map info   = null;
        if (arguments != null && arguments.size() > 0) {
            provider = (MMObjectNode) arguments.get(0);
            if (arguments.size() > 1) {
                source = (MMObjectNode) arguments.get(1);
                if (arguments.size() > 2) {
                    fragment = (MMObjectNode) arguments.get(2);
                    if (arguments.size() > 3) {
                        info = (Map) arguments.get(3);
                    }
                }
            }
        }
        URL url = getURL(n, provider, source, fragment, info);
        return new ResponseInfo(url, source.getStringValue("format"));
    }
    
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.debug("Executing function " + function + " on node " + node.getNumber() + " with argument " + args);
        } 
        
        if (function.equals("info")) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, "info", empty);
            info.put(FUNCTION_URL, "(provider, source, fragment, info) A ResponseInfo evaluated for this composer/provider/source/fragment");
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if (function.equals(FUNCTION_URL)) {
            try {
                return getResponseInfo(node, args);
            } catch (MalformedURLException e) {
                return "";
            }
        }
        return super.executeFunction(node, function, args);
    }
}
