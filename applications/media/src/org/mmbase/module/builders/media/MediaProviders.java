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
 * @version $Id: MediaProviders.java,v 1.9 2003-01-21 17:46:24 michiel Exp $
 * @since MMBase-1.7
 */
public class MediaProviders extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(MediaProviders.class.getName());

    public final static int STATE_ON  = 1;
    public final static int STATE_OFF = 2;

    /**
     * A MediaProvider can provide one or more URL's for every source
     * @returns A List of ResponseInfo's
     */

    public List getURLs(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        List result = new ArrayList();
        // todo: consider posrel
        Iterator composers =  provider.getRelatedNodes("mediaurlcomposers").iterator();

        while (composers.hasNext()) {
            MMObjectNode composer = (MMObjectNode) composers.next();
            MediaURLComposers bul = (MediaURLComposers) composer.parent; // cast everytime, because it can be extended
            List composerurls = bul.getURLs(composer, provider, source, fragment, info);
            composerurls.removeAll(result); // remove duplicates
            result.addAll(composerurls);
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
