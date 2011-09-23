/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.richtext;
import org.mmbase.core.event.*;
import com.opensymphony.oscache.web.ServletCacheAdministrator;
import com.opensymphony.oscache.base.Cache;
import org.mmbase.module.core.MMBaseContext;
import javax.servlet.*;

import org.mmbase.util.logging.*;

/**

 * @author Michiel Meeuwissen
 * @version $Id: Mmxf.java 35335 2009-05-21 08:14:41Z michiel $
 * @see    org.mmbase.util.transformers.XmlField
 * @since  MMBase-1.9.6
 */
public class OSCacheInvalidator implements NodeEventListener, SystemEventListener {

    private static final Logger LOG = Logging.getLoggerInstance(OSCacheInvalidator.class);

    private Cache cache;

    @Override
    public void notify(SystemEvent se) {
        LOG.info(se);
        if (se instanceof SystemEvent.Up) {
            try {
                ServletContext context =  MMBaseContext.getServletContext();
                ServletCacheAdministrator admin = ServletCacheAdministrator.getInstance(context);
                cache = admin.getAppScopeCache(context);
                LOG.info("Cache invalidator initialized with " + cache);
            } catch (NoClassDefFoundError ncdfe) {
                // probably oscache not installed, so never mind
                LOG.debug(ncdfe);
            }
        }
    }
    @Override
    public int getWeight() {
        return 0;
    }

    @Override
    public void notify(NodeEvent ne) {
        if (cache != null) {
            String key = "richtext_" + ne.getNodeNumber();
            LOG.debug("Flusing " + key);
            cache.flushEntry(key);
            cache.flushGroup(key);
        }
    }

    @Override
    public int hashCode() {
        return 13;
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof OSCacheInvalidator;
    }

}
