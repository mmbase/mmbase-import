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
 * @version $Id: MediaURLComposers.java,v 1.4 2003-01-08 22:23:07 michiel Exp $
 * @since MMBase-1.7
 */
public class MediaURLComposers extends MMObjectBuilder {    
    private static Logger log = Logging.getLoggerInstance(MediaURLComposers.class.getName());
    
    /**
     * Returns just an abstract respresentation of an urlcomposer object. Only useful for editors.
     */

    public String getGUIIndicator(MMObjectNode n) {
        try {
            return new URL(n.getStringValue("protocol"), "&lt;host&gt;", n.getStringValue("rootpath")).toString();
        } catch (MalformedURLException e) { return null; }
    }

    /** 
     * A MediaURLComposer can construct one or more URL's for every source/provider combination
     *
     * For this all nodes of importance can be used
     * (form the composer to fragment and possibly some other
     * preferences stored in the Map argument)..
     * 
     * @throws MalformedURLException
     * @throws IllegalArgumentException if provider or source is null
     * @returns A List of ResponseInfo's
     */

    protected List getURLs(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map preferences) {        
        if (provider == null) throw new IllegalArgumentException("Cannot create URL without a provider");
        if (source   == null) throw new IllegalArgumentException("Cannot create URL without a source");
        List result = new ArrayList();
        try {
            result.add(
                       new URL(composer.getStringValue("protocol"),
                               provider.getStringValue("host"),
                               composer.getStringValue("rootpath") + source.getStringValue("url")));
        } catch (MalformedURLException e) {
            log.error(e.toString());
        }
        return result;
    }
    protected boolean isAvailable(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map preferences) {
        Boolean fragmentAvailable;
        if (fragment != null) {
            fragmentAvailable = (Boolean) fragment.getFunctionValue(MediaFragments.FUNCTION_AVAILABLE, null);
        } else {
            fragmentAvailable = Boolean.TRUE;
        }
        boolean providerAvailable = (provider.getIntValue("state") == MediaProviders.STATE_ON); // todo: use symbolic constant
        boolean sourceAvailable = (source.getIntValue("state") == 3); // todo: use symbolic constant
        return fragmentAvailable.booleanValue() && providerAvailable && sourceAvailable;
    }

    /**
     * This function is between executeFunction and getURL and has two
     * purposes. It translates the executeFunction argument List to
     * the arguments of getURL and wraps the result in a ResponseInfo object.
     *
     * Final, because I think that you should override getURLs.
     * 
     * This function is to be called from the MediaSources implementation (which has to give itself) too
     */
    final public List getResponseInfos(MMObjectNode composer, MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        List urls         = getURLs(composer, provider, source, fragment, info);
        List result = new ArrayList();
        boolean online = isAvailable(composer, provider, source, fragment, info);
        Iterator i = urls.iterator();
        while (i.hasNext()) {
            URL url = (URL) i.next();
            result.add(new ResponseInfo(url, source, online, info));           
        }
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
}
