/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import java.util.*;

import org.mmbase.core.event.*;
import org.mmbase.util.logging.*;

/**
 * Invalidates the security caches if somethings changes in the
 * security nodes. This Observer will be subscribed to all security
 * builders for this goal (in their init methods).
 *
 * @todo undoubtly, this is too crude.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CacheInvalidator.java,v 1.9 2006-10-03 13:25:04 michiel Exp $
 * @since MMBase-1.7
 */
class CacheInvalidator implements NodeEventListener, RelationEventListener {

    private static final Logger log = Logging.getLoggerInstance(CacheInvalidator.class);

    private static CacheInvalidator instance = new CacheInvalidator();
    private final Timer timer = new Timer(true);

    // this is a singleton
    static CacheInvalidator getInstance() {
        return instance;
    }

    private CacheInvalidator() {
    }

    private List<Map> securityCaches = new ArrayList(); // list of all security caches that must be invalidated

    /**
     *  A security builder can add its cache(s)
     */
    synchronized void addCache(Map c) {
        securityCaches.add(c);
    }


    protected boolean invalidateScheduled = false;


    /**
     * What happens if something changes: clear the caches
     * @inheritDoc
     */
    public void notify(NodeEvent event) {
        if (log.isServiceEnabled()) {
            log.service("A security object " + event.getNodeNumber() + " (" + event.getBuilderName() + ") has changed, invalidating all security caches");
        }
        invalidate();
    }
    public void notify(RelationEvent event) {
        if (log.isServiceEnabled()) {
            log.service("A security relation (" + event + ") has changed, invalidating all security caches");
        }
        invalidate();
    }

    protected void invalidate() {

        if (! invalidateScheduled) {
            invalidateScheduled = true;
            timer.schedule(new TimerTask() {
                    public void run() {
                        log.service("Invalidating all security caches now.");
                        synchronized(CacheInvalidator.this) {
                            invalidateScheduled = false;
                            for (Map c : CacheInvalidator.this.securityCaches) {
                                c.clear();
                            }
                        }
                    }
                }, 5000);
        }
    }

}
