/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import java.util.*;

import org.mmbase.module.core.MMBaseObserver;
import org.mmbase.util.logging.*;

/**
 * Invalidates the security caches if somethings changes in the
 * security nodes. This Observer will be subscribed to all security
 * builders for this goal (in their init methods).
 *
 * @todo undoubtly, this is too crude.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CacheInvalidator.java,v 1.4 2003-11-05 15:48:03 keesj Exp $
 * @since MMBase-1.7
 */
class CacheInvalidator implements MMBaseObserver {

    private static final Logger log = Logging.getLoggerInstance(CacheInvalidator.class);

    private static CacheInvalidator instance = new CacheInvalidator();

    // this is a singleton
    static CacheInvalidator getInstance() {
        return instance;
    }
    
    private CacheInvalidator() {
    }

    private List securityCaches = new ArrayList(); // list of all security caches that must be invalidated
  
    /**
     *  A security builder can add its cache(s)
     */
    synchronized void addCache(Map c) {
        securityCaches.add(c);
    }

    // javadoc inherited
    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(machine, number, builder, ctype);
    }


    // javadoc inherited
    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(machine, number, builder, ctype);
    }

    /**
     * What happens if something changes: clear the caches
     */
    synchronized protected boolean nodeChanged(String machine, String number, String builder, String ctype) {
        log.debug("A security object has changed, invalidating all security caches");
        Iterator i = securityCaches.iterator();
        while (i.hasNext()) {
            Map c = (Map) i.next();
            c.clear();
        }
        return true;
    }

}
