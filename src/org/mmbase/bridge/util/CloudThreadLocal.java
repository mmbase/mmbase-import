/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util;

import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ThreadLocal to store an MMBase Bridge Cloud
 * @since MMBase-1.9
 * @version $Id: CloudThreadLocal.java,v 1.2 2008-07-17 17:17:08 michiel Exp $
 */
public class CloudThreadLocal {

    private static final Logger log = Logging.getLoggerInstance(CloudThreadLocal.class);

    /**
     * A ThreadLocal maintaining current cloud for the given execution thread.
     */
    private static final ThreadLocal<Cloud> context = new ThreadLocal<Cloud>();

    public static Cloud currentCloud() {
        return context.get();
    }

    /**
     * Associates the given cloud with the current thread of execution.
     *
     * @param cloud The cloud to bind.
     */
    public static void bind(Cloud cloud) {
        cleanupAnyOrphanedCloud();
        doBind( cloud );
    }

    /**
     * Unassociate a previously bound cloud from the current thread of execution.
     *
     * @return The cloud which was unbound.
     */
    public static Cloud unbind() {
        return doUnbind();
    }

    private static void cleanupAnyOrphanedCloud() {
        Cloud orphan = doUnbind();
        if ( orphan != null ) {
            log.warn( "Already cloud bound on call to bind(); make sure you clean up your cloud!" );
        }
    }

    private static void doBind(Cloud cloud) {
        if ( cloud != null ) {
            context.set( cloud );
        }
    }

    private static Cloud doUnbind() {
        Cloud cloud = context.get();
        if ( cloud != null ) {
            context.set( null );
        }
        return cloud;
    }

}
