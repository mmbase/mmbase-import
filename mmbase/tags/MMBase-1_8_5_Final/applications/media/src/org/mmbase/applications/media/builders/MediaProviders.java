/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.media.builders;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.applications.media.urlcomposers.URLComposerFactory;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.lang.reflect.Method;

/**
 * A MediaProvider builder describes a service that offers a media service. The mediaprovider
 * is related to the mediasources that are available on the mediaprovider. A mediaprovider can
 * be online/offline.
 *
* @author Michiel Meeuwissen
 * @version $Id: MediaProviders.java,v 1.13.2.1 2007-10-02 09:44:35 michiel Exp $
 * @since MMBase-1.7
 */
public class MediaProviders extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(MediaProviders.class);

    public final static int STATE_ON  = 1;
    public final static int STATE_OFF = 2;

    private URLComposerFactory urlComposerFactory;

    public boolean init() {
        if (super.init()) {
            try {
                String clazz = getInitParameter("URLComposerFactory");
                if (clazz == null) clazz = "org.mmbase.applications.media.urlcomposers.URLComposerFactory";
                Method m = Class.forName(clazz).getMethod("getInstance", (Class[])null);
                urlComposerFactory = (URLComposerFactory) m.invoke(null, (Object[])null);
                return true;
            } catch (Exception e) {
                log.error("Could not get URLComposerFactory because: " + e.toString());
                return false;
            }
        }
        return false;

    }


    /**
     * A MediaProvider can provide one or more URL's for every source
     * @return A List of URLComposer's
     */

    protected List getURLs(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info, List urls, Set cacheExpireObjects) {
        return urlComposerFactory.createURLComposers(provider, source, fragment, info, urls, cacheExpireObjects);
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

    protected String getNodeGUIIndicator(MMObjectNode node, org.mmbase.util.functions.Parameters params) {
        return node.getStringValue("name") + " " + node.getStringValue("protocol") + "://" + node.getStringValue("host") + node.getStringValue("rootpath");
    }
}
