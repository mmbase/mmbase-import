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
 * @version $Id: MediaURLComposers.java,v 1.3 2003-01-07 14:52:56 michiel Exp $
 * @since MMBase-1.7
 */
public class MediaURLComposers extends MMObjectBuilder {    
    private static Logger log = Logging.getLoggerInstance(MediaURLComposers.class.getName());

    public static final String FUNCTION_URL = "url";
    
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

    protected URL getURL(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, List preferences) throws MalformedURLException {        
        if (provider == null) throw new IllegalArgumentException("Cannot create URL without a provider");
        if (source   == null) throw new IllegalArgumentException("Cannot create URL without a source");
        return 
            new URL(composer.getStringValue("protocol"),
                    provider.getStringValue("host"),
                    composer.getStringValue("rootpath") + source.getStringValue("url"));
    }
    protected boolean isAvailable(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, List preferences) {
        Boolean fragmentAvailable = (Boolean) fragment.getFunctionValue(MediaFragments.FUNCTION_AVAILABLE, null);
        boolean providerAvailable = (provider.getIntValue("state") == 1); // todo: use symbolic constant
        boolean sourceAvailable = (source.getIntValue("state") == 3); // todo: use symbolic constant
        return fragmentAvailable.booleanValue() && providerAvailable && sourceAvailable;
    }

    /**
     * This function is between executeFunction and getURL and has two
     * purposes. It translates the executeFunction argument List to
     * the arguments of getURL and wraps the result in a ResponseInfo object.
     *
     * Final, because I think that you should override getURL.
     */
    final protected ResponseInfo getResponseInfo(MMObjectNode composer, List arguments) throws MalformedURLException {
        MMObjectNode provider   = null;
        MMObjectNode source     = null;
        MMObjectNode fragment   = null;
        List info   = null;
        if (arguments != null && arguments.size() > 0) {
            provider = (MMObjectNode) arguments.get(0);
            if (arguments.size() > 1) {
                source = (MMObjectNode) arguments.get(1);
                if (arguments.size() > 2) {
                    fragment = (MMObjectNode) arguments.get(2);
                    if (arguments.size() > 3) {
                        info = arguments.subList(3, arguments.size());
                    }
                }
            }
        }
        URL url        = getURL(composer, provider, source, fragment, info);
        boolean online = isAvailable(composer, provider, source, fragment, info);
        return new ResponseInfo(url, source, online); 
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
