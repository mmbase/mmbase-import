/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.module.builders.media;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.*;
import java.util.*;


/**
 * A MediaProvider often is a host. One or more 'MediaURLComposers'
 * (or extensions) must be related to it. Those will perform the actual task of creating an URL.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MediaProviders.java,v 1.5 2003-01-03 21:35:44 michiel Exp $
 * @since MMBase-1.7
 */
public class MediaProviders extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(MediaProviders.class.getName());

    public static final String FUNCTION_URLS = "urls";

    protected List getURLs(MMObjectNode provider, List arguments) {
        List result = new ArrayList();
        // todo: consider posrel
        Iterator composers =  provider.getRelatedNodes("mediaurlcomposers").iterator();

        arguments.add(0, provider);
        while (composers.hasNext()) {
            MMObjectNode composer = (MMObjectNode) composers.next();
            result.add(composer.getFunctionValue(MediaURLComposers.FUNCTION_URL, arguments));
        }
        arguments.remove(0);

        return result;

    }
       
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.debug("Executing function " + function + " on node " + node.getNumber() + " with argument " + args);
        }
        
        if (function.equals("info")) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, "info", empty);
            info.put(FUNCTION_URLS, "(source, fragment, info) A list of all possible URLs to this provider/source/fragment (Really MediaURLComposer.ResponseInfo's)");
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }   
        } else if (function.equals(FUNCTION_URLS)) {
            return getURLs(node, args);
        }
        return super.executeFunction(node, function, args);
    }
}
